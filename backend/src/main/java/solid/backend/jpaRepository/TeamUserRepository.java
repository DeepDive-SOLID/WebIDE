package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solid.backend.entity.TeamUser;

import java.util.Optional;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, Integer> {
    
    @Query("SELECT tu FROM TeamUser tu " +
           "JOIN FETCH tu.member " +
           "JOIN FETCH tu.teamAuth " +
           "JOIN FETCH tu.team " +
           "WHERE tu.teamUserId = :id")
    Optional<TeamUser> findByIdWithFetch(@Param("id") Integer id);
}
