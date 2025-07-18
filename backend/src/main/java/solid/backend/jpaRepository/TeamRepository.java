package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}
