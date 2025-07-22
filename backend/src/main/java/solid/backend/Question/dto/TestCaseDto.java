package solid.backend.Question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseDto {
    private String caseEx;
    private String caseAnswer;
    private Boolean caseCheck;
}
