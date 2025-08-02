package solid.backend.Progress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDto {
    private Integer directoryId;
    private Integer teamUserId;
    private Integer progressComplete;
}