package solid.backend.Docker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockerRunDto {
    private String memberId;
    private Integer codeFileId;
    private Integer questionId;
}
