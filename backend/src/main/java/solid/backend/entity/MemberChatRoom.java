package solid.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_chat_room")
public class MemberChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_chat_room_id")
    @Comment("멤버 채팅방ID")
    private Long memberChatRoomId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    public static MemberChatRoom memberJoinsChatRoom(Member member, Container container) {
        MemberChatRoom memberChatRoom = new MemberChatRoom();
        member.addMemberChatRoom(memberChatRoom);
        container.addMemberChatRoom(memberChatRoom);
        return memberChatRoom;
    }
}
