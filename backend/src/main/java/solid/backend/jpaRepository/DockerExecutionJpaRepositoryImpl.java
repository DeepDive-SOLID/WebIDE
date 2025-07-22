package solid.backend.jpaRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solid.backend.docker.entity.DockerExecution;
import solid.backend.docker.entity.QDockerExecution;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 도커 실행 기록 커스텀 레포지토리 구현체
 * 
 * QueryDSL을 사용하여 복잡한 쿼리를 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class DockerExecutionJpaRepositoryImpl implements DockerExecutionRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public long deleteByCreatedAtBefore(LocalDateTime dateTime) {
        QDockerExecution dockerExecution = QDockerExecution.dockerExecution;
        
        return queryFactory
                .delete(dockerExecution)
                .where(dockerExecution.createdAt.before(dateTime))
                .execute();
    }
    
    @Override
    public List<DockerExecution> findRunningExecutions(String memberId) {
        QDockerExecution dockerExecution = QDockerExecution.dockerExecution;
        
        var query = queryFactory
                .selectFrom(dockerExecution)
                .where(dockerExecution.status.eq("RUNNING"));
        
        if (memberId != null) {
            query.where(dockerExecution.member.memberId.eq(memberId));
        }
        
        return query
                .orderBy(dockerExecution.createdAt.desc())
                .fetch();
    }
    
    @Override
    public List<DockerExecution> findExecutionStatistics(Long containerId, LocalDateTime startDate, LocalDateTime endDate) {
        QDockerExecution dockerExecution = QDockerExecution.dockerExecution;
        
        var query = queryFactory
                .selectFrom(dockerExecution)
                .where(dockerExecution.createdAt.between(startDate, endDate));
        
        if (containerId != null) {
            query.where(dockerExecution.container.containerId.eq(containerId));
        }
        
        return query
                .orderBy(dockerExecution.createdAt.desc())
                .fetch();
    }
}