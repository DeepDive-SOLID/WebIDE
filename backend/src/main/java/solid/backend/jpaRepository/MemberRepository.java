package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
}
