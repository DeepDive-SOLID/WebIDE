package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.CodeFile;
import java.util.List;

public interface CodeFileRepository extends JpaRepository<CodeFile, String> {
    List<CodeFile> findByDirectory_DirectoryIdOrderByCodeFileIdDesc(Integer directoryId);
}
