package solid.backend.Progress.service;

import solid.backend.Progress.dto.ProgressDto;
import solid.backend.Progress.dto.ProgressListDto;

import java.util.List;

public interface ProgressService {
    /**
     * 설명: 진행률 조회
     * @param directoryId
     */
    List<ProgressListDto> getProgressList(Integer directoryId);

    /**
     * 설명: 문제 진행률 계산
     * @param progressDto
     */
    void updateProgress(ProgressDto progressDto);
}
