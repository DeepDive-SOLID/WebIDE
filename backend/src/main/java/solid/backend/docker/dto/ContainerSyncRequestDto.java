package solid.backend.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * 컨테이너 파일 동기화 요청 DTO
 * 
 * 데이터베이스에 저장된 컨테이너의 파일 시스템을 Docker 컨테이너와 동기화하기 위한 요청 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerSyncRequestDto {
    
    /**
     * 동기화할 컨테이너 ID
     */
    @NotNull(message = "컨테이너 ID는 필수입니다")
    private Long containerId;
    
    /**
     * Docker 컨테이너 이름 (선택사항)
     * null인 경우 자동 생성됨: webide-container-{containerId}
     */
    private String dockerContainerName;
    
    /**
     * 작업 디렉토리 경로 (선택사항)
     * 기본값: /workspace
     */
    @Builder.Default
    private String workingDirectory = "/workspace";
    
    /**
     * 기존 파일 덮어쓰기 여부
     * true: 기존 파일 덮어쓰기
     * false: 기존 파일 유지
     */
    @Builder.Default
    private Boolean overwriteExisting = false;
    
    /**
     * 동기화 모드
     * FULL: 전체 파일 시스템 동기화
     * INCREMENTAL: 변경된 파일만 동기화
     */
    @Builder.Default
    private SyncMode syncMode = SyncMode.FULL;
    
    public enum SyncMode {
        FULL,
        INCREMENTAL
    }
}