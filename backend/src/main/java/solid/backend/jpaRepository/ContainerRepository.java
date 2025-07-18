package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.Container;

public interface ContainerRepository extends JpaRepository<Container, Integer> {
}
