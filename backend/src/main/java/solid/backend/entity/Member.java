package solid.backend.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "회원")
public class Member {

    @Id
    @Column(name = "MEMBER_ID", length = 20)
    @Comment("회원 ID")
    private String memberId;

    @Column(name = "MEMBER_NAME", nullable = false, length = 10)
    @Comment("회원명")
    private String memberName;

    @Column(name = "MEMBER_PW", nullable = false, length = 100)
    @Comment("회원 비밀번호")
    private String memberPassword;

    @Column(name = "MEMBER_EMAIL", nullable = false, length = 30)
    @Comment("회원 이메일")
    private String memberEmail;

    @Column(name = "MEMBER_PHONE", length = 15)
    @Comment("회원 전화번호")
    private String memberPhone;

    @Column(name = "MEMBER_BIRTH")
    @Comment("회원 생년월일")
    private LocalDate memberBirth;

    @Column(name = "MEMBER_IMG", length = 500)
    @Comment("회원 이미지")
    private String memberImg;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Container> ownedContainers = new ArrayList<>();
}
