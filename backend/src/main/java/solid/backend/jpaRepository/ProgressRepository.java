package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Directory;
import solid.backend.entity.Progress;
import solid.backend.entity.TeamUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Integer> {
    Optional<Progress> findByDirectoryAndTeamUser(Directory directory, TeamUser teamUser);
    
    // 언어별 진행률 조회
    Optional<Progress> findByDirectoryAndTeamUserAndLanguage(Directory directory, TeamUser teamUser, String language);
    
    // 특정 디렉터리와 팀 유저의 모든 언어별 진행률 조회
    List<Progress> findAllByDirectoryAndTeamUser(Directory directory, TeamUser teamUser);
    
    /**
     * 여러 디렉토리의 진행률을 한 번에 조회
     */
    @Query("SELECT p FROM Progress p " +
           "JOIN FETCH p.directory d " +
           "JOIN FETCH p.teamUser tu " +
           "WHERE d.directoryId IN :directoryIds " +
           "AND tu.teamUserId = :teamUserId")
    List<Progress> findAllByDirectoryIdsAndTeamUserId(
        @Param("directoryIds") List<Integer> directoryIds, 
        @Param("teamUserId") Integer teamUserId
    );
    
    /**
     * 컨테이너의 모든 문제 디렉토리 진행률을 한 번에 조회
     */
    @Query("SELECT p FROM Progress p " +
           "JOIN FETCH p.directory d " +
           "JOIN FETCH p.teamUser tu " +
           "JOIN FETCH tu.member m " +
           "WHERE d.container.containerId = :containerId " +
           "AND EXISTS (SELECT 1 FROM Question q WHERE q.directory = d)")
    List<Progress> findAllProgressForProblemDirectoriesInContainer(
        @Param("containerId") Integer containerId
    );
    
    /**
     * 디렉토리별 진행률 삭제
     */
    @Modifying
    @Query("DELETE FROM Progress p WHERE p.directory = :directory")
    int deleteByDirectory(@Param("directory") Directory directory);
}
