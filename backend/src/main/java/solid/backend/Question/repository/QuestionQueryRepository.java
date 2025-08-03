package solid.backend.Question.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import solid.backend.Question.dto.QuestionListDto;
import solid.backend.entity.QQuestion;

import java.util.List;

@Repository
public class QuestionQueryRepository {
    private final JPAQueryFactory queryFactory;

    public QuestionQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<QuestionListDto> getQuestionListByContainerId(Integer containerId) {
        QQuestion question = new QQuestion("question");

        return queryFactory
                .select(Projections.constructor(QuestionListDto.class,
                        question.questionId,
                        question.container.containerId,
                        question.team.teamId,
                        question.directory.directoryId,
                        question.questionTitle,
                        question.questionDescription,
                        question.question,
                        question.questionInput,
                        question.questionOutput,
                        question.questionTime,
                        question.questionMem
                ))
                .from(question)
                .where(question.container.containerId.eq(containerId))
                .fetch();
    }
}
