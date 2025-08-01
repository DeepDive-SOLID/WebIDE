package solid.backend.chat.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.chat.dto.ChatDto;
import solid.backend.chat.dto.ChatListDto;
import solid.backend.chat.repository.ChatQueryRepository;
import solid.backend.entity.Chat;
import solid.backend.entity.Member;
import solid.backend.entity.Team;
import solid.backend.entity.TeamUser;
import solid.backend.jpaRepository.*;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    public static final String CHAT_DESTINATION_PREFIX = "/topic/chatRooms/";
    public static final String HAS_JOINED = "님이 참가하였습니다.";
    public static final String HAS_LEFT = "님이 나갔습니다.";

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TeamRepository teamRepository;
    private final ChatQueryRepository chatQueryRepository;
    private final TeamUserRepository teamUserRepository;

    /**
     * 설명 : 메시지 저장 및 전송 데이터 가공
     * @param memberId 사용자 ID
     * @param chatRoomId 그룹 ID
     * @param content 메시지 내용
     */
    @Override
    @Transactional
    public void sendChatMessage(String memberId, Integer chatRoomId, String content) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Team team = teamRepository.findById(chatQueryRepository.findTeamId(chatRoomId)).orElseThrow();
        Chat chatMessage = Chat.builder()
                .chatText(content)
                .chatType("CHAT")
                .chatDate(LocalDate.now())
                .member(member)
                .team(team)
                .build();

        // 메시지 데이터 저장 및 전송
        publish(chatMessage, chatRoomId);
    }

    /**
     * 설명 : 메시지 리스트 조회
     * @param chatRoomId 그룹 ID
     * @return List<MessageListDto>
     */
    @Override
    public List<ChatListDto> getChatList(Integer chatRoomId) {
        return chatQueryRepository.getChatList(chatQueryRepository.findTeamId(chatRoomId));
    }

    /**
     * 설명 : 채팅방 입장 시 메세지 전송 및 참여자 데이터 추가
     * @param memberId 사용자 ID
     * @param chatRoomId 그룹 ID
     */
    @Override
    @Transactional
    public void joinChatRoom(String memberId, Integer chatRoomId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Team team = teamRepository.findById(chatQueryRepository.findTeamId(chatRoomId)).orElseThrow();

        // 채팅에 참여중인 사용자인지 확인
        if (chatQueryRepository.isOnlineCheck(memberId, team.getTeamId())) {
            return;
        }

        // 채팅에 참여중인 사용자 정보 조회
        TeamUser teamUser = chatQueryRepository.findByTeamUser(memberId, team.getTeamId());

        // 채팅에 참여중인 사용자 상태 수정
        teamUser.setTeamUserIsOnline(true);
        teamUserRepository.save(teamUser);

        // 채팅방에 참여 했다는 메시지 전송
        sendMessage(member, team, HAS_JOINED, chatRoomId);
    }

    /**
     * 설명 : 연결이 끊긴 사용자 퇴장 처리
     * @param userId 사용자 ID
     */
    @Override
    @Transactional
    public void disconnect(String userId) {
        Member member = memberRepository.findById(userId).orElseThrow();

        // 채팅에 참여중인 사용자 리스트 찾기
        List<TeamUser> teamUsers = chatQueryRepository.findByUser(userId);
        for (TeamUser teamUser : teamUsers) {
            // 채팅에 참여중인 사용자 상태 수정
            teamUser.setTeamUserIsOnline(false);
            teamUserRepository.save(teamUser);

            // 채팅에 참여중인 컨테이너 ID 조회
            Integer chatRoomId = chatQueryRepository.findByChatRoomId(teamUser.getTeam().getTeamId());

            // 채팅방에 퇴장 했다는 메시지 전송
            sendMessage(member, teamUser.getTeam(), HAS_LEFT, chatRoomId);
        }

        // 회원 상태 수정
        member.setMemberIsOnline(false);
        memberRepository.save(member);
    }

    /**
     * 설명 : 채팅방 퇴장 시 메세지 전송 및 참여자 데이터 삭제
     * @param memberId 사용자 ID
     * @param chatRoomId 그룹 ID
     */
    @Override
    @Transactional
    public void unsubscribe(String memberId, Integer chatRoomId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Team team = teamRepository.findById(chatQueryRepository.findTeamId(chatRoomId)).orElseThrow();

        // 채팅에 참여중인 사용자 정보 조회
        TeamUser teamUser = chatQueryRepository.findByTeamUser(memberId, team.getTeamId());

        // 채팅에 참여중인 사용자 상태 수정
        teamUser.setTeamUserIsOnline(false);
        teamUserRepository.save(teamUser);

        // 채팅방에 퇴장 했다는 메시지 전송
        sendMessage(member, team, HAS_LEFT, chatRoomId);
    }

    /**
     * 설명 : 채팅방에 입/퇴장 했다는 메시지 전송
     * @param member 사용자 ID
     * @param team 그룹 ID
     */
    @Transactional
    public void sendMessage(Member member, Team team, String str, Integer chatRoomId) {
        Chat leftMessage = Chat.builder()
                .chatText(member.getMemberName() + str)
                .chatType("SYSTEM")
                .chatDate(LocalDate.now())
                .member(member)
                .team(team)
                .build();

        // 메시지 데이터 저장 및 전송
        publish(leftMessage, chatRoomId);
    }

    /**
     * 설명 : 메시지 저장 및 전송
     * @param chat 메시지
     */
    @Transactional
    public void publish(Chat chat, Integer chatRoomId) {
        chatRepository.save(chat);
        simpMessagingTemplate.convertAndSend(CHAT_DESTINATION_PREFIX + chatRoomId, new ChatDto(chat));
    }
}
