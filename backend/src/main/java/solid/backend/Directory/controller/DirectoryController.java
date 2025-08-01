package solid.backend.Directory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solid.backend.Directory.dto.DirectoryDelDto;
import solid.backend.Directory.dto.DirectoryDto;
import solid.backend.Directory.dto.DirectoryListDto;
import solid.backend.Directory.dto.DirectoryUpdDto;
import solid.backend.Directory.service.DirectoryService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/directory")
public class DirectoryController {

    private final DirectoryService directoryService;

    /**
     * 설명: 컨테이너 전체 디렉터리 리스트 조회
     * @param directoryListDto
     * @return List<DirectoryDto>
     */
    @ResponseBody
    @PostMapping("/list")
    public List<DirectoryDto> getDirectory(@RequestBody DirectoryListDto directoryListDto) {
        return directoryService.getDirectoryList(directoryListDto);
    }

    /**
     * 설명: 디렉터리 생성
     * @param directoryDto
     * @return DirectoryDto
     */
    @ResponseBody
    @PostMapping("/create")
    public DirectoryDto createDirectory(@RequestBody DirectoryDto directoryDto) {
        return directoryService.createDirectory(directoryDto);
    }

    /**
     * 설명: 디렉터리 이름 변경
     * @param directoryUpdDto
     * @return ResponseEntity<String>
     */

    @ResponseBody
    @PutMapping("/rename")
    public ResponseEntity<String> renameDirectory(@RequestBody DirectoryUpdDto directoryUpdDto) {
        try {
            directoryService.updateDirectory(directoryUpdDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    /**
     * 설명: 디렉터리 삭제
     * @param directoryDelDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDirectory(@RequestBody DirectoryDelDto directoryDelDto) {
        try {
            directoryService.deleteDirectory(directoryDelDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }
}
