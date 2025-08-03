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
    
    // 팀원 현황용 추가 필드
    private String memberId;
    private String memberName;
    private Integer directoryCount;
    private Integer averageProgress;
    private String language; // 가장 최근 사용한 언어
}