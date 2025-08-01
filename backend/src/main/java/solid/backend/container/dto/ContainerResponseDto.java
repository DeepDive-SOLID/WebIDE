package solid.backend.container.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solid.backend.entity.Container;

import java.time.LocalDate;

/**
 * 컨테이너 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerResponseDto {
    
    /** 컨테이너 고유 식별자 */
    private Integer containerId;
    /** 컨테이너 이름 */
    private String containerName;
    /** 컨테이너 설명 */
    private String containerContent;
    /** 공개 여부 (true: 공개, false: 비공개) */
    private Boolean isPublic;
    /** 컨테이너 생성일 */
    private LocalDate containerDate;
    /** 소유자 이름 */
    private String ownerName;
    /** 소유자 ID */
    private String ownerId;
    /** 현재 컨테이너의 멤버 수 */
    private Integer memberCount;
    /** 현재 사용자의 권한 (ROOT/USER/null) */
    private String userAuthority;
    /** 컨테이너가 속한 팀 ID */
    private Integer teamId;
    
    // 선택적 필드 - 특정 응답에서만 사용
    /** 응답 메시지 (선택적) */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    /** 리다이렉트 URL (선택적) */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String redirectUrl;
    
    /**
     * 컨테이너 엔티티를 DTO로 변환
     * @param container 컨테이너 엔티티
     * @param userAuthority 현재 사용자의 권한
     * @param memberCount 컨테이너 멤버 수
     * @return ContainerResponseDto
     */
    public static ContainerResponseDto from(Container container, String userAuthority, int memberCount) {
        if (container == null) {
            throw new IllegalArgumentException("Container cannot be null");
        }
        
        // Find owner from TeamUser with ROOT authority
        var owner = container.getTeam().getTeamUsers().stream()
                .filter(tu -> "ROOT".equals(tu.getTeamAuth().getAuthId()))
                .findFirst()
                .orElse(null);
        
        String ownerName = owner != null ? owner.getMember().getMemberName() : "Unknown";
        String ownerId = owner != null ? owner.getMember().getMemberId() : null;
        
        return ContainerResponseDto.builder()
                .containerId(container.getContainerId())
                .containerName(container.getContainerName())
                .containerContent(container.getContainerContent())
                .isPublic(container.getContainerAuth())
                .containerDate(container.getContainerDate())
                .ownerName(ownerName)
                .ownerId(ownerId)
                .memberCount(memberCount)
                .userAuthority(userAuthority)
                .teamId(container.getTeam().getTeamId())
                .build();
    }
}