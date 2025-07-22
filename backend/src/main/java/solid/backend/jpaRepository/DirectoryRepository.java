package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Directory;

import java.util.List;
import java.util.Optional;

/**
 * 디렉토리 레포지토리
 */
@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Integer> {
    
    /**
     * 컨테이너 ID로 디렉토리 목록 조회
     * @param containerId 컨테이너 ID
     * @return 디렉토리 목록
     */
    List<Directory> findByContainer_ContainerId(Long containerId);
    
    /**
     * 컨테이너와 디렉토리명으로 조회
     * @param containerId 컨테이너 ID
     * @param directoryName 디렉토리명
     * @return 디렉토리
     */
    Optional<Directory> findByContainer_ContainerIdAndDirectoryName(Long containerId, String directoryName);
    
    /**
     * 컨테이너와 디렉토리 경로로 조회
     * @param containerId 컨테이너 ID
     * @param directoryRoot 디렉토리 경로
     * @return 디렉토리 목록
     */
    List<Directory> findByContainer_ContainerIdAndDirectoryRoot(Long containerId, String directoryRoot);
}