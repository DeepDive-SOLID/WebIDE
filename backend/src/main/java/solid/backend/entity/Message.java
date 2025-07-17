package solid.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import solid.backend.chat.message.domain.MessageType;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue
    @Column(name = "message_id")
    @Comment("메세지 ID")
    private Long messageId;

    @Column(name = "message_content")
    @Comment("메세지 내용")
    private String messageContent;

    @Column(name = "message_type")
    @Comment("메세지 타입")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id")
    private Container container;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Message createChatMessage(Member member, Container container, String content) {
        return Message.builder()
                .messageContent(content)
                .messageType(MessageType.CHAT)
                .member(member)
                .container(container)
                .build();
    }

    public static Message createSystemMessage(Member member, Container container, String content) {
        return Message.builder()
                .messageContent(content)
                .messageType(MessageType.SYSTEM)
                .member(member)
                .container(container)
                .build();
    }
}
