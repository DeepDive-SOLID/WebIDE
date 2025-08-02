package solid.backend.Question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionListDto {
    private Integer questionId;
    private Integer containerId;
    private Integer teamId;
    private Integer directoryId; // 추가
    private String questionTitle;
    private String questionDescription;
    private String question;
    private String questionInput;
    private String questionOutput;
    private Float questionTime;
    private Integer questionMem;
}
