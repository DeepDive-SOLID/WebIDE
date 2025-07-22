package solid.backend.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 컨테이너 파일 동기화 응답 DTO
 * 
 * 파일 동기화 작업의 결과를 담는 응답 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerSyncResponseDto {
    
    /**
     * 동기화 작업 ID
     */
    private String syncId;
    
    /**
     * 컨테이너 ID
     */
    private Long containerId;
    
    /**
     * Docker 컨테이너 이름
     */
    private String dockerContainerName;
    
    /**
     * Docker 컨테이너 ID
     */
    private String dockerContainerId;
    
    /**
     * 동기화 상태
     */
    private SyncStatus status;
    
    /**
     * 동기화된 파일 수
     */
    private Integer syncedFileCount;
    
    /**
     * 동기화된 디렉토리 수
     */
    private Integer syncedDirectoryCount;
    
    /**
     * 동기화 실패한 파일 목록
     */
    private List<String> failedFiles;
    
    /**
     * 오류 메시지 (실패 시)
     */
    private String errorMessage;
    
    /**
     * 동기화 시작 시간
     */
    private LocalDateTime startTime;
    
    /**
     * 동기화 완료 시간
     */
    private LocalDateTime endTime;
    
    /**
     * 동기화 소요 시간 (밀리초)
     */
    private Long elapsedTime;
    
    public enum SyncStatus {
        SUCCESS,
        PARTIAL_SUCCESS,
        FAILED,
        IN_PROGRESS
    }
}