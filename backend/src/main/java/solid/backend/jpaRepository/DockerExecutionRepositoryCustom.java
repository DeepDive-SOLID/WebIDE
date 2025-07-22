package solid.backend.jpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import solid.backend.docker.entity.DockerExecution;

/**
 * 도커 실행 기록에 대한 커스텀 레포지토리 인터페이스
 * 
 * QueryDSL을 사용한 복잡한 쿼리를 정의합니다.
 * 실제 구현은 DockerExecutionJpaRepositoryImpl 클래스에서 수행됩니다.
 */
public interface DockerExecutionRepositoryCustom {
    
    /**
     * 특정 시간 이전의 오래된 실행 기록을 삭제합니다.
     * 
     * @param dateTime 삭제 기준 시간
     * @return 삭제된 레코드 수
     */
    long deleteByCreatedAtBefore(LocalDateTime dateTime);
    
    /**
     * 현재 실행 중인 도커 컨테이너 실행 기록을 조회합니다.
     * 
     * @param memberId 사용자 ID (optional)
     * @return 실행 중인 도커 실행 기록 목록
     */
    List<DockerExecution> findRunningExecutions(String memberId);
    
    /**
     * 특정 기간의 실행 통계를 조회합니다.
     * 
     * @param containerId 컨테이너 ID (optional)
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 실행 기록 목록
     */
    List<DockerExecution> findExecutionStatistics(Long containerId, LocalDateTime startDate, LocalDateTime endDate);
}