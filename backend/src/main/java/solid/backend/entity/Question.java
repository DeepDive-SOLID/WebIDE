package solid.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
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

    @Column(name = "question_title", length = 30, nullable = false)
    @Comment("제목")
    private String questionTitle;

    @Column(name = "question_description", length = 300, nullable = false)
    @Comment("내용")
    private String questionDescription;

    @Column(name = "question", length = 300, nullable = false)
    @Comment("제한사항")
    private String question;

    @Column(name = "question_input", length = 100, nullable = false)
    @Comment("입력 예시")
    private String questionInput;

    @Column(name = "question_output", length = 100, nullable = false)
    @Comment("출력 예시")
    private String questionOutput;

    @Column(name = "question_time", nullable = false)
    @Comment("시간 제한")
    private Float questionTime;

    @Column(name = "question_mem", nullable = false)
    @Comment("메모리 제한")
    private Integer questionMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id")
    private Container container;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TestCase> testCases = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results = new ArrayList<>();
}