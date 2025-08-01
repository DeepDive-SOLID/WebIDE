package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.TestCase;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Integer> {
    List<TestCase> findByQuestion_QuestionId(Integer questionId);
}
