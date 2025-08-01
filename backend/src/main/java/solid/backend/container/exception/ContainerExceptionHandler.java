package solid.backend.container.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingRequestHeaderException;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import solid.backend.common.ApiResponse;

/**
 * 컨테이너 기능 예외 처리기
 * 컨테이너 관련 API에서 발생하는 예외를 일관된 형식으로 처리합니다.
 * Jakarta Validation 검증 실패, 비즈니스 로직 예외 등을 처리합니다.
 */
@Slf4j
@RestControllerAdvice(basePackages = "solid.backend.container")
public class ContainerExceptionHandler {
    
    /**
     * 공통 에러 응답 생성 헬퍼 메서드
     * ApiResponse를 사용하여 일관된 형식의 에러 응답을 생성합니다.
     * 
     * @param errorCode 에러 코드
     * @param message 사용자에게 표시할 메시지
     * @param path 요청 경로
     * @return ApiResponse 형식의 에러 응답
     */
    private ApiResponse<Void> createErrorResponse(String errorCode, String message, String path) {
        return ApiResponse.error(message, errorCode, path);
    }
    
    private ApiResponse<Void> createErrorResponse(String errorCode, String message) {
        return ApiResponse.error(message, errorCode);
    }
    
    /**
     * 컨테이너를 찾을 수 없을 때 발생하는 예외 처리
     * 
     * @param e ContainerNotFoundException 예외
     * @param request HTTP 요청 정보
     * @return 404 Not Found 응답
     */
    @ExceptionHandler(ContainerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleContainerNotFoundException(
            ContainerNotFoundException e, HttpServletRequest request) {
        log.error("Container not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("CONTAINER_NOT_FOUND", e.getMessage(), request.getRequestURI()));
    }
    
    /**
     * 컨테이너 접근 권한이 없을 때 발생하는 예외 처리
     * 
     * @param e UnauthorizedContainerAccessException 예외
     * @param request HTTP 요청 정보
     * @return 403 Forbidden 응답
     */
    @ExceptionHandler(UnauthorizedContainerAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAccess(
            UnauthorizedContainerAccessException e, HttpServletRequest request) {
        log.error("Unauthorized access: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse("UNAUTHORIZED_ACCESS", e.getMessage(), request.getRequestURI()));
    }
    
    /**
     * 중복 멤버 초대 시 발생하는 예외 처리
     * 
     * @param e DuplicateMemberException 예외
     * @param request HTTP 요청 정보
     * @return 409 Conflict 응답
     */
    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateMember(
            DuplicateMemberException e, HttpServletRequest request) {
        log.error("Duplicate member: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse("DUPLICATE_MEMBER", e.getMessage(), request.getRequestURI()));
    }
    
    /**
     * Jakarta Validation 에러 처리
     * @Valid 어노테이션에 의한 검증 실패 시 발생하는 예외를 처리합니다.
     * 
     * @param e MethodArgumentNotValidException 예외
     * @param request HTTP 요청 정보
     * @return 400 Bad Request 응답과 필드별 에러 메시지
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation error: {}", e.getMessage());
        
        // 필드별 에러 메시지 수집
        Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("입력값 검증에 실패했습니다")
                .errorCode("VALIDATION_FAILED")
                .path(request.getRequestURI())
                .data(fieldErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * IllegalArgumentException 처리
     * 비즈니스 로직 검증 실패 시 발생하는 예외를 처리합니다.
     * 
     * @param e IllegalArgumentException 예외
     * @param request HTTP 요청 정보
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        log.error("Business logic error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("INVALID_ARGUMENT", e.getMessage(), request.getRequestURI()));
    }
    
    /**
     * 필수 헤더 누락 시 발생하는 예외 처리
     * 
     * @param e MissingRequestHeaderException 예외
     * @param request HTTP 요청 정보
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingHeader(
            MissingRequestHeaderException e, HttpServletRequest request) {
        log.error("Missing header: {} - Request: {} {}", 
            e.getHeaderName(), request.getMethod(), request.getRequestURI());
        
        String message = String.format("필수 헤더 '%s'가 누락되었습니다", e.getHeaderName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("MISSING_HEADER", message, request.getRequestURI()));
    }
    
    /**
     * 멤버를 찾을 수 없을 때 발생하는 예외 처리
     * 
     * @param e MemberNotFoundException 예외
     * @param request HTTP 요청 정보
     * @return 404 Not Found 응답
     */
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMemberNotFoundException(
            MemberNotFoundException e, HttpServletRequest request) {
        log.error("Member not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("MEMBER_NOT_FOUND", e.getMessage(), request.getRequestURI()));
    }
    
    /**
     * 유효하지 않은 멤버 ID가 제공될 때 발생하는 예외 처리
     * 
     * @param e InvalidMemberException 예외
     * @param request HTTP 요청 정보
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(InvalidMemberException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidMemberException(
            InvalidMemberException e, HttpServletRequest request) {
        log.error("Invalid member: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("INVALID_MEMBER", e.getMessage(), request.getRequestURI()));
    }
    
    /**
     * 일반 예외 처리
     * 처리되지 않은 모든 예외를 포착하여 서버 오류 응답을 반환합니다.
     * 
     * @param e Exception 예외
     * @param request HTTP 요청 정보
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception e, HttpServletRequest request) {
        log.error("Unexpected error on {} {}: ", request.getMethod(), request.getRequestURI(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("INTERNAL_SERVER_ERROR", 
                        "서버 오류가 발생했습니다", request.getRequestURI()));
    }
}