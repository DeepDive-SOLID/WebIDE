package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Question;
import solid.backend.entity.Container;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByContainer_ContainerId(Integer containerId);
    List<Question> findByContainer(Container container);
}
