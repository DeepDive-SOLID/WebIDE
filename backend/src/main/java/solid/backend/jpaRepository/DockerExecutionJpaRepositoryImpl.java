package solid.backend.jpaRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solid.backend.docker.entity.DockerExecution;
import solid.backend.docker.entity.QDockerExecution;

import java.time.LocalDateTime;
import java.util.List;

import static solid.backend.docker.entity.QDockerExecution.dockerExecution;

/**
 * 도커 실행 레포지토리 QueryDSL 구현체
 * 
 * DockerExecutionRepositoryCustom 인터페이스에 정의된 복잡한 쿼리들을 QueryDSL로 구현합니다.
 * 
 * 주요 기능:
 * - 실행 중인 컨테이너 모니터링
 * - 대량 데이터 삭제 (로그 정리)
 * - 통계 데이터 수집
 * - 동적 조건 검색
 * 
 * @see DockerExecutionRepositoryCustom - 구현할 인터페이스
 * @see DockerExecutionJpaRepository - 이 구현체를 사용하는 메인 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class DockerExecutionJpaRepositoryImpl implements DockerExecutionRepositoryCustom {
    
    /** QueryDSL 쿼리 생성을 위한 팩토리 */
    private final JPAQueryFactory queryFactory;
    
    /**
     * 현재 실행 중인 모든 도커 컨테이너를 조회합니다.
     * 
     * 실행 중 판단 기준:
     * - status가 'PENDING' (대기 중) 또는 'RUNNING' (실행 중)
     * 
     * 정렬: 생성 시간 기준 내림차순 (최신 실행부터 표시)
     */
    @Override
    public List<DockerExecution> findRunningExecutions() {
        return queryFactory
                .selectFrom(dockerExecution)
                .where(dockerExecution.status.in("PENDING", "RUNNING"))
                .orderBy(dockerExecution.createdAt.desc())
                .fetch();
    }
    
    /**
     * 특정 날짜 이전에 생성된 오래된 실행 기록을 삭제합니다.
     * 
     * 대량 삭제 시 고려사항:
     * - 트랜잭션 크기: 데이터가 많을 경우 배치 처리 필요
     * - 영속성 컨텍스트: JPA 1차 캐시와 동기화되지 않음
     * - 성능: 인덱스가 createdAt 필드에 있는지 확인 필요
     * 
     * @return 삭제된 레코드 수
     */
    @Override
    public long deleteByCreatedAtBefore(LocalDateTime date) {
        return queryFactory
                .delete(dockerExecution)
                .where(dockerExecution.createdAt.before(date))
                .execute();
    }
    
    /**
     * 특정 컨테이너와 사용자에 대한 실행 통계를 조회합니다.
     * 
     * 동적 조건 처리:
     * - containerId가 null이면 해당 조건 무시
     * - memberId가 null이면 해당 조건 무시
     * - 둘 다 null이면 전체 데이터 조회
     * 
     * 통계 처리:
     * - 이 메서드는 데이터를 조회만 하고, 실제 통계 계산은 서비스 곀4층에서 수행
     * - 필요 시 GROUP BY와 집계 함수를 사용한 통계 쿼리로 확장 가능
     */
    @Override
    public List<DockerExecution> findExecutionStatistics(Long containerId, String memberId) {
        return queryFactory
                .selectFrom(dockerExecution)
                .where(
                    containerId != null ? dockerExecution.container.containerId.eq(containerId) : null,
                    memberId != null ? dockerExecution.member.memberId.eq(memberId) : null
                )
                .orderBy(dockerExecution.createdAt.desc())
                .fetch();
    }
    
    /**
     * 특정 기간 동안 특정 프로그래밍 언어의 실행 횟수를 조회합니다.
     * 
     * BETWEEN 연산자 사용:
     * - startDate와 endDate를 모두 포함하는 범위 검색
     * - SQL: WHERE created_at >= startDate AND created_at <= endDate
     * 
     * fetchCount() 사용:
     * - COUNT(*) 쿼리를 실행하여 건수만 반환
     * - 데이터 자체를 가져오지 않아 성능 최적화
     * 
     * @return 해당 조건에 맞는 실행 횟수
     */
    @Override
    public long countByLanguageAndDateRange(String language, LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(dockerExecution)
                .where(
                    dockerExecution.language.eq(language),
                    dockerExecution.createdAt.between(startDate, endDate)
                )
                .fetchCount();
    }
}