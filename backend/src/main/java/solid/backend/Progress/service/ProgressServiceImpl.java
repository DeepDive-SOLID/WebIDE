package solid.backend.Progress.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.Directory.service.DirectoryService;
import solid.backend.Progress.dto.ProgressDto;
import solid.backend.Progress.dto.ProgressListDto;
import solid.backend.Progress.repository.ProgressQueryRepository;
import solid.backend.entity.Directory;
import solid.backend.entity.Progress;
import solid.backend.entity.TeamUser;
import solid.backend.jpaRepository.DirectoryRepository;
import solid.backend.jpaRepository.ProgressRepository;
import solid.backend.jpaRepository.TeamRepository;
import solid.backend.jpaRepository.TeamUserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService{
    private final ProgressRepository progressRepository;
    private final DirectoryRepository directoryRepository;
    private final TeamUserRepository teamUserRepository;
    private final ProgressQueryRepository progressQueryRepository;

    /**
     * 설명: 디렉터리 속 진행률 조회
     * @param directoryId
     * @return List<ProgressListDto>
     */
    @Override
    @Transactional
    public List<ProgressListDto> getProgressList(Integer directoryId) {
        return progressQueryRepository.getProgressListByDirectoryId(directoryId);
    }

    /**
     * 설명: 문제 진행률 계산
     * @param progressDto
     */
    @Override
    @Transactional
    public void updateProgress(ProgressDto progressDto) {
        // 디렉토리 ID + 팀 유저 ID 조합으로 기존 진행률 찾기
        Directory directory = directoryRepository.findById(progressDto.getDirectoryId())
                .orElseThrow(() -> new RuntimeException("Directory not found"));

        TeamUser teamUser = teamUserRepository.findById(progressDto.getTeamUserId())
                .orElseThrow(() -> new RuntimeException("TeamUser not found"));

        Optional<Progress> progressOptional = progressRepository.findByDirectoryAndTeamUser(directory, teamUser);


        if (progressOptional.isPresent()) {
            // 기존 진행률이 있으면 업데이트
            Progress progress = progressOptional.get();
            progress.setProgressComplete(progressDto.getProgressComplete());
            progressRepository.save(progress);
        } else {
            // 없으면 새로 생성
            Progress progress = new Progress();
            progress.setDirectory(directoryRepository.findById(progressDto.getDirectoryId()).orElseThrow());
            progress.setTeamUser(teamUserRepository.findById(progressDto.getTeamUserId()).orElseThrow());
            progress.setProgressComplete(progressDto.getProgressComplete());
            progressRepository.save(progress);
        }
    }

    /**
     * 설명: 컨테이너 내 모든 멤버의 진행률 조회
     * @param containerId
     * @return List<ProgressListDto>
     */
    @Override
    @Transactional
    public List<ProgressListDto> getAllMembersProgressInContainer(Integer containerId) {
        // 컨테이너에 속한 모든 디렉터리 조회
        List<Directory> directories = directoryRepository.findAllByContainer_ContainerId(containerId);
        
        // 해당 컨테이너의 팀 ID 가져오기 (디렉터리가 있다면 첫 번째 디렉터리의 팀 ID 사용)
        if (directories.isEmpty()) {
            return new ArrayList<>();
        }
        
        Integer teamId = directories.get(0).getTeam().getTeamId();
        
        // 팀에 속한 모든 팀 유저 조회
        List<TeamUser> teamUsers = teamUserRepository.findByTeam_TeamId(teamId);
        
        List<ProgressListDto> result = new ArrayList<>();
        
        for (TeamUser teamUser : teamUsers) {
            int totalProgress = 0;
            int directoryCount = directories.size();
            
            // 각 디렉터리에 대한 진행률 합산
            for (Directory directory : directories) {
                Optional<Progress> progress = progressRepository.findByDirectoryAndTeamUser(directory, teamUser);
                if (progress.isPresent()) {
                    totalProgress += progress.get().getProgressComplete();
                }
            }
            
            // 평균 진행률 계산 (디렉터리당 평균)
            int averageProgress = directoryCount > 0 ? totalProgress / directoryCount : 0;
            
            ProgressListDto dto = new ProgressListDto();
            dto.setMemberId(teamUser.getMember().getMemberId());
            dto.setMemberName(teamUser.getMember().getMemberName());
            dto.setDirectoryCount(directoryCount);
            dto.setAverageProgress(averageProgress);
            
            result.add(dto);
        }
        
        return result;
    }

}
