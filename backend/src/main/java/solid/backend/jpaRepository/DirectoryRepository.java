package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // 경로와 이름으로 정확한 디렉터리 조회
    Optional<Directory> findByDirectoryRootAndDirectoryNameAndContainer(String directoryRoot, String directoryName, Container container);
    
    // 특정 경로의 하위 디렉터리들 조회
    @Query("SELECT d FROM Directory d WHERE d.container.containerId = :containerId " +
           "AND d.directoryRoot LIKE :parentPath% ORDER BY d.directoryRoot, d.directoryName")
    List<Directory> findChildrenByPath(@Param("containerId") Integer containerId, 
                                      @Param("parentPath") String parentPath);
    
    // 특정 깊이의 디렉터리만 조회
    @Query("SELECT d FROM Directory d WHERE d.container.containerId = :containerId " +
           "AND LENGTH(d.directoryRoot) - LENGTH(REPLACE(d.directoryRoot, '/', '')) = :depth " +
           "ORDER BY d.directoryRoot, d.directoryName")
    List<Directory> findByDepth(@Param("containerId") Integer containerId, 
                               @Param("depth") Integer depth);
    
    // 루트 디렉터리들만 조회
    @Query("SELECT d FROM Directory d WHERE d.container.containerId = :containerId " +
           "AND d.directoryRoot = '/' ORDER BY d.directoryName")
    List<Directory> findRootDirectories(@Param("containerId") Integer containerId);
}
