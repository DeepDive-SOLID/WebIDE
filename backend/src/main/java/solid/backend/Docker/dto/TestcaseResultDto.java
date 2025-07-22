package solid.backend.Docker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseResultDto {
    private double time;
    private String mem;
    private String input;
    private String output;
    private String actual;
    private Boolean pass;
}
