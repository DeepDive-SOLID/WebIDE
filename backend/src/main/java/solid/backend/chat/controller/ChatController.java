package solid.backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solid.backend.chat.dto.ChatListDto;
import solid.backend.chat.service.ChatService;
import solid.backend.common.ApiResponse;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 설명 : 메시지 저장 및 전송
     * @param content 메시지 내용
     * @param accessor WebSocket 세션의 헤더 정보
     * @param chatRoomId 그룹 ID
     */
    @MessageMapping("/chatRooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> sendChatMessage(String content, SimpMessageHeaderAccessor accessor, @DestinationVariable("chatRoomId") Integer chatRoomId) {
        if (accessor.getSessionAttributes() == null || !accessor.getSessionAttributes().containsKey("memberId")) {
            throw new IllegalStateException("No SessionAttributes or memberId. sessionId: " + accessor.getSessionId());
        }
        String memberId = (String) accessor.getSessionAttributes().get("memberId");
        chatService.sendChatMessage(memberId, chatRoomId, content);
        return ResponseEntity.ok(ApiResponse.successMessage("메시지가 전송되었습니다."));
    }

    /**
     * 설명 : 메시지 리스트 조회
     * @param chatRoomId 그룹 ID
     * @return List<MessageListDto>
     */
    @ResponseBody
    @GetMapping("/api/chatRooms/{chatRoomId}/chatList")
    public ResponseEntity<ApiResponse<List<ChatListDto>>> getChatList(@PathVariable("chatRoomId") Integer chatRoomId) {
        List<ChatListDto> response = chatService.getChatList(chatRoomId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 설명 : 채팅방 입장 시 메세지 전송 및 참여자 데이터 추가
     * @param chatRoomId 그룹 ID
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @PostMapping("/api/chatRooms/{chatRoomId}/join")
    public ResponseEntity<ApiResponse<Void>> joinChatRoom(@PathVariable("chatRoomId") Integer chatRoomId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            memberId = authentication.getName();
        }
        chatService.joinChatRoom(memberId, chatRoomId);
        return ResponseEntity.ok(ApiResponse.successMessage("채팅방에 입장되었습니다"));
    }
}
