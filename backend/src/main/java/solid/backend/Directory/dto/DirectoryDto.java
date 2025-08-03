package solid.backend.Directory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectoryDto {
    private Integer directoryId;
    private Integer containerId;
    private Integer teamId;
    private String directoryName;
    private String directoryRoot;
    private Boolean hasQuestion;
}
