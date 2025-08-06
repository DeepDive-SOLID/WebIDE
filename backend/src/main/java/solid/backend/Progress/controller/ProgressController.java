package solid.backend.Progress.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solid.backend.Progress.dto.ProgressDto;
import solid.backend.Progress.dto.ProgressListDto;
import solid.backend.Progress.dto.QuestionProgressDto;
import solid.backend.Progress.service.ProgressService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    /**
     * 설명: 디렉터리별 진행률 조회
     * @param directoryId
     * @return List<ProgressListDto>
     */
    @GetMapping("/directory/{directoryId}")
    public ResponseEntity<List<ProgressListDto>> getProgressList(@PathVariable("directoryId") Integer directoryId) {
        List<ProgressListDto> progressList = progressService.getProgressList(directoryId);
        return ResponseEntity.ok(progressList);
    }

    /**
     * 설명: 진행률 업데이트
     * @param progressDto
     * @return ResponseEntity<String>
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateProgress(@RequestBody ProgressDto progressDto) {
        progressService.updateProgress(progressDto);
        return ResponseEntity.ok("Progress updated successfully");
    }

    /**
     * 설명: 컨테이너 내 모든 멤버의 진행률 조회
     * @param containerId
     * @return List<ProgressListDto>
     */
    @GetMapping("/container/{containerId}")
    public ResponseEntity<List<ProgressListDto>> getContainerProgress(@PathVariable("containerId") Integer containerId) {
        List<ProgressListDto> progressList = progressService.getAllMembersProgressInContainer(containerId);
        return ResponseEntity.ok(progressList);
    }

    /**
     * 설명: 컨테이너의 모든 문제별 진행률 조회
     * @param containerId
     * @param memberId
     * @return List<QuestionProgressDto>
     */
    @GetMapping("/container/{containerId}/member/{memberId}/questions")
    public ResponseEntity<?> getQuestionProgressByMember(
            @PathVariable("containerId") Integer containerId,
            @PathVariable("memberId") String memberId) {
        try {
            List<QuestionProgressDto> progressList = progressService.getQuestionProgressByMember(containerId, memberId);
            return ResponseEntity.ok(progressList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * 설명: 특정 디렉토리의 멤버 진행률 조회 (간단 버전)
     * @param directoryId
     * @param memberId
     * @return 진행률 정보
     */
    @GetMapping("/directory/{directoryId}/member/{memberId}")
    public ResponseEntity<?> getMemberProgressInDirectory(
            @PathVariable("directoryId") Integer directoryId,
            @PathVariable("memberId") String memberId) {
        try {
            Integer progressPercentage = progressService.getMemberProgressInDirectory(directoryId, memberId);
            // 프론트엔드가 기대하는 형식으로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("progressPercentage", progressPercentage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}