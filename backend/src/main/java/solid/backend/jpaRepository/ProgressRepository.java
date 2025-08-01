package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.Directory;
import solid.backend.entity.Progress;
import solid.backend.entity.TeamUser;

import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {
    Optional<Progress> findByDirectoryAndTeamUser(Directory directory, TeamUser teamUser);
}
