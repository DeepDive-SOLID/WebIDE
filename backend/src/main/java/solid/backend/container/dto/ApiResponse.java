package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API 공통 응답 형식
 * 모든 API 응답을 일관된 형식으로 제공
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    @Builder.Default
    private boolean success = true;
    
    private T data;
    
    private String message;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 성공 응답 생성
     * @param data 응답 데이터
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    /**
     * 성공 응답 생성 (메시지 포함)
     * @param data 응답 데이터
     * @param message 응답 메시지
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }
    
    /**
     * 메시지만 있는 성공 응답
     * @param message 응답 메시지
     * @return ApiResponse
     */
    public static ApiResponse<Void> successMessage(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .build();
    }
}