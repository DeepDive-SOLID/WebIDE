package solid.backend.Progress.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import solid.backend.Progress.dto.ProgressDto;
import solid.backend.Progress.dto.ProgressListDto;
import solid.backend.Progress.service.ProgressService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/progress")
public class ProgressController {
    private final ProgressService progressService;

    /**
     * 설명: 진행률 조회
     * @param directoryId
     * @return List<ProgressListDto>
     */
    @ResponseBody
    @PostMapping("/list")
    public List<ProgressListDto> getProgressList(@RequestBody Integer directoryId) {
        return progressService.getProgressList(directoryId);
    }

    /**
     * 설명: 문제 진행률 계산
     * @param progressDto
     */
    @ResponseBody
    @PostMapping("/update")
    public ResponseEntity<String> updateProgress(@RequestBody ProgressDto progressDto) {
        try {
            progressService.updateProgress(progressDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }
}
