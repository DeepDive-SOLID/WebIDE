package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Directory;
import solid.backend.entity.Container;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Integer> {
    List<Directory> findAllByContainer_ContainerId(Integer containerId);
    Optional<Directory> findByDirectoryNameAndContainer_ContainerIdAndTeam_TeamId(String directoryName, Integer containerId, Integer teamId);
    List<Directory> findByContainer(Container container);
    
    // 부모 디렉터리의 하위 디렉터리 조회
    List<Directory> findByParentDirectory(Directory parentDirectory);
    
    // 최상위 디렉터리(부모가 없는) 조회
    List<Directory> findByContainerAndParentDirectoryIsNull(Container container);
}
