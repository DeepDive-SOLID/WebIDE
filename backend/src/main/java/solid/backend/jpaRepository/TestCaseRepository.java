package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import solid.backend.entity.TestCase;
import solid.backend.entity.Question;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Integer> {
    List<TestCase> findByQuestion_QuestionId(Integer questionId);
    
    @Modifying
    int deleteByQuestion(Question question);
}
