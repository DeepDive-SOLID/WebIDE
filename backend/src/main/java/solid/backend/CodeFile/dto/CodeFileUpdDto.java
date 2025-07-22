package solid.backend.CodeFile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeFileUpdDto {
    private Integer codeFileId;
    private String codeContent;
}
