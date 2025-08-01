package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
}
