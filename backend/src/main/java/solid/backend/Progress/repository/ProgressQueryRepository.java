package solid.backend.Progress.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import solid.backend.Progress.dto.ProgressListDto;
import solid.backend.entity.QProgress;

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
                        progress.progressComplete
                ))
                .from(progress)
                .where (progress.directory.directoryId.eq(directoryId))
                .fetch();
    }
}
