package solid.backend.chat.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import solid.backend.chat.message.domain.MessageType;
import solid.backend.entity.Message;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private MessageType type;
    private String content;
    private String memberId;
    private String memberName;

    public MessageDto(Message message) {
        this.type = message.getMessageType();
        this.content = message.getMessageContent();
        this.memberId = message.getMember().getMemberId();
        this.memberName = message.getMember().getMemberName();
    }
}
