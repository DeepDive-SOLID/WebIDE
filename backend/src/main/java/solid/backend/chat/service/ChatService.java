package solid.backend.chat.service;

import solid.backend.chat.dto.ChatListDto;

import java.util.List;

public interface ChatService {

    /**
     * 설명 : 메시지 저장 및 전송
     * @param memberId 사용자 ID
     * @param chatRoomId 그룹 ID
     * @param content 메시지 내용
     */
    void sendChatMessage(String memberId, Integer chatRoomId, String content);

    /**
     * 설명 : 메시지 리스트 조회
     * @param chatRoomId 그룹 ID
     * @return List<MessageListDto>
     */
    List<ChatListDto> getChatList(Integer chatRoomId);

    /**
     * 설명 : 채팅방 입장 시 메세지 전송 및 참여자 데이터 추가
     * @param memberId 사용자 ID
     * @param chatRoomId 그룹 ID
     */
    void joinChatRoom(String memberId, Integer chatRoomId);

    /**
     * 설명 : 연결이 끊긴 사용자 퇴장 처리
     * @param memberId 사용자 ID
     */
    void disconnect(String memberId);

    /**
     * 설명 : 채팅방 퇴장 시 메세지 전송 및 참여자 데이터 삭제
     * @param memberId 사용자 ID
     * @param chatRoomId 그룹 ID
     */
    void unsubscribe(String memberId, Integer chatRoomId);
}
