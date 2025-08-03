package solid.backend.Question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseListDto {
    private Integer caseId;
    private String caseEx;
    private String caseAnswer;
}
