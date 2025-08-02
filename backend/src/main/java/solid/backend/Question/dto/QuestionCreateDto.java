package solid.backend.Question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateDto {
    private Integer containerId;
    private Integer teamId;
    private String questionTitle; // 파일명?
    private String questionDescription;
    private String question;
    private String questionInput;
    private String questionOutput;
    private Float questionTime;
    private Integer questionMem;
    private List<TestCaseDto> testcases;
}
