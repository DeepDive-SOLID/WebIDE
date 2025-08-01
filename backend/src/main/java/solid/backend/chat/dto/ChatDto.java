package solid.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import solid.backend.entity.Chat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {

    private String chatType;
    private String chatText;
    private String memberId;
    private LocalDate chatDate;

    public ChatDto(Chat chat) {
        this.chatType = chat.getChatType();
        this.chatText = chat.getChatText();
        this.chatDate = chat.getChatDate();
        this.memberId = chat.getMember().getMemberId();
    }
}
