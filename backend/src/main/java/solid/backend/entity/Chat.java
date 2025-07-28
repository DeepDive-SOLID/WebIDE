package solid.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    @Comment("채팅 ID")
    private Integer chatId;

    @Column(name = "chat_text", length = 50, nullable = false)
    @Comment("채팅 내용")
    private String chatText;

    @Column(name = "chat_data", nullable = false)
    @Comment("채팅 날짜")
    private LocalDate chatData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

}
