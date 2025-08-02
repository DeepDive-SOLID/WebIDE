package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import solid.backend.entity.CodeFile;
import solid.backend.entity.Directory;
import java.util.List;

public interface CodeFileRepository extends JpaRepository<CodeFile, String> {
    List<CodeFile> findByDirectory_DirectoryIdOrderByCodeFileIdDesc(Integer directoryId);
    
    @Modifying
    int deleteByDirectory(Directory directory);
}
