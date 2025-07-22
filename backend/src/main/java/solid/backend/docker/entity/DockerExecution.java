package solid.backend.docker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solid.backend.entity.Container;
import solid.backend.entity.Member;

import java.time.LocalDateTime;

/**
 * 도커 실행 기록 엔티티
 */
@Entity
@Table(name = "docker_execution")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DockerExecution {
    
    /** 실행 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_id")
    private Long executionId;
    
    /** 소속 컨테이너 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;
    
    /** 실행 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    /** 프로그래밍 언어 (python, java, javascript, cpp, c) */
    @Column(name = "language", nullable = false, length = 20)
    private String language;
    
    /** 파일 경로 */
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    /** 실행 코드 */
    @Column(name = "code", columnDefinition = "TEXT")
    private String code;
    
    /** 표준 입력 */
    @Column(name = "input", columnDefinition = "TEXT")
    private String input;
    
    /** 표준 출력 */
    @Column(name = "output", columnDefinition = "TEXT")
    private String output;
    
    /** 에러 출력 */
    @Column(name = "error_output", columnDefinition = "TEXT")
    private String errorOutput;
    
    /** 실행 상태 (PENDING, RUNNING, COMPLETED, ERROR, TIMEOUT) */
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    /** 실행 시간 (밀리초) */
    @Column(name = "execution_time")
    private Long executionTime;
    
    /** 메모리 사용량 (바이트) */
    @Column(name = "memory_used")
    private Long memoryUsed;
    
    /** 생성 일시 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /** 완료 일시 */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * 실행 완료 처리
     * @param output 표준 출력
     * @param errorOutput 에러 출력
     * @param executionTime 실행 시간
     * @param memoryUsed 메모리 사용량
     */
    public void complete(String output, String errorOutput, Long executionTime, Long memoryUsed) {
        this.output = output;
        this.errorOutput = errorOutput;
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * 실행 실패 처리
     * @param errorOutput 에러 메시지
     */
    public void fail(String errorOutput) {
        this.errorOutput = errorOutput;
        this.status = "ERROR";
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * 상태 업데이트
     * @param status 새로운 상태
     */
    public void updateStatus(String status) {
        this.status = status;
    }
}