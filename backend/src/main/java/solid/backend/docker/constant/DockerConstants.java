package solid.backend.docker.constant;

/**
 * 도커 관련 상수
 */
public final class DockerConstants {
    
    private DockerConstants() {}
    
    // 도커 이미지
    public static final String PYTHON_IMAGE = "python:3.9-slim";
    public static final String JAVA_IMAGE = "openjdk:17-slim";
    public static final String NODE_IMAGE = "node:18-slim";
    public static final String CPP_IMAGE = "gcc:latest";
    
    // 리소스 제한
    public static final long MEMORY_LIMIT = 512 * 1024 * 1024L; // 512MB
    public static final long CPU_SHARES = 512; // 0.5 CPU
    public static final long EXECUTION_TIMEOUT = 30; // 30초
    
    // 파일 경로
    public static final String CONTAINER_WORKSPACE = "/workspace";
    public static final String CODE_FILE_PREFIX = "code_";
    
    // 실행 상태
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_TIMEOUT = "TIMEOUT";
    
    // 에러 메시지
    public static final String ERROR_CONTAINER_NOT_FOUND = "컨테이너를 찾을 수 없습니다";
    public static final String ERROR_EXECUTION_FAILED = "코드 실행에 실패했습니다";
    public static final String ERROR_UNSUPPORTED_LANGUAGE = "지원하지 않는 언어입니다";
    public static final String ERROR_FILE_NOT_FOUND = "파일을 찾을 수 없습니다";
    public static final String ERROR_TIMEOUT = "실행 시간이 초과되었습니다";
}