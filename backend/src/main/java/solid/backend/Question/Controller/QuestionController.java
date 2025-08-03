package solid.backend.Question.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solid.backend.Question.dto.QuestionCreateDto;
import solid.backend.Question.dto.QuestionListDto;
import solid.backend.Question.dto.QuestionUpdDto;
import solid.backend.Question.dto.TestCaseListDto;
import solid.backend.Question.service.QuestionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/question")
public class QuestionController {
    private final QuestionService questionService;

    /**
     * 설명: 문제 리스트 조회
     * @return List<QuestionListDto>
     */
    @ResponseBody
    @GetMapping("/list")
    public List<QuestionListDto> getQuestionList() {
        return questionService.getQuestionList();
    }

    /**
     * 설명: 컨테이너 내부 등록된 문제 조회
     * @param containerId
     * @return List<QuestionListDto>
     */
    @ResponseBody
    @PostMapping("/list_id")
    public List<QuestionListDto> getQuestionListById(@RequestBody Integer containerId) {
        return questionService.containerInQuestionList(containerId);
    }

    /**
     * 설명: 등록된 문제의 공개된 테스트케이스 리스트 반환
     * @param questionId
     * @return List<TestCaseListDto>
     */
    @ResponseBody
    @PostMapping("/trueList")
    public List<TestCaseListDto> trueQuestionList(@RequestBody Integer questionId) {
        return questionService.trueQuestionList(questionId);
    }

    /**
     * 설명: 문제 및 테스트 케이스 등록
     * @param questionCreateDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionCreateDto questionCreateDto) {
        try {
            questionService.createQuestion(questionCreateDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "문제 생성에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 설명: 문제 및 테스트 케이스 수정
     * @param questionUpdDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PutMapping("/update")
    public ResponseEntity<String> updateQuestion(@RequestBody QuestionUpdDto questionUpdDto) {
        try {
            questionService.updateQuestion(questionUpdDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    /**
     * 설명: 문제 및 테스트 케이스 삭제
     * @param questionId
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteQuestion(@RequestBody Integer questionId) {
        try {
            questionService.deleteQuestion(questionId);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }
}
