package solid.backend.Progress.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import solid.backend.Progress.dto.ProgressListDto;
import solid.backend.entity.QProgress;
import solid.backend.entity.QTeamUser;
import solid.backend.entity.QMember;

import java.util.List;

@Repository
public class ProgressQueryRepository {
    private final JPAQueryFactory queryFactory;

    public ProgressQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<ProgressListDto> getProgressListByDirectoryId(Integer directoryId) {
        QProgress progress = QProgress.progress;

        return queryFactory
                .select(Projections.constructor(ProgressListDto.class,
                        progress.progressId,
                        progress.directory.directoryId,
                        progress.teamUser.teamUserId,
                        progress.progressComplete,
                        progress.teamUser.member.memberId,
                        progress.teamUser.member.memberName,
                        com.querydsl.core.types.dsl.Expressions.constant(1),  // directoryCount (단일 디렉토리이므로 1)
                        progress.progressComplete,  // averageProgress (단일 디렉토리이므로 그대로 사용)
                        com.querydsl.core.types.dsl.Expressions.nullExpression(String.class)  // language (여기서는 null로 설정, 서비스에서 설정)
                ))
                .from(progress)
                .where (progress.directory.directoryId.eq(directoryId))
                .fetch();
    }
}
