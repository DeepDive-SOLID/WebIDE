package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.docker.entity.DockerExecution;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 도커 실행 기록 레포지토리 인터페이스
 * 
 * 도커 컨테이너 실행 기록을 관리하는 레포지토리입니다.
 * 컨테이너 생성, 실행, 종료 등의 이벤트를 기록하여 사용 통계 및 모니터링에 활용됩니다.
 * 
 * @see DockerExecution - 도커 실행 기록 엔티티
 * @see DockerExecutionRepositoryImpl - QueryDSL 커스텀 메서드 구현
 */
@Repository
public interface DockerExecutionRepository extends JpaRepository<DockerExecution, Long>, DockerExecutionRepositoryCustom {
    
    /**
     * 특정 컨테이너의 모든 실행 기록을 조회합니다.
     * 
     * 사용 예시:
     * - 컨테이너의 사용 내역 확인
     * - 컨테이너별 실행 통계 수집
     * 
     * @param containerId 조회할 컨테이너 ID
     * @return 해당 컨테이너의 실행 기록 목록 (생성일 기준 내림차순)
     */
    List<DockerExecution> findByContainerContainerIdOrderByCreatedAtDesc(Long containerId);
    
    /**
     * 특정 사용자의 모든 도커 실행 기록을 조회합니다.
     * 
     * 사용 예시:
     * - 사용자의 활동 내역 확인
     * - 사용자별 사용량 통계
     * 
     * @param memberId 조회할 사용자 ID
     * @return 해당 사용자의 실행 기록 목록 (생성일 기준 내림차순)
     */
    List<DockerExecution> findByMemberMemberIdOrderByCreatedAtDesc(String memberId);
    
    /**
     * 특정 컨테이너에서 특정 사용자의 실행 기록을 조회합니다.
     * 
     * 사용 예시:
     * - 특정 컨테이너에서 특정 사용자의 활동 확인
     * - 권한 검증 후 사용 내역 표시
     * 
     * @param containerId 조회할 컨테이너 ID
     * @param memberId 조회할 사용자 ID
     * @return 해당 컨테이너에서 해당 사용자의 실행 기록 (생성일 기준 내림차순)
     */
    List<DockerExecution> findByContainerContainerIdAndMemberMemberIdOrderByCreatedAtDesc(Long containerId, String memberId);
}