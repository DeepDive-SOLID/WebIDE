package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 컨테이너 멤버 정보 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerMemberDto {
    
    /** 멤버 ID */
    private String memberId;
    
    /** 멤버 이름 */
    private String memberName;
    
    /** 접속 여부 */
    private Boolean isOnline;
    
    /** 권한 (ROOT/USER) */
    private String authority;
    
    /** 마지막 활동 시간 */
    private LocalDateTime lastActivityDate;
    
    /** 가입일 */
    private LocalDateTime joinedDate;
}