package solid.backend.Progress.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.Directory.service.DirectoryService;
import solid.backend.Progress.dto.ProgressDto;
import solid.backend.entity.Directory;
import solid.backend.entity.Progress;
import solid.backend.entity.TeamUser;
import solid.backend.jpaRepository.DirectoryRepository;
import solid.backend.jpaRepository.ProgressRepository;
import solid.backend.jpaRepository.TeamRepository;
import solid.backend.jpaRepository.TeamUserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService{
    private final ProgressRepository progressRepository;
    private final DirectoryRepository directoryRepository;
    private final TeamUserRepository teamUserRepository;


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
}
