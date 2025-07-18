package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.entity.MemberChatRoom;

import java.util.List;

@Repository
public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    List<MemberChatRoom> findByMember(Member member);

    void deleteAllByMember(Member member);

    void deleteByMemberAndChatRoom(Member member, Container container);

    boolean existsMemberChatRoomByMemberAndChatRoom(Member member, Container container);

    long countMemberChatRoomByChatRoom(Container container);
}
