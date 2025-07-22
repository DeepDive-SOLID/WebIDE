package solid.backend.Directory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryDto {
    private Integer directoryId;
    private Long containerId;
    private Integer teamId;
    private String directoryName;
    private String directoryRoot;
}
