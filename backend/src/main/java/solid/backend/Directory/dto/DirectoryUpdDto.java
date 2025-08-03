package solid.backend.Directory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryUpdDto {
    private Integer directoryId;
    private String oldDirectoryName;
    private String directoryName;
}
