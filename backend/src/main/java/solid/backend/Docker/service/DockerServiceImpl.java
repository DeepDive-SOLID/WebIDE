package solid.backend.Docker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solid.backend.Docker.dto.*;
import solid.backend.common.DockerRun;
import solid.backend.entity.CodeFile;
import solid.backend.entity.TestCase;
import solid.backend.jpaRepository.CodeFileRepository;
import solid.backend.jpaRepository.TestCaseRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DockerServiceImpl implements DockerService {
    private final CodeFileRepository codeFileRepository;
    private final TestCaseRepository testcaseRepository;
    private final DockerRun dockerRun;

    /**
     * 설명: 코드 파일 도커 컨테이너에서 실행
     * @param codeFileId
     * @param questionId
     * @return String (실행 결과)
     */
    @Override
    @Transactional
    public ExecutionResultDto runCodeFile(Integer codeFileId, Integer questionId) {
        CodeFile codeFile = codeFileRepository.findById(String.valueOf(codeFileId))
                .orElseThrow(() -> new IllegalArgumentException("코드 파일이 존재하지 않습니다."));
        List<TestCase> testcases = testcaseRepository.findByQuestion_QuestionId(questionId);

        String filePath = codeFile.getCodeFilePath();
        String extension = dockerRun.getFileExtension(filePath);
        String language = dockerRun.getLanguageByExtension(extension);

        List<TestcaseResultDto> testcaseResults = new ArrayList<>();
        boolean allPass = true;
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (TestCase testcase : testcases) {
            String[] command = dockerRun.buildDockerCommand(filePath, extension, testcase.getCaseEx());

            DockerResultDto result = dockerRun.runDockerCommand(command);
            String output = result.getOutput().trim();
            float execTime = result.getTime();

            boolean pass = output.equals(testcase.getCaseAnswer().trim());
            allPass &= pass;

            long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memUsedBytes = afterUsedMem - beforeUsedMem;
            double memUsedMb = memUsedBytes / 1024.0 / 1024.0;
            String totalMemoryUsed = String.format("%.2f MB", memUsedMb);

            testcaseResults.add(new TestcaseResultDto(
                    Math.round(execTime * 100.0) / 100.0,
                    totalMemoryUsed,
                    testcase.getCaseEx(),
                    testcase.getCaseAnswer(),
                    output,
                    pass
            ));
        }

        return new ExecutionResultDto(
                language,
                allPass,
                testcaseResults
        );
    }

    /**
     * 설명: 테스트 실행
     * @param codeFileId
     * @param questionId
     * @return ExecutionResultDto
     */
    @Override
    @Transactional
    public ExecutionResultDto runTestCodeFile(Integer codeFileId, Integer questionId) {
        CodeFile codeFile = codeFileRepository.findById(String.valueOf(codeFileId))
                .orElseThrow(() -> new IllegalArgumentException("코드 파일이 존재하지 않습니다."));
        List<TestCase> testcases = testcaseRepository.findByQuestion_QuestionId(questionId);

        String filePath = codeFile.getCodeFilePath();
        String extension = dockerRun.getFileExtension(filePath);
        String language = dockerRun.getLanguageByExtension(extension);

        List<TestcaseResultDto> testcaseResults = new ArrayList<>();
        boolean allPass = true;
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (TestCase testcase : testcases) {
            if (!Boolean.TRUE.equals(testcase.getCaseCheck())) continue;

            String[] command = dockerRun.buildDockerCommand(filePath, extension, testcase.getCaseEx());

            DockerResultDto result = dockerRun.runDockerCommand(command);
            String output = result.getOutput().trim();
            float execTime = result.getTime();

            boolean pass = output.equals(testcase.getCaseAnswer().trim());
            allPass &= pass;

            long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memUsedBytes = afterUsedMem - beforeUsedMem;
            double memUsedMb = memUsedBytes / 1024.0 / 1024.0;
            String totalMemoryUsed = String.format("%.2f MB", memUsedMb);

            testcaseResults.add(new TestcaseResultDto(
                    Math.round(execTime * 100.0) / 100.0,
                    totalMemoryUsed,
                    testcase.getCaseEx(),
                    testcase.getCaseAnswer(),
                    output,
                    pass
            ));
        }

        return new ExecutionResultDto(
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
        String[] command = dockerRun.buildDockerCommand(filePath, extension, customInputDto.getInput());
        DockerResultDto result = dockerRun.runDockerCommand(command);

        // 결과 반환
        return new CustomInputResultDto(
                result.getOutput().trim()
        );
    }
}
