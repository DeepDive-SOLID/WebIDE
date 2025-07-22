package solid.backend.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.container.exception.ContainerNotFoundException;
import solid.backend.config.DockerSecurityConfig;
import solid.backend.docker.constant.DockerConstants;
import solid.backend.docker.dto.CodeExecutionRequestDto;
import solid.backend.docker.dto.CodeExecutionResponseDto;
import solid.backend.docker.dto.ContainerStatisticsDto;
import solid.backend.docker.dto.ContainerSyncRequestDto;
import solid.backend.docker.dto.ContainerSyncResponseDto;
import solid.backend.docker.dto.ExecutionStatusDto;
import solid.backend.docker.dto.FileSystemNodeDto;
import solid.backend.docker.dto.LanguageStatisticsDto;
import solid.backend.docker.dto.LanguageStatisticsResponseDto;
import solid.backend.docker.entity.DockerExecution;
import solid.backend.docker.exception.DockerExecutionException;
import solid.backend.jpaRepository.DockerExecutionJpaRepository;
import solid.backend.entity.Code;
import solid.backend.entity.CodeFile;
import solid.backend.entity.Container;
import solid.backend.entity.Directory;
import solid.backend.entity.Member;
import solid.backend.jpaRepository.CodeFileRepository;
import solid.backend.jpaRepository.CodeRepository;
import solid.backend.jpaRepository.ContainerJpaRepository;
import solid.backend.jpaRepository.DirectoryRepository;
import solid.backend.jpaRepository.MemberRepository;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 도커 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DockerServiceImpl implements DockerService {
    
    /** 도커 클라이언트 */
    private final DockerClient dockerClient;
    /** 도커 실행 기록 레포지토리 */
    private final DockerExecutionJpaRepository dockerExecutionRepository;
    /** 컨테이너 레포지토리 */
    private final ContainerJpaRepository containerRepository;
    /** 회원 레포지토리 */
    private final MemberRepository memberRepository;
    /** 도커 보안 설정 */
    private final DockerSecurityConfig dockerSecurityConfig;
    /** 디렉토리 레포지토리 */
    private final DirectoryRepository directoryRepository;
    /** 코드 파일 레포지토리 */
    private final CodeFileRepository codeFileRepository;
    /** 코드 레포지토리 */
    private final CodeRepository codeRepository;
    
    @Override
    @Transactional
    public CodeExecutionResponseDto executeCode(String memberId, CodeExecutionRequestDto request) {
        // 권한 확인
        Container container = containerRepository.findById(request.getContainerId())
                .orElseThrow(() -> new ContainerNotFoundException(DockerConstants.ERROR_CONTAINER_NOT_FOUND));
                
        if (!containerRepository.isTeamMember(request.getContainerId(), memberId)) {
            throw new DockerExecutionException("컨테이너에 접근 권한이 없습니다");
        }
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));
        
        // 코드 보안 검증
        dockerSecurityConfig.validateCode(request.getCode(), request.getLanguage());
        
        // 실행 기록 생성
        DockerExecution execution = DockerExecution.builder()
                .container(container)
                .member(member)
                .language(request.getLanguage())
                .filePath(request.getFilePath())
                .code(request.getCode())
                .input(request.getInput())
                .status(DockerConstants.STATUS_PENDING)
                .build();
                
        execution = dockerExecutionRepository.save(execution);
        
        try {
            // 도커 컨테이너에서 코드 실행
            executeInDocker(execution);
            
            return convertToResponseDto(execution);
        } catch (Exception e) {
            log.error("코드 실행 실패: ", e);
            execution.fail(e.getMessage());
            dockerExecutionRepository.save(execution);
            throw new DockerExecutionException(DockerConstants.ERROR_EXECUTION_FAILED, e);
        }
    }
    
    /**
     * 도커 컨테이너에서 코드 실행
     * @param execution 실행 기록
     * @throws Exception 실행 중 오류 발생 시
     */
    private void executeInDocker(DockerExecution execution) throws Exception {
        String dockerImage = getDockerImage(execution.getLanguage());
        String containerId = null;
        
        try {
            // 임시 파일 생성
            String fileName = DockerConstants.CODE_FILE_PREFIX + UUID.randomUUID() + 
                             getFileExtension(execution.getLanguage());
            Path tempFile = Files.createTempFile(fileName, null);
            Files.write(tempFile, execution.getCode().getBytes(StandardCharsets.UTF_8));
            
            // 도커 컨테이너 생성
            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withMemory(DockerConstants.MEMORY_LIMIT)
                    .withCpuShares((int) DockerConstants.CPU_SHARES)
                    .withBinds(new Bind(tempFile.toAbsolutePath().toString(), 
                              new Volume(DockerConstants.CONTAINER_WORKSPACE + "/" + fileName)));
            
            CreateContainerResponse containerResponse = dockerClient.createContainerCmd(dockerImage)
                    .withHostConfig(hostConfig)
                    .withWorkingDir(DockerConstants.CONTAINER_WORKSPACE)
                    .exec();
                    
            containerId = containerResponse.getId();
            
            // 컨테이너 시작
            dockerClient.startContainerCmd(containerId).exec();
            execution.updateStatus(DockerConstants.STATUS_RUNNING);
            dockerExecutionRepository.save(execution);
            
            // 코드 실행
            String command = getExecutionCommand(execution.getLanguage(), fileName);
            
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("sh", "-c", command)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();
                    
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            
            long startTime = System.currentTimeMillis();
            
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(stdout, stderr))
                    .awaitCompletion(DockerConstants.EXECUTION_TIMEOUT, TimeUnit.SECONDS);
                    
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 결과 저장
            String output = stdout.toString(StandardCharsets.UTF_8);
            String errorOutput = stderr.toString(StandardCharsets.UTF_8);
            
            // 메모리 사용량 조회 (간단한 구현)
            Long memoryUsed = getContainerMemoryUsage(containerId);
            
            execution.complete(output, errorOutput, executionTime, memoryUsed);
            
            // 임시 파일 삭제
            Files.deleteIfExists(tempFile);
            
        } finally {
            // 컨테이너 정리
            if (containerId != null) {
                try {
                    dockerClient.stopContainerCmd(containerId).exec();
                    dockerClient.removeContainerCmd(containerId).exec();
                } catch (Exception e) {
                    log.error("컨테이너 정리 실패: ", e);
                }
            }
        }
    }
    
    /**
     * 언어별 도커 이미지 반환
     * @param language 프로그래밍 언어
     * @return 도커 이미지 이름
     */
    private String getDockerImage(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return DockerConstants.PYTHON_IMAGE;
            case "java":
                return DockerConstants.JAVA_IMAGE;
            case "javascript":
                return DockerConstants.NODE_IMAGE;
            case "cpp":
            case "c":
                return DockerConstants.CPP_IMAGE;
            default:
                throw new DockerExecutionException(DockerConstants.ERROR_UNSUPPORTED_LANGUAGE);
        }
    }
    
    /**
     * 언어별 파일 확장자 반환
     * @param language 프로그래밍 언어
     * @return 파일 확장자
     */
    private String getFileExtension(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return ".py";
            case "java":
                return ".java";
            case "javascript":
                return ".js";
            case "cpp":
                return ".cpp";
            case "c":
                return ".c";
            default:
                return ".txt";
        }
    }
    
    /**
     * 언어별 실행 명령어 반환
     * @param language 프로그래밍 언어
     * @param fileName 파일명
     * @return 실행 명령어
     */
    private String getExecutionCommand(String language, String fileName) {
        switch (language.toLowerCase()) {
            case "python":
                return "python " + fileName;
            case "java":
                return "javac " + fileName + " && java " + fileName.replace(".java", "");
            case "javascript":
                return "node " + fileName;
            case "cpp":
                return "g++ " + fileName + " -o output && ./output";
            case "c":
                return "gcc " + fileName + " -o output && ./output";
            default:
                throw new DockerExecutionException(DockerConstants.ERROR_UNSUPPORTED_LANGUAGE);
        }
    }
    
    /**
     * 컨테이너 메모리 사용량 조회
     * @param containerId 컨테이너 ID
     * @return 메모리 사용량 (바이트)
     */
    private Long getContainerMemoryUsage(String containerId) {
        try {
            // 실제 구현에서는 Docker stats API 사용
            return 50 * 1024 * 1024L; // 임시로 50MB 반환
        } catch (Exception e) {
            log.error("메모리 사용량 조회 실패: ", e);
            return null;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ExecutionStatusDto getExecutionStatus(Long executionId) {
        DockerExecution execution = dockerExecutionRepository.findById(executionId)
                .orElseThrow(() -> new DockerExecutionException("실행 기록을 찾을 수 없습니다"));
                
        return ExecutionStatusDto.builder()
                .executionId(execution.getExecutionId())
                .status(execution.getStatus())
                .message(getStatusMessage(execution.getStatus()))
                .progress(getProgress(execution.getStatus()))
                .build();
    }
    
    /**
     * 상태별 메시지 반환
     */
    private String getStatusMessage(String status) {
        switch (status) {
            case DockerConstants.STATUS_PENDING:
                return "실행 대기 중";
            case DockerConstants.STATUS_RUNNING:
                return "실행 중";
            case DockerConstants.STATUS_COMPLETED:
                return "실행 완료";
            case DockerConstants.STATUS_ERROR:
                return "실행 오류";
            case DockerConstants.STATUS_TIMEOUT:
                return "시간 초과";
            default:
                return "알 수 없는 상태";
        }
    }
    
    /**
     * 상태별 진행률 반환
     */
    private Integer getProgress(String status) {
        switch (status) {
            case DockerConstants.STATUS_PENDING:
                return 0;
            case DockerConstants.STATUS_RUNNING:
                return 50;
            case DockerConstants.STATUS_COMPLETED:
            case DockerConstants.STATUS_ERROR:
            case DockerConstants.STATUS_TIMEOUT:
                return 100;
            default:
                return 0;
        }
    }
    
    @Override
    @Transactional
    public void stopExecution(Long executionId) {
        DockerExecution execution = dockerExecutionRepository.findById(executionId)
                .orElseThrow(() -> new DockerExecutionException("실행 기록을 찾을 수 없습니다"));
                
        if (DockerConstants.STATUS_RUNNING.equals(execution.getStatus())) {
            execution.updateStatus(DockerConstants.STATUS_ERROR);
            execution.fail("사용자에 의해 중지됨");
            dockerExecutionRepository.save(execution);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CodeExecutionResponseDto> getExecutionHistory(Long containerId, String memberId) {
        List<DockerExecution> executions = dockerExecutionRepository
                .findByContainerContainerIdAndMemberMemberIdOrderByCreatedAtDesc(containerId, memberId);
                
        return executions.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteExecution(Long executionId, String memberId) {
        DockerExecution execution = dockerExecutionRepository.findById(executionId)
                .orElseThrow(() -> new DockerExecutionException("실행 기록을 찾을 수 없습니다"));
                
        if (!execution.getMember().getMemberId().equals(memberId)) {
            throw new DockerExecutionException("삭제 권한이 없습니다");
        }
        
        dockerExecutionRepository.delete(execution);
    }
    
    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("python", "java", "javascript", "cpp", "c");
    }
    
    /**
     * Entity를 DTO로 변환
     * @param execution 실행 기록 엔티티
     * @return 응답 DTO
     */
    private CodeExecutionResponseDto convertToResponseDto(DockerExecution execution) {
        return CodeExecutionResponseDto.builder()
                .executionId(execution.getExecutionId())
                .language(execution.getLanguage())
                .code(execution.getCode())
                .input(execution.getInput())
                .output(execution.getOutput())
                .errorOutput(execution.getErrorOutput())
                .status(execution.getStatus())
                .executionTime(execution.getExecutionTime())
                .memoryUsed(execution.getMemoryUsed())
                .createdAt(execution.getCreatedAt())
                .completedAt(execution.getCompletedAt())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ContainerStatisticsDto getContainerStatistics(Long containerId, String memberId) {
        // 권한 확인
        if (!containerRepository.isTeamMember(containerId, memberId)) {
            throw new DockerExecutionException("컨테이너에 접근 권한이 없습니다");
        }
        
        // 컨테이너의 모든 실행 기록 조회
        List<DockerExecution> executions = dockerExecutionRepository
                .findByContainerContainerIdOrderByCreatedAtDesc(containerId);
        
        if (executions.isEmpty()) {
            return ContainerStatisticsDto.builder()
                    .containerId(containerId)
                    .totalExecutions(0L)
                    .successfulExecutions(0L)
                    .failedExecutions(0L)
                    .averageExecutionTime(0.0)
                    .averageMemoryUsage(0.0)
                    .generatedAt(LocalDateTime.now())
                    .build();
        }
        
        // 통계 계산
        long totalExecutions = executions.size();
        long successfulExecutions = executions.stream()
                .filter(e -> DockerConstants.STATUS_COMPLETED.equals(e.getStatus()))
                .count();
        long failedExecutions = executions.stream()
                .filter(e -> DockerConstants.STATUS_ERROR.equals(e.getStatus()) || 
                            DockerConstants.STATUS_TIMEOUT.equals(e.getStatus()))
                .count();
        
        // 완료된 실행들의 시간 통계
        List<DockerExecution> completedExecutions = executions.stream()
                .filter(e -> DockerConstants.STATUS_COMPLETED.equals(e.getStatus()) && 
                            e.getExecutionTime() != null)
                .collect(Collectors.toList());
        
        double avgExecutionTime = completedExecutions.stream()
                .mapToLong(DockerExecution::getExecutionTime)
                .average()
                .orElse(0.0);
        
        long maxExecutionTime = completedExecutions.stream()
                .mapToLong(DockerExecution::getExecutionTime)
                .max()
                .orElse(0L);
        
        long minExecutionTime = completedExecutions.stream()
                .mapToLong(DockerExecution::getExecutionTime)
                .min()
                .orElse(0L);
        
        // 메모리 통계
        double avgMemoryUsage = executions.stream()
                .filter(e -> e.getMemoryUsed() != null)
                .mapToLong(DockerExecution::getMemoryUsed)
                .average()
                .orElse(0.0);
        
        long maxMemoryUsage = executions.stream()
                .filter(e -> e.getMemoryUsed() != null)
                .mapToLong(DockerExecution::getMemoryUsed)
                .max()
                .orElse(0L);
        
        // 기간 계산
        LocalDateTime periodStart = executions.get(executions.size() - 1).getCreatedAt();
        LocalDateTime periodEnd = executions.get(0).getCreatedAt();
        
        return ContainerStatisticsDto.builder()
                .containerId(containerId)
                .totalExecutions(totalExecutions)
                .successfulExecutions(successfulExecutions)
                .failedExecutions(failedExecutions)
                .averageExecutionTime(avgExecutionTime)
                .maxExecutionTime(maxExecutionTime)
                .minExecutionTime(minExecutionTime)
                .averageMemoryUsage(avgMemoryUsage)
                .maxMemoryUsage(maxMemoryUsage)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .generatedAt(LocalDateTime.now())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public LanguageStatisticsResponseDto getLanguageStatistics(Long containerId, String memberId) {
        // 권한 확인
        if (!containerRepository.isTeamMember(containerId, memberId)) {
            throw new DockerExecutionException("컨테이너에 접근 권한이 없습니다");
        }
        
        // 컨테이너의 모든 실행 기록 조회
        List<DockerExecution> executions = dockerExecutionRepository
                .findByContainerContainerIdOrderByCreatedAtDesc(containerId);
        
        if (executions.isEmpty()) {
            return LanguageStatisticsResponseDto.builder()
                    .containerId(containerId)
                    .languageStatistics(new ArrayList<>())
                    .totalExecutions(0L)
                    .generatedAt(LocalDateTime.now())
                    .build();
        }
        
        long totalExecutions = executions.size();
        
        // 언어별 그룹화
        Map<String, List<DockerExecution>> executionsByLanguage = executions.stream()
                .collect(Collectors.groupingBy(DockerExecution::getLanguage));
        
        // 언어별 통계 계산
        List<LanguageStatisticsDto> languageStats = executionsByLanguage.entrySet().stream()
                .map(entry -> {
                    String language = entry.getKey();
                    List<DockerExecution> langExecutions = entry.getValue();
                    
                    long executionCount = langExecutions.size();
                    double percentage = (executionCount * 100.0) / totalExecutions;
                    
                    long successCount = langExecutions.stream()
                            .filter(e -> DockerConstants.STATUS_COMPLETED.equals(e.getStatus()))
                            .count();
                    
                    long failureCount = langExecutions.stream()
                            .filter(e -> DockerConstants.STATUS_ERROR.equals(e.getStatus()) || 
                                        DockerConstants.STATUS_TIMEOUT.equals(e.getStatus()))
                            .count();
                    
                    double avgExecutionTime = langExecutions.stream()
                            .filter(e -> DockerConstants.STATUS_COMPLETED.equals(e.getStatus()) && 
                                        e.getExecutionTime() != null)
                            .mapToLong(DockerExecution::getExecutionTime)
                            .average()
                            .orElse(0.0);
                    
                    double avgMemoryUsage = langExecutions.stream()
                            .filter(e -> e.getMemoryUsed() != null)
                            .mapToLong(DockerExecution::getMemoryUsed)
                            .average()
                            .orElse(0.0);
                    
                    String lastExecutedAt = langExecutions.get(0).getCreatedAt().toString();
                    
                    return LanguageStatisticsDto.builder()
                            .language(language)
                            .executionCount(executionCount)
                            .percentage(Math.round(percentage * 100.0) / 100.0) // 소수점 2자리
                            .successCount(successCount)
                            .failureCount(failureCount)
                            .averageExecutionTime(Math.round(avgExecutionTime * 100.0) / 100.0)
                            .averageMemoryUsage(Math.round(avgMemoryUsage * 100.0) / 100.0)
                            .lastExecutedAt(lastExecutedAt)
                            .build();
                })
                .sorted((a, b) -> b.getExecutionCount().compareTo(a.getExecutionCount())) // 실행 횟수 내림차순
                .collect(Collectors.toList());
        
        return LanguageStatisticsResponseDto.builder()
                .containerId(containerId)
                .languageStatistics(languageStats)
                .totalExecutions(totalExecutions)
                .generatedAt(LocalDateTime.now())
                .build();
    }
    
    @Override
    @Transactional
    public ContainerSyncResponseDto syncContainerFiles(String memberId, ContainerSyncRequestDto request) {
        LocalDateTime startTime = LocalDateTime.now();
        String syncId = UUID.randomUUID().toString();
        
        try {
            // 권한 확인
            if (!containerRepository.isTeamMember(request.getContainerId(), memberId)) {
                throw new DockerExecutionException("컨테이너에 접근 권한이 없습니다");
            }
            
            Container container = containerRepository.findById(request.getContainerId())
                    .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다"));
            
            // Docker 컨테이너 이름 설정
            String dockerContainerName = request.getDockerContainerName() != null ? 
                    request.getDockerContainerName() : 
                    "webide-container-" + request.getContainerId();
            
            // Docker 컨테이너 생성 또는 확인
            String dockerContainerId = ensureDockerContainer(dockerContainerName, request.getWorkingDirectory());
            
            // 파일 시스템 동기화
            SyncResult syncResult = syncFileSystem(container, dockerContainerId, request);
            
            LocalDateTime endTime = LocalDateTime.now();
            long elapsedTime = java.time.Duration.between(startTime, endTime).toMillis();
            
            return ContainerSyncResponseDto.builder()
                    .syncId(syncId)
                    .containerId(request.getContainerId())
                    .dockerContainerName(dockerContainerName)
                    .dockerContainerId(dockerContainerId)
                    .status(syncResult.failedFiles.isEmpty() ? 
                            ContainerSyncResponseDto.SyncStatus.SUCCESS : 
                            ContainerSyncResponseDto.SyncStatus.PARTIAL_SUCCESS)
                    .syncedFileCount(syncResult.syncedFileCount)
                    .syncedDirectoryCount(syncResult.syncedDirectoryCount)
                    .failedFiles(syncResult.failedFiles)
                    .startTime(startTime)
                    .endTime(endTime)
                    .elapsedTime(elapsedTime)
                    .build();
                    
        } catch (Exception e) {
            log.error("파일 동기화 실패: {}", e.getMessage(), e);
            return ContainerSyncResponseDto.builder()
                    .syncId(syncId)
                    .containerId(request.getContainerId())
                    .status(ContainerSyncResponseDto.SyncStatus.FAILED)
                    .errorMessage(e.getMessage())
                    .startTime(startTime)
                    .endTime(LocalDateTime.now())
                    .build();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public FileSystemNodeDto getContainerFileTree(Long containerId, String memberId) {
        // 권한 확인
        if (!containerRepository.isTeamMember(containerId, memberId)) {
            throw new DockerExecutionException("컨테이너에 접근 권한이 없습니다");
        }
        
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ContainerNotFoundException("컨테이너를 찾을 수 없습니다"));
        
        // 루트 디렉토리 생성
        FileSystemNodeDto root = FileSystemNodeDto.builder()
                .name(container.getContainerName())
                .path("/")
                .type(FileSystemNodeDto.NodeType.DIRECTORY)
                .children(new ArrayList<>())
                .build();
        
        // 디렉토리 구조 조회
        List<Directory> directories = directoryRepository.findByContainer_ContainerId(containerId);
        
        // 디렉토리별로 파일 트리 구성
        for (Directory directory : directories) {
            FileSystemNodeDto dirNode = buildDirectoryNode(directory);
            root.getChildren().add(dirNode);
        }
        
        return root;
    }
    
    @Override
    public boolean isDockerContainerExists(String containerName) {
        try {
            List<com.github.dockerjava.api.model.Container> containers = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withNameFilter(Arrays.asList(containerName))
                    .exec();
            
            return !containers.isEmpty();
        } catch (Exception e) {
            log.error("Docker 컨테이너 확인 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 디렉토리 노드 구성
     */
    private FileSystemNodeDto buildDirectoryNode(Directory directory) {
        FileSystemNodeDto dirNode = FileSystemNodeDto.builder()
                .name(directory.getDirectoryName())
                .path(directory.getDirectoryRoot())
                .type(FileSystemNodeDto.NodeType.DIRECTORY)
                .children(new ArrayList<>())
                .build();
        
        // 디렉토리의 파일들 조회
        List<CodeFile> files = codeFileRepository.findByDirectory_DirectoryId(directory.getDirectoryId());
        for (CodeFile file : files) {
            FileSystemNodeDto fileNode = FileSystemNodeDto.builder()
                    .name(file.getCodeFileName())
                    .path(file.getCodeFilePath())
                    .type(FileSystemNodeDto.NodeType.FILE)
                    .size(0L) // 실제 파일 크기는 별도 계산 필요
                    .build();
            dirNode.getChildren().add(fileNode);
        }
        
        // 디렉토리의 코드들 조회
        List<Code> codes = codeRepository.findByDirectory_DirectoryId(directory.getDirectoryId());
        for (Code code : codes) {
            FileSystemNodeDto codeNode = FileSystemNodeDto.builder()
                    .name(code.getCodeName())
                    .path(directory.getDirectoryRoot() + "/" + code.getCodeName())
                    .type(FileSystemNodeDto.NodeType.FILE)
                    .content(code.getCodeText())
                    .size((long) (code.getCodeText() != null ? code.getCodeText().length() : 0))
                    .build();
            dirNode.getChildren().add(codeNode);
        }
        
        return dirNode;
    }
    
    /**
     * Docker 컨테이너 확인 및 생성
     */
    private String ensureDockerContainer(String containerName, String workingDirectory) {
        try {
            // 기존 컨테이너 확인
            List<com.github.dockerjava.api.model.Container> containers = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withNameFilter(Arrays.asList(containerName))
                    .exec();
            
            if (!containers.isEmpty()) {
                // 컨테이너가 실행 중이 아니면 시작
                com.github.dockerjava.api.model.Container container = containers.get(0);
                if (!container.getState().equals("running")) {
                    dockerClient.startContainerCmd(container.getId()).exec();
                }
                return container.getId();
            }
            
            // 새 컨테이너 생성
            CreateContainerResponse containerResponse = dockerClient.createContainerCmd("ubuntu:latest")
                    .withName(containerName)
                    .withWorkingDir(workingDirectory)
                    .withCmd("/bin/bash")
                    .withTty(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withHostConfig(HostConfig.newHostConfig()
                            .withMemory(512L * 1024 * 1024) // 512MB
                            .withCpuShares(512)) // 0.5 CPU
                    .exec();
            
            // 컨테이너 시작
            dockerClient.startContainerCmd(containerResponse.getId()).exec();
            
            // 작업 디렉토리 생성
            ExecCreateCmdResponse execCreateResponse = dockerClient.execCreateCmd(containerResponse.getId())
                    .withCmd("mkdir", "-p", workingDirectory)
                    .exec();
            
            dockerClient.execStartCmd(execCreateResponse.getId())
                    .exec(new ExecStartResultCallback())
                    .awaitCompletion();
            
            return containerResponse.getId();
            
        } catch (Exception e) {
            throw new DockerExecutionException("Docker 컨테이너 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 파일 시스템 동기화
     */
    private SyncResult syncFileSystem(Container container, String dockerContainerId, ContainerSyncRequestDto request) {
        SyncResult result = new SyncResult();
        
        // 디렉토리 구조 조회
        List<Directory> directories = directoryRepository.findByContainer_ContainerId(container.getContainerId());
        
        for (Directory directory : directories) {
            try {
                // 디렉토리 생성
                String dirPath = request.getWorkingDirectory() + directory.getDirectoryRoot();
                createDirectoryInContainer(dockerContainerId, dirPath);
                result.syncedDirectoryCount++;
                
                // 디렉토리의 파일들 동기화
                syncDirectoryFiles(directory, dockerContainerId, request.getWorkingDirectory(), result);
                
            } catch (Exception e) {
                log.error("디렉토리 동기화 실패: {}", directory.getDirectoryName(), e);
                result.failedFiles.add(directory.getDirectoryRoot());
            }
        }
        
        return result;
    }
    
    /**
     * 디렉토리 내 파일 동기화
     */
    private void syncDirectoryFiles(Directory directory, String dockerContainerId, 
                                   String workingDirectory, SyncResult result) {
        // Code 엔티티 파일들 동기화
        List<Code> codes = codeRepository.findByDirectory_DirectoryId(directory.getDirectoryId());
        for (Code code : codes) {
            try {
                String filePath = workingDirectory + directory.getDirectoryRoot() + "/" + code.getCodeName();
                copyFileToContainer(dockerContainerId, filePath, code.getCodeText());
                result.syncedFileCount++;
            } catch (Exception e) {
                log.error("파일 동기화 실패: {}", code.getCodeName(), e);
                result.failedFiles.add(code.getCodeName());
            }
        }
        
        // CodeFile 엔티티는 실제 파일 내용이 없으므로 빈 파일 생성
        List<CodeFile> files = codeFileRepository.findByDirectory_DirectoryId(directory.getDirectoryId());
        for (CodeFile file : files) {
            try {
                String filePath = workingDirectory + file.getCodeFilePath();
                copyFileToContainer(dockerContainerId, filePath, "");
                result.syncedFileCount++;
            } catch (Exception e) {
                log.error("파일 동기화 실패: {}", file.getCodeFileName(), e);
                result.failedFiles.add(file.getCodeFileName());
            }
        }
    }
    
    /**
     * 컨테이너에 디렉토리 생성
     */
    private void createDirectoryInContainer(String containerId, String path) {
        try {
            ExecCreateCmdResponse execCreateResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("mkdir", "-p", path)
                    .exec();
            
            dockerClient.execStartCmd(execCreateResponse.getId())
                    .exec(new ExecStartResultCallback())
                    .awaitCompletion();
        } catch (Exception e) {
            throw new DockerExecutionException("디렉토리 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 컨테이너로 파일 복사
     */
    private void copyFileToContainer(String containerId, String targetPath, String content) {
        try {
            // 임시 파일 생성
            Path tempFile = Files.createTempFile("sync_", ".tmp");
            Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8));
            
            // 파일을 컨테이너로 복사
            dockerClient.copyArchiveToContainerCmd(containerId)
                    .withHostResource(tempFile.toString())
                    .withRemotePath(targetPath)
                    .exec();
            
            // 임시 파일 삭제
            Files.deleteIfExists(tempFile);
            
        } catch (Exception e) {
            throw new DockerExecutionException("파일 복사 실패: " + e.getMessage());
        }
    }
    
    /**
     * 동기화 결과 내부 클래스
     */
    private static class SyncResult {
        int syncedFileCount = 0;
        int syncedDirectoryCount = 0;
        List<String> failedFiles = new ArrayList<>();
    }
}