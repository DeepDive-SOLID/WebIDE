package solid.backend.Progress.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.Progress.dto.ProgressDto;
import solid.backend.Progress.dto.ProgressListDto;
import solid.backend.Progress.dto.QuestionProgressDto;
import solid.backend.Progress.repository.ProgressQueryRepository;
import solid.backend.entity.*;
import solid.backend.jpaRepository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService{
    private final ProgressRepository progressRepository;
    private final DirectoryRepository directoryRepository;
    private final TeamUserRepository teamUserRepository;
    private final ProgressQueryRepository progressQueryRepository;
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final MemberRepository memberRepository;
    private final CodeFileRepository codeFileRepository;
    private final ContainerRepository containerRepository;
    

    /**
     * 설명: 디렉터리 속 진행률 조회
     * @param directoryId
     * @return List<ProgressListDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProgressListDto> getProgressList(Integer directoryId) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        
        // 해당 디렉터리의 팀 조회
        Team team = directory.getTeam();
        List<TeamUser> teamUsers = teamUserRepository.findByTeam_TeamId(team.getTeamId());
        
        List<ProgressListDto> result = new ArrayList<>();
        
        for (TeamUser teamUser : teamUsers) {
            Member member = teamUser.getMember();
            
            // 해당 디렉터리와 팀 유저의 모든 언어별 진행률 조회
            List<Progress> progressListByLang = progressRepository.findAllByDirectoryAndTeamUser(directory, teamUser);
            
            if (progressListByLang.isEmpty()) {
                // 진행률이 없는 경우에도 멤버 정보는 표시
                ProgressListDto dto = new ProgressListDto();
                dto.setMemberId(member.getMemberId());
                dto.setMemberName(member.getMemberName());
                dto.setProgressComplete(0);
                dto.setLanguage("N/A");
                result.add(dto);
            } else {
                // 각 언어별로 별도의 DTO 생성
                for (Progress progress : progressListByLang) {
                    ProgressListDto dto = new ProgressListDto();
                    dto.setMemberId(member.getMemberId());
                    dto.setMemberName(member.getMemberName());
                    dto.setProgressComplete(progress.getProgressComplete());
                    
                    // 언어 포맷팅
                    String lang = progress.getLanguage();
                    if (lang != null) {
                        switch (lang.toLowerCase()) {
                            case "javascript":
                                dto.setLanguage("JavaScript");
                                break;
                            case "java":
                                dto.setLanguage("Java");
                                break;
                            case "python":
                                dto.setLanguage("Python");
                                break;
                            default:
                                dto.setLanguage(lang);
                        }
                    } else {
                        dto.setLanguage("N/A");
                    }
                    
                    result.add(dto);
                }
            }
        }
        
        return result;
    }
    

    /**
     * 설명: 문제 진행률 계산
     * @param progressDto
     */
    @Override
    @Transactional
    public void updateProgress(ProgressDto progressDto) {
        // 디렉토리 ID + 팀 유저 ID + 언어 조합으로 기존 진행률 찾기
        Directory directory = directoryRepository.findById(progressDto.getDirectoryId())
                .orElseThrow(() -> new RuntimeException("Directory not found"));

        TeamUser teamUser = teamUserRepository.findById(progressDto.getTeamUserId())
                .orElseThrow(() -> new RuntimeException("TeamUser not found"));

        String language = progressDto.getLanguage();
        if (language == null || language.isEmpty()) {
            // 언어가 없으면 기존 방식대로 처리
            Optional<Progress> progressOptional = progressRepository.findByDirectoryAndTeamUser(directory, teamUser);
            if (progressOptional.isPresent()) {
                Progress progress = progressOptional.get();
                progress.setProgressComplete(progressDto.getProgressComplete());
                progressRepository.save(progress);
            } else {
                Progress progress = new Progress();
                progress.setDirectory(directory);
                progress.setTeamUser(teamUser);
                progress.setProgressComplete(progressDto.getProgressComplete());
                progressRepository.save(progress);
            }
        } else {
            // 언어가 있으면 언어별로 처리
            Optional<Progress> progressOptional = progressRepository.findByDirectoryAndTeamUserAndLanguage(
                directory, teamUser, language
            );

            if (progressOptional.isPresent()) {
                // 기존 진행률이 있으면 업데이트
                Progress progress = progressOptional.get();
                progress.setProgressComplete(progressDto.getProgressComplete());
                progressRepository.save(progress);
            } else {
                // 없으면 새로 생성
                Progress progress = new Progress();
                progress.setDirectory(directory);
                progress.setTeamUser(teamUser);
                progress.setLanguage(language);
                progress.setProgressComplete(progressDto.getProgressComplete());
                progressRepository.save(progress);
            }
        }
    }
    
    /**
     * 설명: 컨테이너 내 모든 멤버의 진행률 조회
     * @param containerId
     * @return List<ProgressListDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProgressListDto> getAllMembersProgressInContainer(Integer containerId) {
        // 컨테이너에 속한 모든 문제 조회
        List<Question> questions = questionRepository.findByContainer_ContainerId(containerId);
        
        // 컨테이너 정보를 통해 팀 ID 가져오기
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));
        
        Integer teamId = container.getTeam().getTeamId();
        
        if (teamId == null) {
            return new ArrayList<>();
        }
        
        // Question과 연결된 Directory만 추출 (중복 제거)
        Set<Directory> problemDirectories = questions.stream()
                .map(Question::getDirectory)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        // 로그는 필요시 SLF4J 로거를 사용하여 남기세요
        // log.debug("Total questions: {}, Connected directories: {}", questions.size(), problemDirectories.size());
        
        // 팀에 속한 모든 팀 유저 조회
        List<TeamUser> teamUsers = teamUserRepository.findByTeam_TeamId(teamId);
        
        // 문제가 없는 경우 팀원 정보만 반환
        if (questions.isEmpty()) {
            List<ProgressListDto> result = new ArrayList<>();
            for (TeamUser teamUser : teamUsers) {
                Member member = teamUser.getMember();
                ProgressListDto dto = new ProgressListDto();
                dto.setMemberId(member.getMemberId());
                dto.setMemberName(member.getMemberName());
                dto.setDirectoryCount(0);
                dto.setAverageProgress(0);
                dto.setLanguage("N/A");
                result.add(dto);
            }
            return result;
        }
        
        // 배치 처리로 모든 진행률 데이터를 한 번에 조회
        List<Progress> allProgressData = progressRepository
            .findAllProgressForProblemDirectoriesInContainer(containerId);
        
        // 팀 유저별로 진행률 데이터 그룹화
        Map<Integer, Map<Integer, List<Progress>>> progressByTeamUserAndDirectory = 
            allProgressData.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getTeamUser().getTeamUserId(),
                    Collectors.groupingBy(p -> p.getDirectory().getDirectoryId())
                ));
        
        List<ProgressListDto> result = new ArrayList<>();
        
        for (TeamUser teamUser : teamUsers) {
            Member member = teamUser.getMember();
            Map<Integer, List<Progress>> userDirectoryProgress = 
                progressByTeamUserAndDirectory.getOrDefault(teamUser.getTeamUserId(), new HashMap<>());
            
            // 언어별로 진행률 데이터 수집
            Map<String, List<Integer>> languageProgressMap = new HashMap<>();
            Map<String, Integer> languageCompletedCount = new HashMap<>();
            
            for (Directory directory : problemDirectories) {
                List<Progress> progressListByLang = userDirectoryProgress.getOrDefault(
                    directory.getDirectoryId(), 
                    new ArrayList<>()
                );
                
                // 각 언어별로 진행률 수집
                for (Progress progress : progressListByLang) {
                    String lang = progress.getLanguage();
                    if (lang == null || lang.isEmpty()) {
                        lang = "N/A";
                    }
                    
                    languageProgressMap.computeIfAbsent(lang, k -> new ArrayList<>()).add(progress.getProgressComplete());
                    if (progress.getProgressComplete() >= 100) {
                        languageCompletedCount.put(lang, languageCompletedCount.getOrDefault(lang, 0) + 1);
                    }
                }
            }
            
            // 언어가 없는 경우 기본 DTO 추가
            if (languageProgressMap.isEmpty()) {
                ProgressListDto dto = new ProgressListDto();
                dto.setMemberId(member.getMemberId());
                dto.setMemberName(member.getMemberName());
                dto.setDirectoryCount(problemDirectories.size());
                dto.setAverageProgress(0);
                dto.setLanguage("N/A");
                result.add(dto);
            } else {
                // 각 언어별로 DTO 생성
                for (Map.Entry<String, List<Integer>> entry : languageProgressMap.entrySet()) {
                    String language = entry.getKey();
                    List<Integer> progressValues = entry.getValue();
                    
                    // 해당 언어의 평균 진행률 계산
                    int totalProgress = progressValues.stream().mapToInt(Integer::intValue).sum();
                    int averageProgress = problemDirectories.size() > 0 ? totalProgress / problemDirectories.size() : 0;
                    
                    ProgressListDto dto = new ProgressListDto();
                    dto.setMemberId(member.getMemberId());
                    dto.setMemberName(member.getMemberName());
                    dto.setDirectoryCount(languageCompletedCount.getOrDefault(language, 0));
                    dto.setAverageProgress(averageProgress);
                    dto.setLanguage(formatLanguageName(language));
                    result.add(dto);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 멤버가 해당 컨테이너에서 가장 최근에 사용한 언어 조회
     */
    private String getRecentLanguageForMember(Member member, Integer containerId) {
        // 해당 컨테이너의 모든 문제 조회
        List<Question> questions = questionRepository.findByContainer_ContainerId(containerId);
        
        // 언어 조회 로그는 필요시 SLF4J 로거 사용
        // log.debug("Finding language for member: {} in container: {}", member.getMemberId(), containerId);
        // log.debug("Found {} questions in container", questions.size());
        
        // 모든 Result를 수집하여 가장 최근 것 찾기
        Result mostRecentResult = null;
        int totalResults = 0;
        
        for (Question question : questions) {
            List<Result> results = resultRepository.findByQuestionAndMember(question, member);
            totalResults += results.size();
            
            for (Result result : results) {
                // Result 로그는 필요시 SLF4J 로거 사용
                // log.debug("Result ID: {}, Language: {}, Answer: {}",
                //          result.getResultId(), result.getResultLang(), result.getResultAnswer());
                if (mostRecentResult == null || result.getResultId() > mostRecentResult.getResultId()) {
                    mostRecentResult = result;
                }
            }
        }
        
        // 결과 수 로그는 필요시 SLF4J 로거 사용
        // log.debug("Total results found: {}", totalResults);
        
        // 가장 최근 결과의 언어 반환
        if (mostRecentResult != null && mostRecentResult.getResultLang() != null) {
            String lang = mostRecentResult.getResultLang();
            // 최근 언어 로그는 필요시 SLF4J 로거 사용
            // log.debug("Most recent language: {}", lang);
            
            // 언어명을 적절한 형식으로 변환
            switch (lang.toLowerCase()) {
                case "javascript":
                    return "JavaScript";
                case "java":
                    return "Java";
                case "python":
                    return "Python";
                default:
                    return lang;
            }
        }
        
        // 결과 없음 로그는 필요시 SLF4J 로거 사용
        // log.debug("No results found, returning N/A");
        return "N/A";
    }
    
    /**
     * 언어 이름을 적절한 형식으로 변환
     */
    private String formatLanguageName(String language) {
        if (language == null || language.isEmpty() || "N/A".equals(language)) {
            return "N/A";
        }
        
        switch (language.toLowerCase()) {
            case "javascript":
                return "JavaScript";
            case "java":
                return "Java";
            case "python":
                return "Python";
            default:
                return language;
        }
    }
    
    /**
     * 멤버가 특정 디렉터리에서 사용한 언어 조회 (CodeFile 기반)
     */
    private String getLanguageForDirectoryAndMember(Integer directoryId, String memberId) {
        // 해당 디렉터리의 CodeFile을 최신순으로 조회
        List<CodeFile> codeFiles = codeFileRepository.findByDirectory_DirectoryIdOrderByCodeFileIdDesc(directoryId);
        
        // 해당 멤버의 파일 찾기
        for (CodeFile codeFile : codeFiles) {
            String fileName = codeFile.getCodeFileName();
            if (fileName != null && fileName.toLowerCase().startsWith(memberId.toLowerCase())) {
                // 파일 확장자로 언어 판단
                if (fileName.endsWith(".js")) return "JavaScript";
                if (fileName.endsWith(".java")) return "Java";
                if (fileName.endsWith(".py")) return "Python";
            }
        }
        
        return "N/A";
    }

    
    /**
     * 설명: 컨테이너의 모든 문제별 진행률 조회
     * @param containerId
     * @param memberId
     * @return List<QuestionProgressDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuestionProgressDto> getQuestionProgressByMember(Integer containerId, String memberId) {
        // 1. 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        // 2. 컨테이너의 모든 문제 조회
        List<Question> questions = questionRepository.findByContainer_ContainerId(containerId);
        
        List<QuestionProgressDto> result = new ArrayList<>();
        
        for (Question question : questions) {
            QuestionProgressDto dto = new QuestionProgressDto();
            dto.setQuestionId(question.getQuestionId());
            dto.setQuestionTitle(question.getQuestionTitle());
            
            // 3. 각 문제의 테스트케이스 정보
            List<TestCase> testCases = question.getTestCases();
            int totalTestCases = testCases.size();
            
            // 4. Question에 연결된 Directory 찾기
            Directory directory = question.getDirectory();
            int progressPercentage = 0;
            
            if (directory != null) {
                // 해당 멤버의 TeamUser 찾기
                TeamUser teamUser = teamUserRepository.findByMember_MemberIdAndTeam_TeamId(
                    memberId, directory.getTeam().getTeamId()
                ).orElse(null);
                
                if (teamUser != null) {
                    // Progress에서 진행률 가져오기 (모든 언어 중 최고 진행률)
                    List<Progress> progressList = progressRepository.findAllByDirectoryAndTeamUser(directory, teamUser);
                    for (Progress progress : progressList) {
                        if (progress.getProgressComplete() > progressPercentage) {
                            progressPercentage = progress.getProgressComplete();
                        }
                    }
                }
            }
            
            // 5. 진행률 계산
            int passedTestCases = (progressPercentage * totalTestCases) / 100;
            dto.setTotalTestCases(totalTestCases);
            dto.setPassedTestCases(passedTestCases);
            dto.setProgressPercentage(progressPercentage);
            
            result.add(dto);
        }
        
        return result;
    }
    
    
    /**
     * 설명: 특정 디렉토리의 멤버 진행률 조회 (간단 버전)
     * @param directoryId
     * @param memberId
     * @return 진행률 퍼센트
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getMemberProgressInDirectory(Integer directoryId, String memberId) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        
        TeamUser teamUser = teamUserRepository.findByMember_MemberIdAndTeam_TeamId(
                memberId, directory.getTeam().getTeamId()
        ).orElse(null);
        
        if (teamUser == null) {
            return 0;
        }
        
        // 모든 언어별 진행률 조회
        List<Progress> progressList = progressRepository.findAllByDirectoryAndTeamUser(directory, teamUser);
        
        // 가장 높은 진행률 반환
        int maxProgress = 0;
        for (Progress progress : progressList) {
            if (progress.getProgressComplete() > maxProgress) {
                maxProgress = progress.getProgressComplete();
            }
        }
        
        return maxProgress;
    }
}