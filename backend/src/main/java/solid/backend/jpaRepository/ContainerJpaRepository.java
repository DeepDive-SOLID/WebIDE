package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Container;
import solid.backend.entity.Member;

import java.util.List;

/**
 * 컨테이너 JPA 레포지토리 인터페이스
 * 
 * Spring Data JPA를 사용하여 컨테이너 엔티티에 대한 기본 데이터 접근을 처리합니다.
 * 
 * 복잡한 쿼리는 ContainerQueryRepository에서 별도로 처리합니다.
 * 
 * @see Container - 컨테이너 엔티티
 * @see ContainerQueryRepository - QueryDSL을 사용한 복잡한 쿼리 처리
 */
@Repository
public interface ContainerJpaRepository extends JpaRepository<Container, Long> {
    
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
    
}