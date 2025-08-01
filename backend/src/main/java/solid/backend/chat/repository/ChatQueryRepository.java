package solid.backend.chat.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solid.backend.chat.dto.ChatListDto;
import solid.backend.entity.*;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatQueryRepository {

    private final JPAQueryFactory query;

    /**
     * 설명 : 메시지 리스트 조회
     * @param chatRoomId 그룹 ID
     * @return List<MessageListDto>
     */
    public List<ChatListDto> getChatList(Integer chatRoomId) {
        QChat chat = QChat.chat;
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        return query
                .select(Projections.constructor(
                        ChatListDto.class,
                        chat.chatId,
                        chat.chatText,
                        chat.chatType,
                        chat.chatDate,
                        member.memberId,
                        team.teamId
                ))
                .from(chat)
                .join(chat.member, member)
                .join(chat.team, team)
                .where(chat.team.teamId.eq(chatRoomId))
                .fetch();

    }

    /**
     * 설명 : 채팅에 참여중인 사용자인지 확인
     * @param memberId 사용자 ID
     * @param chatRoomId 그룹 ID
     * @return Boolean
     */
    public Boolean isOnlineCheck(String memberId, Integer chatRoomId) {
        QTeam team = QTeam.team;
        QTeamUser teamUser = QTeamUser.teamUser;
        QMember member = QMember.member;

        Boolean result = query.select(teamUser.teamUserIsOnline)
                .from(teamUser)
                .join(teamUser.team, team)
                .join(teamUser.member, member)
                .where(
                        team.teamId.eq(chatRoomId),
                        member.memberId.eq(memberId)
                )
                .fetchOne();

        return result != null ? result : false;
    }

    /**
     * 설명 : 채팅에 참여중인 사용자 찾기
     * @param memberId 사용자 ID
     * @param chatRoomId 그룹 ID
     * @return TeamUser
     */
    public TeamUser findByTeamUser(String memberId, Integer chatRoomId) {
        QTeam team = QTeam.team;
        QTeamUser teamUser = QTeamUser.teamUser;
        QMember member = QMember.member;

        return query.select(teamUser)
                .from(teamUser)
                .join(teamUser.team, team)
                .join(teamUser.member, member)
                .where(team.teamId.eq(chatRoomId))
                .where(member.memberId.eq(memberId))
                .fetchOne();
    }

    /**
     * 설명 : 채팅에 참여중인 사용자 리스트 찾기
     * @param memberId 사용자 ID
     * @return List<TeamUser>
     */
    public List<TeamUser> findByUser(String memberId) {
        QMember member = QMember.member;
        QTeamUser teamUser = QTeamUser.teamUser;

        return query.select(teamUser)
                .from(teamUser)
                .join(teamUser.member, member)
                .where(member.memberId.eq(memberId))
                .where(teamUser.teamUserIsOnline.eq(true))
                .fetch();
    }

    /**
     * 설명 : 그룹 ID 찾기
     * @param chatRoomId 컨테이너 ID
     * @return Integer
     */
    public Integer findTeamId(Integer chatRoomId) {
        QTeam team = QTeam.team;
        QContainer container = QContainer.container;

        return query.select(team.teamId)
                .from(container)
                .join(container.team, team)
                .where(container.containerId.eq(chatRoomId))
                .fetchOne();
    }

    /**
     * 설명 : 컨테이너 ID 찾기
     * @param chatRoomId 그룹 ID
     * @return Integer
     */
    public Integer findByChatRoomId(Integer chatRoomId) {
        QTeam team = QTeam.team;
        QContainer container = QContainer.container;

        return query.select(team.teamId)
                .from(container)
                .join(container.team, team)
                .where(team.teamId.eq(chatRoomId))
                .fetchOne();
    }
}
