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
import java.util.regex.Pattern;

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
    
    // 문제 디렉터리 필터링을 위한 정규표현식 패턴
    private static final Pattern EXCLUDE_PATTERN = Pattern.compile(".*(주차|week|Week|root|알고리즘|Algorithm|단원|Chapter).*");
    private static final Pattern PROBLEM_PATTERN = Pattern.compile(
        "^([A-Z][\\+\\-\\*\\/][A-Z]|" +      // A+B, A-B, A*B, A/B
        "[0-9]+[_\\-\\s].*|" +               // 1_문제, 1-문제, 1 문제
        "\\[.*\\].*|" +                      // [BOJ1000]문제
        "[a-zA-Z]+[\\+\\-\\*\\/][a-zA-Z]+)$" // a+b, a*b 등
    );

    /**
     * 설명: 디렉터리 속 진행률 조회
     * @param directoryId
     * @return List<ProgressListDto>
     */
    @Override
    @Transactional
    public List<ProgressListDto> getProgressList(Integer directoryId) {
        // QueryDSL을 사용한 기존 방식 사용
        List<ProgressListDto> progressList = progressQueryRepository.getProgressListByDirectoryId(directoryId);
        
        // language 필드는 여기서 채워줌
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        Integer containerId = directory.getContainer().getContainerId();
        
        for (ProgressListDto dto : progressList) {
            // 디렉터리별 진행률을 볼 때는 해당 디렉터리의 CodeFile에서 언어 정보 가져오기
            String language = getLanguageForDirectoryAndMember(directoryId, dto.getMemberId());
            dto.setLanguage(language);
        }
        
        return progressList;
    }
    

    /**
     * 설명: 문제 진행률 계산
     * @param progressDto
     */
    @Override
    @Transactional
    public void updateProgress(ProgressDto progressDto) {
        // 디렉토리 ID + 팀 유저 ID 조합으로 기존 진행률 찾기
        Directory directory = directoryRepository.findById(progressDto.getDirectoryId())
                .orElseThrow(() -> new RuntimeException("Directory not found"));

        TeamUser teamUser = teamUserRepository.findById(progressDto.getTeamUserId())
                .orElseThrow(() -> new RuntimeException("TeamUser not found"));

        Optional<Progress> progressOptional = progressRepository.findByDirectoryAndTeamUser(directory, teamUser);


        if (progressOptional.isPresent()) {
            // 기존 진행률이 있으면 업데이트
            Progress progress = progressOptional.get();
            progress.setProgressComplete(progressDto.getProgressComplete());
            progressRepository.save(progress);
        } else {
            // 없으면 새로 생성
            Progress progress = new Progress();
            progress.setDirectory(directoryRepository.findById(progressDto.getDirectoryId()).orElseThrow());
            progress.setTeamUser(teamUserRepository.findById(progressDto.getTeamUserId()).orElseThrow());
            progress.setProgressComplete(progressDto.getProgressComplete());
            progressRepository.save(progress);
        }
    }
    
    /**
     * 설명: 컨테이너 내 모든 멤버의 진행률 조회
     * @param containerId
     * @return List<ProgressListDto>
     */
    @Override
    @Transactional
    public List<ProgressListDto> getAllMembersProgressInContainer(Integer containerId) {
        // 컨테이너에 속한 모든 디렉터리 조회
        List<Directory> allDirectories = directoryRepository.findAllByContainer_ContainerId(containerId);
        
        // 해당 컨테이너의 팀 ID 가져오기 (디렉터리가 있다면 첫 번째 디렉터리의 팀 ID 사용)
        if (allDirectories.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 정규표현식으로 문제 디렉터리만 필터링
        List<Directory> problemDirectories = allDirectories.stream()
                .filter(this::isProblemDirectory)
                .collect(Collectors.toList());
        
        System.out.println("Total directories: " + allDirectories.size() + ", Problem directories: " + problemDirectories.size());
        for (Directory dir : problemDirectories) {
            System.out.println("Problem directory: " + dir.getDirectoryName());
        }
        
        Integer teamId = allDirectories.get(0).getTeam().getTeamId();
        
        // 팀에 속한 모든 팀 유저 조회
        List<TeamUser> teamUsers = teamUserRepository.findByTeam_TeamId(teamId);
        
        List<ProgressListDto> result = new ArrayList<>();
        
        for (TeamUser teamUser : teamUsers) {
            Member member = teamUser.getMember();
            
            // 문제 디렉터리별 진행률 수집
            List<Integer> progressList = new ArrayList<>();
            int totalProgress = 0;
            int problemCount = 0;
            
            for (Directory directory : problemDirectories) {
                Optional<Progress> progress = progressRepository.findByDirectoryAndTeamUser(directory, teamUser);
                int progressValue = 0;
                if (progress.isPresent()) {
                    progressValue = progress.get().getProgressComplete();
                    progressList.add(progressValue);
                    totalProgress += progressValue;
                } else {
                    progressList.add(0);
                }
                System.out.println("Directory: " + directory.getDirectoryName() + 
                                 ", Progress: " + progressValue + "%");
                problemCount++;
            }
            
            // 평균 진행률 계산 (문제 디렉터리만 기준)
            int averageProgress = problemCount > 0 ? totalProgress / problemCount : 0;
            System.out.println("Member: " + member.getMemberName() + 
                             ", Total Progress: " + totalProgress + 
                             ", Problem Count: " + problemCount + 
                             ", Average: " + averageProgress + "%");
            
            ProgressListDto dto = new ProgressListDto();
            dto.setMemberId(member.getMemberId());
            dto.setMemberName(member.getMemberName());
            dto.setDirectoryCount(problemCount); // 문제 디렉터리 수만 표시
            dto.setAverageProgress(averageProgress);
            
            // 해당 멤버의 가장 최근 사용 언어 조회
            String recentLanguage = getRecentLanguageForMember(member, containerId);
            dto.setLanguage(recentLanguage);
            
            result.add(dto);
        }
        
        return result;
    }
    
    /**
     * 멤버가 해당 컨테이너에서 가장 최근에 사용한 언어 조회
     */
    private String getRecentLanguageForMember(Member member, Integer containerId) {
        // 해당 컨테이너의 모든 문제 조회
        List<Question> questions = questionRepository.findByContainer_ContainerId(containerId);
        
        System.out.println("Finding language for member: " + member.getMemberId() + " in container: " + containerId);
        System.out.println("Found " + questions.size() + " questions in container");
        
        // 모든 Result를 수집하여 가장 최근 것 찾기
        Result mostRecentResult = null;
        int totalResults = 0;
        
        for (Question question : questions) {
            List<Result> results = resultRepository.findByQuestionAndMember(question, member);
            totalResults += results.size();
            
            for (Result result : results) {
                System.out.println("Result ID: " + result.getResultId() + 
                                 ", Language: " + result.getResultLang() + 
                                 ", Answer: " + result.getResultAnswer());
                if (mostRecentResult == null || result.getResultId() > mostRecentResult.getResultId()) {
                    mostRecentResult = result;
                }
            }
        }
        
        System.out.println("Total results found: " + totalResults);
        
        // 가장 최근 결과의 언어 반환
        if (mostRecentResult != null && mostRecentResult.getResultLang() != null) {
            String lang = mostRecentResult.getResultLang();
            System.out.println("Most recent language: " + lang);
            
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
        
        System.out.println("No results found, returning N/A");
        return "N/A";
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
    @Transactional
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
            int passedTestCases = 0;
            
            // 4. 해당 멤버가 통과한 테스트케이스 수 계산
            for (TestCase testCase : testCases) {
                // Result에서 해당 문제, 멤버, 테스트케이스의 성공 여부 확인
                List<Result> results = resultRepository.findByQuestionAndMemberAndTestCase(
                    question, member, testCase
                );
                
                // 가장 최근 결과를 확인하여 통과 여부 판단
                if (!results.isEmpty()) {
                    Result latestResult = results.get(results.size() - 1);
                    // "통과", "PASS", "AC" 등의 성공 결과인지 확인
                    // Result 테이블에 데이터가 없어서 실제로는 작동하지 않음
                    String answer = latestResult.getResultAnswer();
                    if (answer != null && 
                        (answer.contains("통과") || answer.contains("PASS") || 
                         answer.contains("AC") || answer.contains("SUCCESS"))) {
                        passedTestCases++;
                    }
                }
            }
            
            // 5. 진행률 계산
            dto.setTotalTestCases(totalTestCases);
            dto.setPassedTestCases(passedTestCases);
            int progressPercentage = totalTestCases > 0 ? (passedTestCases * 100 / totalTestCases) : 0;
            dto.setProgressPercentage(progressPercentage);
            
            result.add(dto);
        }
        
        return result;
    }
    
    /**
     * 문제 디렉터리인지 확인하는 메서드
     */
    private boolean isProblemDirectory(Directory directory) {
        String directoryName = directory.getDirectoryName();
        
        // 제외 패턴에 매칭되면 문제 디렉터리가 아님
        if (EXCLUDE_PATTERN.matcher(directoryName).matches()) {
            System.out.println("Excluded directory: " + directoryName);
            return false;
        }
        
        // 문제 패턴에 매칭되거나, 특별한 패턴이 없어도 제외 패턴에 해당하지 않으면 문제로 간주
        boolean isProblem = PROBLEM_PATTERN.matcher(directoryName).matches() || 
               (!directoryName.contains(" ") && directoryName.length() < 20);
        
        if (!isProblem) {
            System.out.println("Not a problem directory: " + directoryName);
        }
        
        return isProblem;
    }
    
    /**
     * 설명: 특정 디렉토리의 멤버 진행률 조회 (간단 버전)
     * @param directoryId
     * @param memberId
     * @return 진행률 퍼센트
     */
    @Override
    @Transactional
    public Integer getMemberProgressInDirectory(Integer directoryId, String memberId) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        
        TeamUser teamUser = teamUserRepository.findByMember_MemberIdAndTeam_TeamId(
                memberId, directory.getTeam().getTeamId()
        ).orElse(null);
        
        if (teamUser == null) {
            return 0;
        }
        
        Optional<Progress> progress = progressRepository.findByDirectoryAndTeamUser(directory, teamUser);
        return progress.map(Progress::getProgressComplete).orElse(0);
    }
}