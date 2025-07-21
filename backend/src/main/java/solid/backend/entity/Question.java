package solid.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.util.List;

@Data
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    @Comment("문제 ID")
    private Integer questionId;

    @Column(name = "question_title", length = 50, nullable = false)
    @Comment("문제 제목")
    private String questionTitle;

    @Column(name = "question_content", columnDefinition = "TEXT", nullable = false)
    @Comment("문제 내용")
    private String questionContent;

    @Column(name = "question_limit_time", nullable = false)
    @Comment("시간 제한")
    private Integer questionLimitTime;

    @Column(name = "question_limit_memory", nullable = false)
    @Comment("메모리 제한")
    private Integer questionLimitMemory;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCase> testCases;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results;
}