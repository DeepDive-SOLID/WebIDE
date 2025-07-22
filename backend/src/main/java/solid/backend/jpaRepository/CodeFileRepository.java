package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.CodeFile;

import java.util.List;
import java.util.Optional;

/**
 * 코드 파일 레포지토리
 */
@Repository
public interface CodeFileRepository extends JpaRepository<CodeFile, Integer> {
    
    /**
     * 디렉토리 ID로 파일 목록 조회
     * @param directoryId 디렉토리 ID
     * @return 파일 목록
     */
    List<CodeFile> findByDirectory_DirectoryId(Integer directoryId);
    
    /**
     * 디렉토리와 파일명으로 조회
     * @param directoryId 디렉토리 ID
     * @param codeFileName 파일명
     * @return 코드 파일
     */
    Optional<CodeFile> findByDirectory_DirectoryIdAndCodeFileName(Integer directoryId, String codeFileName);
    
    /**
     * 파일 경로로 조회
     * @param codeFilePath 파일 경로
     * @return 코드 파일
     */
    Optional<CodeFile> findByCodeFilePath(String codeFilePath);
}