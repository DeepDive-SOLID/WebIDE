package solid.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import solid.backend.common.enums.Authority;

import java.time.LocalDateTime;

/**
 * 그룹 멤버 엔티티
 * 그룹에 속한 각 멤버의 정보와 권한을 관리
 * 멤버의 활동 시간을 추적하여 6개월 미활동 시 자동 탈퇴 처리
 */
@Entity
@Table(name = "그룹 회원")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_USER_ID")
    private Long groupUserId; // 그룹 멤버 고유 ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private Group group; // 소속된 그룹
    
    @Column(name = "GROUP_AUTH_ID", nullable = false, length = 10)
    private String groupAuthId; // 권한 코드 (ROOT/USER)
    
    @Transient
    private Authority authority; // 멤버 권한 Enum
    
    @PostLoad
    private void loadAuthority() {
        if (this.groupAuthId != null) {
            this.authority = Authority.valueOf(this.groupAuthId);
        }
    }
    
    @PrePersist
    @PreUpdate
    private void saveAuthority() {
        if (this.authority != null) {
            this.groupAuthId = this.authority.name();
        }
    }
    
    // Authority getter/setter
    public Authority getAuthority() {
        return authority;
    }
    
    public void setAuthority(Authority authority) {
        this.authority = authority;
        this.groupAuthId = authority != null ? authority.name() : null;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member; // 그룹 멤버
    
    @Column(name = "JOINED_DATE", nullable = false)
    @Builder.Default
    private LocalDateTime joinedDate = LocalDateTime.now(); // 그룹 가입 날짜
    
    @Column(name = "LAST_ACTIVITY_DATE", nullable = false)
    @Builder.Default
    private LocalDateTime lastActivityDate = LocalDateTime.now(); // 마지막 활동 날짜 (6개월 자동 탈퇴 기준)
}