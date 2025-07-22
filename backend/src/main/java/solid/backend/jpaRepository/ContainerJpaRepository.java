package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.container.repository.ContainerRepositoryCustom;

import java.util.List;

/**
 * 컨테이너 JPA 레포지토리 인터페이스
 * 
 * Spring Data JPA와 QueryDSL을 함께 사용하여 컨테이너 엔티티에 대한 데이터 접근을 처리합니다.
 * 
 * 상속 구조:
 * 1. JpaRepository<Container, Long>: Spring Data JPA의 기본 CRUD 기능 제공
 *    - save(), findById(), findAll(), delete() 등의 기본 메서드 자동 구현
 * 
 * 2. ContainerRepositoryCustom: QueryDSL을 사용한 복잡한 쿼리를 위한 커스텀 인터페이스
 *    - 동적 쿼리, JOIN, 대량 업데이트 등 복잡한 로직 처리
 *    - ContainerJpaRepositoryImpl에서 구현
 * 
 * @see Container - 컨테이너 엔티티
 * @see ContainerRepositoryCustom - QueryDSL 커스텀 메서드 정의
 * @see ContainerJpaRepositoryImpl - QueryDSL 커스텀 메서드 구현
 */
@Repository
public interface ContainerJpaRepository extends JpaRepository<Container, Long>, ContainerRepositoryCustom {
    
    /**
     * 특정 사용자가 소유한 모든 컨테이너를 조회합니다.
     * 
     * Spring Data JPA의 메서드 이름 규칙에 따라 자동으로 SQL이 생성됩니다:
     * SELECT * FROM container WHERE owner_id = ? ORDER BY container_date DESC
     * 
     * @param owner 조회할 컨테이너의 소유자 (Member 엔티티)
     * @return 해당 사용자가 소유한 컨테이너 목록 (생성일 기준 내림차순 정렬)
     */
    List<Container> findByOwnerOrderByContainerDateDesc(Member owner);
    
    /**
     * 특정 이름과 소유자로 컨테이너의 존재 여부를 확인합니다.
     * 
     * 중복된 컨테이너 이름 방지를 위해 사용됩니다.
     * SQL: SELECT EXISTS(SELECT 1 FROM container WHERE container_name = ? AND owner_id = ?)
     * 
     * @param containerName 확인할 컨테이너 이름
     * @param owner 컨테이너 소유자
     * @return 해당 이름의 컨테이너가 존재하면 true, 없으면 false
     */
    boolean existsByContainerNameAndOwner(String containerName, Member owner);
    
    /**
     * 공개 여부에 따라 컨테이너 목록을 조회합니다.
     * 
     * PUBLIC/PRIVATE 컨테이너를 구분하여 조회할 때 사용됩니다.
     * SQL: SELECT * FROM container WHERE is_public = ? ORDER BY container_date DESC
     * 
     * @param isPublic true: PUBLIC 컨테이너만 조회, false: PRIVATE 컨테이너만 조회
     * @return 해당 공개 설정의 컨테이너 목록 (생성일 기준 내림차순)
     */
    List<Container> findByIsPublicOrderByContainerDateDesc(Boolean isPublic);
    
    /*
     * QueryDSL로 구현된 복잡한 쿼리 메서드들:
     * 
     * ContainerRepositoryCustom 인터페이스에 정의되고,
     * ContainerJpaRepositoryImpl 클래스에서 QueryDSL로 구현됩니다:
     * 
     * 1. findByIdWithTeam(Long containerId)
     *    - Team 엔티티를 Fetch Join으로 함께 조회하여 N+1 문제 방지
     * 
     * 2. findSharedContainers(Member member)
     *    - 사용자가 팀 멤버로 참여한 컨테이너 목록 조회
     * 
     * 3. findAllAccessibleContainers(Member member)
     *    - 사용자가 접근 가능한 모든 컨테이너 (소유, 참여, 공개)
     * 
     * 4. isTeamMember(Long containerId, String memberId)
     *    - 특정 사용자가 컨테이너의 팀 멤버인지 확인
     * 
     * 5. updateContainerVisibility(List<Long> containerIds, boolean isPublic)
     *    - 대량 컨테이너의 공개 여부 일괄 업데이트
     * 
     * 6. searchContainers(String name, Boolean isPublic, String ownerId, Member member)
     *    - 동적 조건에 따른 컨테이너 검색
     */
}