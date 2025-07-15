package solid.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Data
@Entity
@Table(name = "result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    @Comment("결과 ID")
    private Integer resultId;

    @Column(name = "result_answer",length = 20 , nullable = false)
    @Comment("실행 결과")
    private String resultAnswer;

    @Column(name = "result_time", nullable = false)
    @Comment("실행 시간")
    private Float resultTime;

    @Column(name = "result_memory", nullable = false)
    @Comment("사용 메모리양")
    private Integer resultMemory;

    @Column(name = "result_lang", length = 20, nullable = false)
    @Comment("사용 언어")
    private String resultLang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private TestCase testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;



}
