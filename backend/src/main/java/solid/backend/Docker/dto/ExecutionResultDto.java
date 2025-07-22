package solid.backend.Docker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResultDto {
    private String language;
    private Boolean isCorrect;
    private List<TestcaseResultDto> testcaseResults;
}
