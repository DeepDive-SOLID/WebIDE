package solid.backend.common;

public class DirectoryPathUtils {
    
    /**
     * 경로에서 부모 디렉터리 이름 추출
     * 예: "/알고리즘/정렬" → "알고리즘"
     */
    public static String extractParentName(String path) {
        if (path == null || "/".equals(path)) return null;
        
        String normalizedPath = normalizePath(path);
        if ("/".equals(normalizedPath)) return null;
        
        String[] parts = normalizedPath.split("/");
        if (parts.length <= 1) return null;
        
        return parts[parts.length - 1];
    }
    
    /**
     * 경로에서 부모 경로 추출
     * 예: "/알고리즘/정렬" → "/알고리즘"
     */
    public static String extractParentPath(String path) {
        if (path == null || "/".equals(path)) return "/";
        
        String normalizedPath = normalizePath(path);
        int lastSlash = normalizedPath.lastIndexOf("/");
        
        if (lastSlash <= 0) return "/";
        return normalizedPath.substring(0, lastSlash);
    }
    
    /**
     * 경로 깊이 계산
     * 예: "/" → 0, "/알고리즘" → 1, "/알고리즘/정렬" → 2
     */
    public static int calculateDepth(String path) {
        if (path == null || "/".equals(path)) return 0;
        
        String normalizedPath = normalizePath(path);
        if ("/".equals(normalizedPath)) return 0;
        
        return normalizedPath.split("/").length - 1;
    }
    
    /**
     * 경로 정규화 (중복 슬래시 제거, 끝 슬래시 제거)
     */
    public static String normalizePath(String path) {
        if (path == null) return "/";
        
        String normalized = path.replaceAll("/+", "/");
        
        // 끝에 있는 슬래시 제거 (루트가 아닌 경우)
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        
        return normalized.isEmpty() ? "/" : normalized;
    }
    
    /**
     * 전체 경로 생성
     * 예: parentPath="/알고리즘", name="정렬" → "/알고리즘/정렬"
     */
    public static String buildFullPath(String parentPath, String name) {
        if (name == null || name.isEmpty()) return parentPath;
        
        String normalizedParent = normalizePath(parentPath);
        
        if ("/".equals(normalizedParent)) {
            return "/" + name;
        }
        
        return normalizedParent + "/" + name;
    }
    
    /**
     * 경로가 다른 경로의 하위인지 확인
     */
    public static boolean isChildPath(String childPath, String parentPath) {
        String normalizedChild = normalizePath(childPath);
        String normalizedParent = normalizePath(parentPath);
        
        if ("/".equals(normalizedParent)) {
            return !"/".equals(normalizedChild);
        }
        
        return normalizedChild.startsWith(normalizedParent + "/");
    }
}