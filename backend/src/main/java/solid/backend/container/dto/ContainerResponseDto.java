package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import solid.backend.entity.Container;
import solid.backend.common.enums.ContainerVisibility;

import java.time.LocalDate;

/**
 * 컨테이너 응답 DTO
 * 컨테이너 조회 시 반환하는 정보를 담는 객체
 * 사용자의 권한 정보와 멤버 수 등 추가 정보 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerResponseDto {
    
    private Long containerId; // 컨테이너 ID
    private String containerName; // 컨테이너 이름
    private String containerContent; // 컨테이너 설명
    private ContainerVisibility visibility; // 공개 범위
    private LocalDate containerDate; // 생성 날짜
    private String ownerName; // 소유자 이름
    private String ownerId; // 소유자 ID
    private Integer memberCount; // 컨테이너 멤버 수
    private String userAuthority; // 현재 사용자의 권한 (ROOT/USER/null)
    
    /**
     * 컨테이너 엔티티를 DTO로 변환
     * @param container 컨테이너 엔티티
     * @param userAuthority 현재 사용자의 권한
     * @param memberCount 컨테이너 멤버 수
     * @return ContainerResponseDto
     */
    public static ContainerResponseDto from(Container container, String userAuthority, int memberCount) {
        return ContainerResponseDto.builder()
                .containerId(container.getContainerId())
                .containerName(container.getContainerName())
                .containerContent(container.getContainerContent())
                .visibility(container.getVisibility())
                .containerDate(container.getContainerDate())
                .ownerName(container.getOwner().getMemberName())
                .ownerId(container.getOwner().getMemberId())
                .memberCount(memberCount)
                .userAuthority(userAuthority)
                .build();
    }
}