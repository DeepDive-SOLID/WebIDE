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
@RequestMapping("/api/directory")
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
    
    /**
     * 설명: 특정 디렉터리의 하위 디렉터리 조회
     * @param directoryId
     * @return List<DirectoryDto>
     */
    @ResponseBody
    @GetMapping("/children/{directoryId}")
    public ResponseEntity<List<DirectoryDto>> getChildDirectories(@PathVariable("directoryId") Integer directoryId) {
        try {
            List<DirectoryDto> children = directoryService.getChildDirectories(directoryId);
            return ResponseEntity.ok(children);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * 설명: 루트 디렉터리만 조회
     * @param containerId
     * @return List<DirectoryDto>
     */
    @ResponseBody
    @GetMapping("/roots/{containerId}")
    public ResponseEntity<List<DirectoryDto>> getRootDirectories(@PathVariable("containerId") Integer containerId) {
        try {
            List<DirectoryDto> roots = directoryService.getRootDirectories(containerId);
            return ResponseEntity.ok(roots);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
