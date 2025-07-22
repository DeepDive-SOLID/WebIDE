package solid.backend.Docker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import solid.backend.Docker.dto.CustomInputDto;
import solid.backend.Docker.dto.CustomInputResultDto;
import solid.backend.Docker.dto.DockerRunDto;
import solid.backend.Docker.dto.ExecutionResultDto;
import solid.backend.Docker.service.DockerServiceImpl;

@Controller
@RequiredArgsConstructor
@RequestMapping("/docker")
public class DockerController {
    private final DockerServiceImpl dockerService;

    /**
     * 설명: 전체 실행
     * @param dockerRunDto
     * @return ResponseEntity
     */
    @ResponseBody
    @PostMapping("/run")
    public ResponseEntity<ExecutionResultDto> runCodeFile(@RequestBody DockerRunDto dockerRunDto) {
        return ResponseEntity.ok(dockerService.runCodeFile(dockerRunDto.getCodeFileId(), dockerRunDto.getQuestionId()));
    }

    /**
     * 설명: 테스트케이스 실행
     * */
    @ResponseBody
    @PostMapping("/test")
    public ResponseEntity<ExecutionResultDto> runTestCodeFile(@RequestBody DockerRunDto dockerRunDto) {
        return ResponseEntity.ok(dockerService.runTestCodeFile(dockerRunDto.getCodeFileId(), dockerRunDto.getQuestionId()));
    }

    @ResponseBody
    @PostMapping("/custom")
    public ResponseEntity<CustomInputResultDto> runExctCodeFile(@RequestBody CustomInputDto customInputDto) {
        return ResponseEntity.ok(dockerService.runExctCodeFile(customInputDto));
    }
}
