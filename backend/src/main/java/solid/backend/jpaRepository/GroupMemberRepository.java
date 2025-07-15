package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Group;
import solid.backend.entity.GroupMember;
import solid.backend.entity.Member;
import solid.backend.common.enums.Authority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 그룹 멤버 리포지토리
 * 그룹 멤버 엔티티에 대한 데이터베이스 접근 계층
 */
@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    /**
     * 특정 그룹의 특정 멤버 조회
     * @param group 그룹
     * @param member 멤버
     * @return 그룹 멤버 정보 (Optional)
     */
    Optional<GroupMember> findByGroupAndMember(Group group, Member member);
    
    /**
     * 특정 그룹의 모든 멤버 조회
     * @param group 그룹
     * @return 그룹 멤버 목록
     */
    List<GroupMember> findByGroup(Group group);
    
    /**
     * 특정 사용자가 속한 모든 그룹 조회
     * @param member 사용자
     * @return 소속 그룹 목록
     */
    List<GroupMember> findByMember(Member member);
    
    /**
     * 특정 그룹에서 특정 권한을 가진 멤버 조회
     * @param group 그룹
     * @param groupAuthId 권한 ID (ROOT/USER)
     * @return 해당 권한의 멤버 목록
     */
    List<GroupMember> findByGroupAndGroupAuthId(Group group, String groupAuthId);
    
    /**
     * 특정 날짜 이전에 마지막 활동한 멤버 조회 (6개월 미활동 자동 탈퇴용)
     * @param date 기준 날짜
     * @return 미활동 멤버 목록
     */
    @Query("SELECT gm FROM GroupMember gm WHERE gm.lastActivityDate < :date")
    List<GroupMember> findInactiveMembers(@Param("date") LocalDateTime date);
    
    /**
     * 특정 그룹에 특정 멤버가 속해있는지 확인
     * @param group 그룹
     * @param member 멤버
     * @return 소속 여부
     */
    boolean existsByGroupAndMember(Group group, Member member);
    
    /**
     * 특정 그룹에서 특정 멤버 제거
     * @param group 그룹
     * @param member 제거할 멤버
     */
    void deleteByGroupAndMember(Group group, Member member);
}