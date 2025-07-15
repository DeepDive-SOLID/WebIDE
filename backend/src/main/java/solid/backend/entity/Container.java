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
@Table(name = "컨테이너")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Container {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTAINER_ID")
    private Long containerId; // 컨테이너 고유 ID
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private Group group; // 컨테이너와 1:1 매핑되는 그룹
    
    @Column(name = "CONTAINER_AUTH", nullable = false)
    private Boolean containerAuth; // true: public / false: private
    
    @Transient
    private ContainerVisibility visibility; // 컨테이너 공개 범위 (PUBLIC/PRIVATE)
    
    @PostLoad
    private void loadVisibility() {
        this.visibility = this.containerAuth ? ContainerVisibility.PUBLIC : ContainerVisibility.PRIVATE;
    }
    
    @PrePersist
    @PreUpdate
    private void saveContainerAuth() {
        if (this.visibility != null) {
            this.containerAuth = this.visibility == ContainerVisibility.PUBLIC;
        }
    }
    
    @Column(name = "CONTAINER_DATE", nullable = false)
    @Builder.Default
    private LocalDate containerDate = LocalDate.now(); // 컨테이너 생성 날짜
    
    @Column(name = "CONTAINER_NM", nullable = false, length = 20)
    private String containerName; // 컨테이너 이름
    
    @Column(name = "CONTAINER_CONTENT", length = 200)
    private String containerContent; // 컨테이너 설명
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private Member owner; // 컨테이너 소유자 (항상 ROOT 권한)
    
    // Visibility getter/setter 추가
    public ContainerVisibility getVisibility() {
        if (this.visibility == null && this.containerAuth != null) {
            this.visibility = this.containerAuth ? ContainerVisibility.PUBLIC : ContainerVisibility.PRIVATE;
        }
        return this.visibility;
    }
    
    public void setVisibility(ContainerVisibility visibility) {
        this.visibility = visibility;
        this.containerAuth = visibility == ContainerVisibility.PUBLIC;
    }
}