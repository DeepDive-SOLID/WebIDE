package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Integer> {
}
