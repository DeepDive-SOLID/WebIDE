package solid.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "member")
public class Member {

    @Id
    @Column(name = "member_id", length = 20)
    @Comment("회원 ID")
    private String memberId;

    @Column(name = "member_name", nullable = false, length = 10)
    @Comment("회원명")
    private String memberName;

    @Column(name = "member_pw", nullable = false, length = 100)
    @Comment("회원 비밀번호")
    private String memberPassword;

    @Column(name = "member_email", nullable = false, length = 30)
    @Comment("회원 이메일")
    private String memberEmail;

    @Column(name = "member_phone", length = 15)
    @Comment("회원 전화번호")
    private String memberPhone;

    @Column(name = "member_birth")
    @Comment("회원 생년월일")
    private LocalDate memberBirth;

    @Column(name = "member_img", length = 500)
    @Comment("회원 이미지")
    private String memberImg;

    @Column(name = "member_is_online", nullable = false)
    @Comment("회원 접속 여부")
    private boolean memberIsOnline = false;
}
