package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.CodeFile;

public interface CodeFileRepository extends JpaRepository<CodeFile, String> {
}
