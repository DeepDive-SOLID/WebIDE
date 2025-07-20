package solid.backend.CodeFile.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeFileListDto {
    private Integer codeFileId;
    private Integer directoryId;
    private String codeFilePath;
    private String codeFileName;
    private LocalDate codeFileUploadDt;
    private LocalDate codeFileCreateDt;
}
