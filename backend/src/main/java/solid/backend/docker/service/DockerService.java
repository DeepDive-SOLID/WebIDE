package solid.backend.docker.service;

import solid.backend.docker.dto.CodeExecutionRequestDto;
import solid.backend.docker.dto.CodeExecutionResponseDto;
import solid.backend.docker.dto.ExecutionStatusDto;

import java.util.List;

/**
 * 도커 서비스 인터페이스
 */
public interface DockerService {
    
    /**
     * 코드 실행
     * @param memberId 사용자 ID
     * @param request 코드 실행 요청 정보
     * @return 실행 결과
     */
    CodeExecutionResponseDto executeCode(String memberId, CodeExecutionRequestDto request);
    
    /**
     * 실행 상태 조회
     * @param executionId 실행 ID
     * @return 실행 상태
     */
    ExecutionStatusDto getExecutionStatus(Long executionId);
    
    /**
     * 실행 중지
     * @param executionId 실행 ID
     */
    void stopExecution(Long executionId);
    
    /**
     * 컨테이너의 실행 기록 조회
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @return 실행 기록 목록
     */
    List<CodeExecutionResponseDto> getExecutionHistory(Long containerId, String memberId);
    
    /**
     * 실행 기록 삭제
     * @param executionId 실행 ID
     * @param memberId 사용자 ID
     */
    void deleteExecution(Long executionId, String memberId);
    
    /**
     * 지원 언어 목록 조회
     * @return 지원 언어 목록
     */
    List<String> getSupportedLanguages();
}