package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import solid.backend.entity.*;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {
    List<Result> findByQuestionAndMemberAndTestCase(Question question, Member member, TestCase testCase);
    
    List<Result> findByQuestionAndMember(Question question, Member member);
    
    @Modifying
    int deleteByQuestion(Question question);
}
