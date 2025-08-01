package solid.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "chat")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    @Comment("채팅 ID")
    private Integer chatId;

    @Column(name = "chat_text", length = 50, nullable = false)
    @Comment("채팅 내용")
    private String chatText;

    @Column(name = "chat_type", length = 20)
    @Comment("채팅 타입")
    private String chatType;

    @Column(name = "chat_date", nullable = false)
    @Comment("채팅 날짜")
    private LocalDate chatDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

}
