package solid.backend.Directory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryDelDto {
    private Integer directoryId;
    private Long containerId;
    private String directoryRoot;
    private String directoryName;
}
