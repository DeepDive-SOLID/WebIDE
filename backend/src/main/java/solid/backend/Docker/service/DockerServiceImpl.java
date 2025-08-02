package solid.backend.Docker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solid.backend.Docker.dto.*;
import solid.backend.common.DockerRun;
import solid.backend.entity.*;
import solid.backend.jpaRepository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DockerServiceImpl implements DockerService {
    private final CodeFileRepository codeFileRepository;
    private final TestCaseRepository testcaseRepository;
    private final ResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final DockerRun dockerRun;
    private final ProgressRepository progressRepository;
    private final TeamUserRepository teamUserRepository;

    /**
     * 설명: 코드 파일 도커 컨테이너에서 실행
     * @param codeFileId
     * @param questionId
     * @return ExecutionResultDto
     */
    @Override
    @Transactional
    public ExecutionResultDto runCodeFile(String memberId, Integer codeFileId, Integer questionId) {
        CodeFile codeFile = codeFileRepository.findById(String.valueOf(codeFileId))
                .orElseThrow(() -> new IllegalArgumentException("코드 파일이 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제가 존재하지 않습니다."));
        List<TestCase> testcases = testcaseRepository.findByQuestion_QuestionId(questionId);

        String filePath = codeFile.getCodeFilePath();
        String extension = dockerRun.getFileExtension(filePath);
        String language = dockerRun.getLanguageByExtension(extension);
        float time = 0.0f;
        float memory = 0.0f;
        int count = 0;

        List<TestcaseResultDto> testcaseResults = new ArrayList<>();
        boolean allPass = true;
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (TestCase testcase : testcases) {
            String[] command = dockerRun.buildDockerCommand(filePath, extension, testcase.getCaseEx(), question.getQuestionMem());

            DockerResultDto result = dockerRun.runDockerCommand(command, question.getQuestionTime());
            String output = result.getOutput().trim();
            float execTime = result.getTime();

            boolean pass = output.equals(testcase.getCaseAnswer().trim());

            long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memUsedBytes = afterUsedMem - beforeUsedMem;
            double memUsedMb = memUsedBytes / 1024.0 / 1024.0;
            String totalMemoryUsed = String.format("%.2f MB", memUsedMb);

            if((Math.round(execTime * 100.0) / 100.0) > testcase.getQuestion().getQuestionTime()) pass = false;
            if(memUsedMb > testcase.getQuestion().getQuestionMem()) pass = false;

            if(time < (Math.round(execTime * 100.0) / 100.0)) time = (float) (Math.round(execTime * 100.0) / 100.0);
            if(memory < (Math.round(execTime * 100.0) / 100.0)) memory = (float) (Math.round(memUsedMb * 100.0) / 100.0);

            if(pass) count++;
            allPass &= pass;

            testcaseResults.add(new TestcaseResultDto(
                    Math.round(execTime * 100.0) / 100.0,
                    totalMemoryUsed,
                    testcase.getCaseEx(),
                    testcase.getCaseAnswer(),
                    output,
                    pass
            ));
        }
        int progress = (int) ((double) count / testcases.size() * 100.0);

        Result result = new Result();
        result.setResultTime(time);
        result.setResultAnswer(allPass ? "정답" : "실패");
        result.setResultMemory(memory);
        result.setResultLang(language);
        result.setMember(member);
        result.setQuestion(question);
        result.setTestCase(testcases.getFirst());

        resultRepository.save(result);

        // Progress 업데이트 (부분 점수 반영)
        Directory directory = codeFile.getDirectory();
        if (directory != null) {
            // TeamUser 찾기
            TeamUser teamUser = teamUserRepository.findByMember_MemberIdAndTeam_TeamId(
                memberId, 
                directory.getTeam().getTeamId()
            ).orElse(null);
            
            if (teamUser != null) {
                // Progress 찾기 또는 생성 (언어별로)
                Progress progressEntity = progressRepository.findByDirectoryAndTeamUserAndLanguage(
                    directory, teamUser, language
                ).orElseGet(() -> {
                    Progress newProgress = new Progress();
                    newProgress.setDirectory(directory);
                    newProgress.setTeamUser(teamUser);
                    newProgress.setLanguage(language);
                    newProgress.setProgressComplete(0);
                    return newProgress;
                });
                
                // 진행률 업데이트 (부분 점수 반영)
                progressEntity.setProgressComplete(progress); // 계산된 퍼센트 저장
                progressRepository.save(progressEntity);
            }
        }

        return new ExecutionResultDto(
                language,
                time,
                String.format("%.2f MB", memory),
                allPass,
                progress,
                testcaseResults
        );
    }

    /**
     * 설명: 테스트 실행
     * @param memberId
     * @param codeFileId
     * @param questionId
     * @return ExecutionTestDto
     */
    @Override
    @Transactional
    public ExecutionTestDto runTestCodeFile(String memberId, Integer codeFileId, Integer questionId) {
        CodeFile codeFile = codeFileRepository.findById(String.valueOf(codeFileId))
                .orElseThrow(() -> new IllegalArgumentException("코드 파일이 존재하지 않습니다."));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제가 존재하지 않습니다."));
        List<TestCase> testcases = testcaseRepository.findByQuestion_QuestionId(questionId);

        String filePath = codeFile.getCodeFilePath();
        String extension = dockerRun.getFileExtension(filePath);
        String language = dockerRun.getLanguageByExtension(extension);

        List<TestcaseResultDto> testcaseResults = new ArrayList<>();
        boolean allPass = true;
        int passCount = 0;
        int totalCount = 0;
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (TestCase testcase : testcases) {
            if (!Boolean.TRUE.equals(testcase.getCaseCheck())) continue;
            totalCount++;

            String[] command = dockerRun.buildDockerCommand(filePath, extension, testcase.getCaseEx(), question.getQuestionMem());

            DockerResultDto result = dockerRun.runDockerCommand(command, question.getQuestionTime());
            String output = result.getOutput().trim();
            float execTime = result.getTime();

            boolean pass = output.equals(testcase.getCaseAnswer().trim());

            long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memUsedBytes = afterUsedMem - beforeUsedMem;
            double memUsedMb = memUsedBytes / 1024.0 / 1024.0;
            String totalMemoryUsed = String.format("%.2f MB", memUsedMb);

            if((Math.round(execTime * 100.0) / 100.0) > testcase.getQuestion().getQuestionTime()) pass = false;
            if(memUsedMb > testcase.getQuestion().getQuestionMem()) pass = false;

            if (pass) passCount++;
            allPass &= pass;

            testcaseResults.add(new TestcaseResultDto(
                    Math.round(execTime * 100.0) / 100.0,
                    totalMemoryUsed,
                    testcase.getCaseEx(),
                    testcase.getCaseAnswer(),
                    output,
                    pass
            ));
        }

        // 테스트 실행 시에도 진행률 업데이트 (테스트로 보이는 케이스만 계산)
        Directory directory = codeFile.getDirectory();
        if (directory != null && totalCount > 0) {
            TeamUser teamUser = teamUserRepository.findByMember_MemberIdAndTeam_TeamId(
                memberId, 
                directory.getTeam().getTeamId()
            ).orElse(null);
            
            if (teamUser != null) {
                int progress = (passCount * 100) / totalCount;
                
                Progress progressEntity = progressRepository.findByDirectoryAndTeamUserAndLanguage(
                    directory, teamUser, language
                ).orElseGet(() -> {
                    Progress newProgress = new Progress();
                    newProgress.setDirectory(directory);
                    newProgress.setTeamUser(teamUser);
                    newProgress.setLanguage(language);
                    newProgress.setProgressComplete(0);
                    return newProgress;
                });
                
                // 기존 진행률과 비교하여 더 높은 값으로 업데이트
                Integer currentProgress = progressEntity.getProgressComplete();
                if (currentProgress == null || progress > currentProgress) {
                    progressEntity.setProgressComplete(progress);
                    progressRepository.save(progressEntity);
                }
            }
        }

        return new ExecutionTestDto(
                language,
                allPass,
                testcaseResults
        );
    }

    /**
     * 설명: 사용자의 입력값으로 실행
     * @param customInputDto
     * @return CustomInputResultDto
     */
    @Override
    @Transactional
    public CustomInputResultDto runExctCodeFile(CustomInputDto customInputDto) {
        // 코드 파일 조회
        CodeFile codeFile = codeFileRepository.findById(String.valueOf(customInputDto.getCodeFileId()))
                .orElseThrow(() -> new IllegalArgumentException("코드 파일이 존재하지 않습니다."));

        String filePath = codeFile.getCodeFilePath();
        String extension = dockerRun.getFileExtension(filePath);

        // 입력값으로 실행
        String[] command = dockerRun.buildDockerCommand(filePath, extension, customInputDto.getInput(), 256);
        DockerResultDto result = dockerRun.runDockerCommand(command, 2f);

        // 결과 반환
        return new CustomInputResultDto(
                result.getOutput().trim()
        );
    }
}
