package solid.backend.CodeFile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solid.backend.CodeFile.dto.CodeFileDelDto;
import solid.backend.CodeFile.dto.CodeFileListDto;
import solid.backend.CodeFile.dto.CodeFileSaveDto;
import solid.backend.CodeFile.dto.CodeFileUpdDto;
import solid.backend.CodeFile.service.CodeFileService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/CodeFile")
public class CodeFileController {
    private final CodeFileService codeFileService;

    /**
     * 설명: 코드 파일 전체 조회
     * @return List<CodeFileListDto>
     */
    @ResponseBody
    @GetMapping("/list")
    public List<CodeFileListDto> getCodeFileList() {
        return codeFileService.getCodeFileList();
    }

    /**
     * 설명: 코드 파일 내용 조회
     * @param codeFileId
     * @return String
     */
    @ResponseBody
    @PostMapping("/content")
    public String codeContent(@RequestBody Integer codeFileId) {
        return codeFileService.codeContent(codeFileId);
    }

    /**
     * 설명: 코드 파일 생성
     * @param codeFileSaveDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<String> createCodeFile(@RequestBody CodeFileSaveDto codeFileSaveDto) {
        try {
            codeFileService.createCodeFile(codeFileSaveDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    /**
     * 설명: 코드 파일 내용 변경
     * @param codeFileUpdDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PutMapping("/update")
    public ResponseEntity<String> updateCodeFile(@RequestBody CodeFileUpdDto codeFileUpdDto) {
        try {
            codeFileService.updateCodeFile(codeFileUpdDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    /**
     * 설명: 코드 파일 삭제
     * @param codeFileDelDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCodeFile(@RequestBody CodeFileDelDto codeFileDelDto) {
        try {
            codeFileService.deleteCodeFile(codeFileDelDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }
}
