package solid.backend.Docker.service;

import solid.backend.Docker.dto.CustomInputDto;
import solid.backend.Docker.dto.CustomInputResultDto;
import solid.backend.Docker.dto.ExecutionResultDto;
import solid.backend.Docker.dto.ExecutionTestDto;

public interface DockerService {
    /**
     * 설명: 언어별 코드 파일 실행
     * @param codeFileId
     */
    ExecutionResultDto runCodeFile(String memberId, Integer codeFileId, Integer questionId);

    /**
     * 설명: 테스트케이스 실행
     * @param codeFileId
     * @param questionId
     */
    ExecutionTestDto runTestCodeFile(Integer codeFileId, Integer questionId);

    /**
     * 설명: 사용자 입력 실행
     * @param customInputDto
     */
    CustomInputResultDto runExctCodeFile(CustomInputDto customInputDto);
}
