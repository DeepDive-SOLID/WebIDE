package solid.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import solid.backend.common.enums.ContainerVisibility;

import java.time.LocalDate;

/**
 * 컨테이너 엔티티
 * Web IDE에서 사용자가 생성하는 프로젝트 작업 공간
 * 각 컨테이너는 하나의 그룹을 가지며, 여러 멤버가 참여 가능
 */
@Entity
@Table(name = "container")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Container {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "container_id")
    private Long containerId; // 컨테이너 고유 ID
    
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team; // 컨테이너가 속한 팀
    
    @Column(name = "container_auth", nullable = false)
    private Boolean containerAuth; // true: public / false: private
    
    @Column(name = "container_date", nullable = false)
    @Builder.Default
    private LocalDate containerDate = LocalDate.now(); // 컨테이너 생성 날짜
    
    @Column(name = "container_nm", nullable = false, length = 20)
    private String containerName; // 컨테이너 이름
    
    @Column(name = "container_content", length = 200)
    private String containerContent; // 컨테이너 설명
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner; // 컨테이너 소유자 (항상 ROOT 권한)
    
    // visibility를 계산된 속성으로 제공
    public ContainerVisibility getVisibility() {
        return this.containerAuth != null && this.containerAuth ? 
            ContainerVisibility.PUBLIC : ContainerVisibility.PRIVATE;
    }
    
    public void setVisibility(ContainerVisibility visibility) {
        this.containerAuth = visibility == ContainerVisibility.PUBLIC;
    }
}