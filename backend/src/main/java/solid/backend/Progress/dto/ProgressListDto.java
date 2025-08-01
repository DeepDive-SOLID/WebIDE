package solid.backend.Progress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressListDto {
    private Integer progressId;
    private Integer directoryId;
    private Integer teamUserId;
    private Integer progressComplete;
    
    // 컨테이너 레벨 필드 추가
    private Integer directoryCount;
    private Integer averageProgress;
    private String memberId;
    private String memberName;
}
