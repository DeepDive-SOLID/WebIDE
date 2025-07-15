package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Group;

/**
 * 그룹 리포지토리
 * 그룹 엔티티에 대한 데이터베이스 접근 계층
 * 기본 CRUD 기능만 사용
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}