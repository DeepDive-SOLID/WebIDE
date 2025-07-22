package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Code;

import java.util.List;

/**
 * 코드 JPA 레포지토리 인터페이스
 * 
 * 디렉토리에 저장된 코드 파일들을 관리하는 레포지토리입니다.
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, Integer> {
    
    /**
     * 특정 디렉토리의 모든 코드 조회
     * 
     * @param directoryId 디렉토리 ID
     * @return 해당 디렉토리의 코드 목록
     */
    List<Code> findByDirectory_DirectoryId(Integer directoryId);
    
    /**
     * 특정 디렉토리에서 코드명으로 코드 조회
     * 
     * @param directoryId 디렉토리 ID
     * @param codeName 코드명
     * @return 코드 엔티티
     */
    Code findByDirectory_DirectoryIdAndCodeName(Integer directoryId, String codeName);
}