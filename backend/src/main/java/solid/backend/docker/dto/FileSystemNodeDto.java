package solid.backend.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 파일 시스템 노드 DTO
 * 
 * 디렉토리 트리 구조를 표현하기 위한 재귀적 데이터 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileSystemNodeDto {
    
    /**
     * 노드 이름 (파일명 또는 디렉토리명)
     */
    private String name;
    
    /**
     * 전체 경로
     */
    private String path;
    
    /**
     * 노드 타입
     */
    private NodeType type;
    
    /**
     * 파일 내용 (파일인 경우만)
     */
    private String content;
    
    /**
     * 파일 크기 (바이트)
     */
    private Long size;
    
    /**
     * 하위 노드 목록 (디렉토리인 경우만)
     */
    private List<FileSystemNodeDto> children;
    
    /**
     * 파일/디렉토리 권한
     */
    @Builder.Default
    private String permissions = "755";
    
    public enum NodeType {
        FILE,
        DIRECTORY
    }
}