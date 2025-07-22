package solid.backend.docker.service;

import solid.backend.docker.dto.CodeExecutionRequestDto;
import solid.backend.docker.dto.CodeExecutionResponseDto;
import solid.backend.docker.dto.ContainerStatisticsDto;
import solid.backend.docker.dto.ContainerSyncRequestDto;
import solid.backend.docker.dto.ContainerSyncResponseDto;
import solid.backend.docker.dto.ExecutionStatusDto;
import solid.backend.docker.dto.FileSystemNodeDto;
import solid.backend.docker.dto.LanguageStatisticsResponseDto;

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
    
    /**
     * 컨테이너 실행 통계 조회
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @return 컨테이너 실행 통계
     */
    ContainerStatisticsDto getContainerStatistics(Long containerId, String memberId);
    
    /**
     * 컨테이너 언어별 실행 통계 조회
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @return 언어별 실행 통계
     */
    LanguageStatisticsResponseDto getLanguageStatistics(Long containerId, String memberId);
    
    /**
     * 컨테이너의 파일 시스템을 Docker 컨테이너와 동기화
     * @param memberId 사용자 ID
     * @param request 동기화 요청 정보
     * @return 동기화 결과
     */
    ContainerSyncResponseDto syncContainerFiles(String memberId, ContainerSyncRequestDto request);
    
    /**
     * 컨테이너의 파일 시스템 트리 조회
     * @param containerId 컨테이너 ID
     * @param memberId 사용자 ID
     * @return 파일 시스템 트리 구조
     */
    FileSystemNodeDto getContainerFileTree(Long containerId, String memberId);
    
    /**
     * Docker 컨테이너가 존재하는지 확인
     * @param containerName Docker 컨테이너 이름
     * @return 존재 여부
     */
    boolean isDockerContainerExists(String containerName);
}