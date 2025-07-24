package solid.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Data
@Entity
@Table(name = "test_case")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    @Comment("케이스 ID")
    private Integer caseId;

    @Column(name = "case_ex", length = 200, nullable = false)
    @Comment("예제")
    private String caseEx;

    @Column(name = "case_answer", length = 200, nullable = false)
    @Comment("정답")
    private String caseAnswer;

    @Column(name = "case_check", nullable = false)
    @Comment("true: 문제에 보여짐 & 채점시 적용, false: 채점시 적용")
    private Boolean caseCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
}
