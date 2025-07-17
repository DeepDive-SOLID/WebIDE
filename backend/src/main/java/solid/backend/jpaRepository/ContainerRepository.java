package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.common.enums.ContainerVisibility;

import java.util.List;
import java.util.Optional;

/**
 * 컨테이너 리포지토리
 * 컨테이너 엔티티에 대한 데이터베이스 접근 계층
 */
@Repository
public interface ContainerRepository extends JpaRepository<Container, Long>, ContainerRepositoryCustom {
    
    /**
     * 특정 사용자가 소유한 컨테이너 목록 조회
     * @param owner 소유자
     * @return 소유 컨테이너 목록
     */
    List<Container> findByOwner(Member owner);
    
    /**
     * 공개 범위별 컨테이너 목록 조회
     * @param containerAuth 공개 여부 (true: PUBLIC, false: PRIVATE)
     * @return 해당 공개 범위의 컨테이너 목록
     */
    List<Container> findByContainerAuth(Boolean containerAuth);
    
    /**
     * 특정 사용자가 참여한 컨테이너 목록 조회 (소유 컨테이너 제외)
     * @param member 사용자
     * @return 참여한 컨테이너 목록
     */
    @Query("SELECT c FROM Container c JOIN c.team.teamUsers tu WHERE tu.member = :member AND c.owner != :member")
    List<Container> findSharedContainers(@Param("member") Member member);
    
    /**
     * 특정 사용자가 접근 가능한 모든 컨테이너 목록 조회 (소유 + 참여)
     * @param member 사용자
     * @return 접근 가능한 모든 컨테이너 목록
     */
    @Query("SELECT c FROM Container c WHERE c.owner = :member OR EXISTS (SELECT tu FROM TeamUser tu WHERE tu.team = c.team AND tu.member = :member)")
    List<Container> findAllAccessibleContainers(@Param("member") Member member);
}