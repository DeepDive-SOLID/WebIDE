package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import solid.backend.entity.Directory;
import solid.backend.entity.Container;

import java.util.List;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Integer> {
    List<Directory> findAllByContainer_ContainerId(Integer containerId);
    Optional<Directory> findByDirectoryNameAndContainer_ContainerIdAndTeam_TeamId(String directoryName, Integer containerId, Integer teamId);
    List<Directory> findByContainer(Container container);
}
