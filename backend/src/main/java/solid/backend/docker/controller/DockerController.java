package solid.backend.docker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import solid.backend.common.ApiResponse;
import solid.backend.docker.dto.CodeExecutionRequestDto;
import solid.backend.docker.dto.CodeExecutionResponseDto;
import solid.backend.docker.dto.ExecutionStatusDto;
import solid.backend.docker.service.DockerService;

import java.util.List;

/**
 * 도커 실행 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/docker")
@RequiredArgsConstructor
public class DockerController {
    
    /** 도커 서비스 */
    private final DockerService dockerService;
    
    /**
     * 코드 실행
     * @param authentication 인증 정보
     * @param request 코드 실행 요청
     * @return 실행 결과
     */
    @PostMapping("/execute")
    public ResponseEntity<ApiResponse<CodeExecutionResponseDto>> executeCode(
            Authentication authentication,
            @RequestBody @Valid CodeExecutionRequestDto request) {
        
        String memberId = authentication.getName();
        CodeExecutionResponseDto response = dockerService.executeCode(memberId, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "코드 실행이 완료되었습니다"));
    }
    
    /**
     * 실행 상태 조회
     * @param executionId 실행 ID
     * @return 실행 상태
     */
    @GetMapping("/executions/{executionId}/status")
    public ResponseEntity<ApiResponse<ExecutionStatusDto>> getExecutionStatus(
            @PathVariable Long executionId) {
        
        ExecutionStatusDto status = dockerService.getExecutionStatus(executionId);
        return ResponseEntity.ok(ApiResponse.success(status, "실행 상태 조회 성공"));
    }
    
    /**
     * 실행 중지
     * @param executionId 실행 ID
     * @return 성공 메시지
     */
    @PostMapping("/executions/{executionId}/stop")
    public ResponseEntity<ApiResponse<Void>> stopExecution(
            @PathVariable Long executionId) {
        
        dockerService.stopExecution(executionId);
        return ResponseEntity.ok(ApiResponse.success(null, "실행이 중지되었습니다"));
    }
    
    /**
     * 컨테이너의 실행 기록 조회
     * @param authentication 인증 정보
     * @param containerId 컨테이너 ID
     * @return 실행 기록 목록
     */
    @GetMapping("/containers/{containerId}/executions")
    public ResponseEntity<ApiResponse<List<CodeExecutionResponseDto>>> getExecutionHistory(
            Authentication authentication,
            @PathVariable Long containerId) {
        
        String memberId = authentication.getName();
        List<CodeExecutionResponseDto> history = dockerService.getExecutionHistory(containerId, memberId);
        
        return ResponseEntity.ok(ApiResponse.success(history, "실행 기록 조회 성공"));
    }
    
    /**
     * 실행 기록 삭제
     * @param authentication 인증 정보
     * @param executionId 실행 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/executions/{executionId}")
    public ResponseEntity<ApiResponse<Void>> deleteExecution(
            Authentication authentication,
            @PathVariable Long executionId) {
        
        String memberId = authentication.getName();
        dockerService.deleteExecution(executionId, memberId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "실행 기록이 삭제되었습니다"));
    }
    
    /**
     * 지원 언어 목록 조회
     * @return 지원 언어 목록
     */
    @GetMapping("/languages")
    public ResponseEntity<ApiResponse<List<String>>> getSupportedLanguages() {
        List<String> languages = dockerService.getSupportedLanguages();
        return ResponseEntity.ok(ApiResponse.success(languages, "지원 언어 목록 조회 성공"));
    }
}