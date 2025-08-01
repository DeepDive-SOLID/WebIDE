package solid.backend.CodeFile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeFileSaveDto {
    private Integer directoryId;
    private String codeFileName;
    private String codeContent;
}
