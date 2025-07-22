package solid.backend.Docker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomInputDto {
    private Integer codeFileId;
    private Integer questionId;
    private String input;
}
