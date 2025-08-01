package solid.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatListDto {

    private Integer chatId;
    private String chatText;
    private String chatType;
    private LocalDate chatDate;
    private String memberId;
    private Integer teamId;
}
