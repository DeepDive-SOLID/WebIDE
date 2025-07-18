package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Container;
import solid.backend.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    void deleteByContainer(Container containerId);
}
