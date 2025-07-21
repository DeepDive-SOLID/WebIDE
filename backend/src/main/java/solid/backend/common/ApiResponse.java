package solid.backend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 통일된 API 응답 구조
 * 
 * WebIDE의 모든 REST API 응답은 이 형식을 따릅니다.
 * 일관된 응답 구조를 통해 클라이언트에서 예측 가능한 방식으로 응답을 처리할 수 있습니다.
 * 
 * 사용 예시:
 * - 성공: ApiResponse.success(data, "메시지")
 * - 실패: ApiResponse.error("오류 메시지", "ERROR_CODE")
 * 
 * @param <T> 응답 데이터 타입
 * @since 2025-07-21 Container 모듈에 전면 적용
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * 요청 성공 여부
     */
    @Builder.Default
    private boolean success = true;
    
    /**
     * 응답 데이터
     */
    private T data;
    
    /**
     * 응답 메시지 (선택적)
     * 주로 오류 메시지나 안내 메시지에 사용
     */
    private String message;
    
    /**
     * 응답 시간
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 에러 코드 (선택적)
     * 오류 발생 시에만 포함
     */
    private String errorCode;
    
    /**
     * 요청 경로 (선택적)
     * 주로 오류 응답에서 디버깅용으로 사용
     */
    private String path;
    
    // ==================== 정적 팩토리 메서드 ====================
    
    /**
     * 성공 응답 생성 (데이터만)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    /**
     * 성공 응답 생성 (데이터 + 메시지)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }
    
    /**
     * 성공 응답 생성 (메시지만)
     */
    public static ApiResponse<Void> successMessage(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .build();
    }
    
    /**
     * 실패 응답 생성 (메시지만)
     */
    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .build();
    }
    
    /**
     * 실패 응답 생성 (메시지 + 에러코드)
     */
    public static ApiResponse<Void> error(String message, String errorCode) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
    
    /**
     * 실패 응답 생성 (전체 정보)
     */
    public static ApiResponse<Void> error(String message, String errorCode, String path) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .build();
    }
}