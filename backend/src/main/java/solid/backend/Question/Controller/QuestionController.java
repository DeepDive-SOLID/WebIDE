package solid.backend.Question.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solid.backend.Question.dto.QuestionCreateDto;
import solid.backend.Question.dto.QuestionListDto;
import solid.backend.Question.dto.QuestionUpdDto;
import solid.backend.Question.service.QuestionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;

    /**
     * 설명:
     * @return
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
     * 설명: 문제 및 테스트 케이스 등록
     * @param questionCreateDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<String> createQuestion(@RequestBody QuestionCreateDto questionCreateDto) {
        try {
            questionService.createQuestion(questionCreateDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    /**
     * 설명: 문제 및 테스트 케이스 수정
     * @param questionUpdDto
     * @return
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
