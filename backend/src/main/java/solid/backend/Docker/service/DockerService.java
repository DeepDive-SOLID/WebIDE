package solid.backend.Docker.service;

import solid.backend.Docker.dto.CustomInputDto;
import solid.backend.Docker.dto.CustomInputResultDto;
import solid.backend.Docker.dto.ExecutionResultDto;

public interface DockerService {
    /**
     * 설명: 언어별 코드 파일 실행
     * @param codeFileId
     */
    ExecutionResultDto runCodeFile(Integer codeFileId, Integer questionId);

    ExecutionResultDto runTestCodeFile(Integer codeFileId, Integer questionId);

    CustomInputResultDto runExctCodeFile(CustomInputDto customInputDto);
}
