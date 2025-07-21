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
import solid.backend.docker.dto.ExecutionStatusDto;
import solid.backend.docker.entity.DockerExecution;
import solid.backend.docker.exception.DockerExecutionException;
import solid.backend.jpaRepository.DockerExecutionJpaRepository;
import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.jpaRepository.ContainerJpaRepository;
import solid.backend.jpaRepository.MemberRepository;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
}