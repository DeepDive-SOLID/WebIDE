package solid.backend.Progress.service;

import solid.backend.Progress.dto.ProgressDto;
import solid.backend.Progress.dto.ProgressListDto;
import solid.backend.Progress.dto.QuestionProgressDto;

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
    
    /**
     * 설명: 컨테이너 내 모든 멤버의 진행률 조회
     * @param containerId
     * @return List<ProgressListDto>
     */
    List<ProgressListDto> getAllMembersProgressInContainer(Integer containerId);
    
    /**
     * 설명: 컨테이너의 모든 문제별 진행률 조회
     * @param containerId
     * @param memberId
     * @return List<QuestionProgressDto>
     */
    List<QuestionProgressDto> getQuestionProgressByMember(Integer containerId, String memberId);
    
    /**
     * 설명: 특정 디렉토리의 멤버 진행률 조회 (간단 버전)
     * @param directoryId
     * @param memberId
     * @return 진행률 퍼센트
     */
    Integer getMemberProgressInDirectory(Integer directoryId, String memberId);
}