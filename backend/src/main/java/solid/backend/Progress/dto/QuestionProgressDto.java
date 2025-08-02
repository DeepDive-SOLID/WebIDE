package solid.backend.Progress.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionProgressDto {
    private Integer questionId;
    private String questionTitle;
    private Integer passedTestCases;
    private Integer totalTestCases;
    private Integer progressPercentage;
}