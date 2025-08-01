package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
}
