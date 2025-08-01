package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Auth;

@Repository
public interface AuthRepository extends JpaRepository<Auth, String> {
}