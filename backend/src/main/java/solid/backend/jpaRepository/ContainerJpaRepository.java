package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Container;
import solid.backend.entity.Member;

import java.util.List;

/**
 * 컨테이너 JPA 레포지토리 인터페이스
 * Spring Data JPA를 사용하여 컨테이너 엔티티에 대한 기본 데이터 접근을 처리합니다.
 * 복잡한 쿼리는 ContainerQueryRepository에서 별도로 처리합니다.
 * 
 * @see Container - 컨테이너 엔티티
 */
@Repository
public interface ContainerJpaRepository extends JpaRepository<Container, Integer> {
    
    
    /**
     * 공개 여부에 따라 컨테이너 목록을 조회합니다.
     * PUBLIC/PRIVATE 컨테이너를 구분하여 조회할 때 사용됩니다.
     * SQL: SELECT * FROM container WHERE container_auth = ? ORDER BY container_date DESC
     * 
     * @param containerAuth true: PUBLIC 컨테이너만 조회, false: PRIVATE 컨테이너만 조회
     * @return 해당 공개 설정의 컨테이너 목록 (생성일 기준 내림차순)
     */
    List<Container> findByContainerAuthOrderByContainerDateDesc(Boolean containerAuth);
    
    /**
     * 특정 사용자가 컨테이너가 속한 팀의 멤버인지 확인합니다.
     * 컨테이너의 팀에 해당 사용자가 TeamUser로 등록되어 있는지 검사합니다.
     * 
     * @param containerId 확인할 컨테이너 ID
     * @param memberId 확인할 사용자 ID
     * @return 팀 멤버인 경우 true, 아닌 경우 false
     */
    @Query("SELECT COUNT(tu) > 0 FROM Container c " +
           "JOIN c.team t " +
           "JOIN TeamUser tu ON tu.team = t " +
           "WHERE c.containerId = :containerId " +
           "AND tu.member.memberId = :memberId")
    boolean isTeamMember(@Param("containerId") Integer containerId, @Param("memberId") String memberId);
    
}