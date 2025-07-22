package solid.backend.config;

import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 도커 실행 보안 설정
 */
@Configuration
public class DockerSecurityConfig {
    
    // 금지된 명령어 패턴
    private static final List<Pattern> FORBIDDEN_PATTERNS = Arrays.asList(
            Pattern.compile(".*\\brm\\s+-rf.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\bshutdown.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\breboot.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\bmkfs.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\bdd\\s+if=/dev/zero.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\bfork\\s*\\(\\s*\\).*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*:\\(\\)\\{.*:\\|:&.*\\};:.*", Pattern.CASE_INSENSITIVE), // Fork bomb
            Pattern.compile(".*\\bwget.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\bcurl.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\bnc\\s+-.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\bnetcat.*", Pattern.CASE_INSENSITIVE)
    );
    
    // 금지된 import/include 패턴
    private static final List<Pattern> FORBIDDEN_IMPORTS = Arrays.asList(
            Pattern.compile(".*import\\s+os.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*import\\s+subprocess.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*import\\s+socket.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*from\\s+os\\s+import.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*#include\\s*<unistd\\.h>.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*#include\\s*<sys/socket\\.h>.*", Pattern.CASE_INSENSITIVE)
    );
    
    /**
     * 코드 보안 검증
     */
    public void validateCode(String code, String language) {
        // 코드 길이 제한
        if (code.length() > 10000) {
            throw new SecurityException("코드가 너무 깁니다 (최대 10000자)");
        }
        
        // 금지된 패턴 검사
        for (Pattern pattern : FORBIDDEN_PATTERNS) {
            if (pattern.matcher(code).find()) {
                throw new SecurityException("금지된 명령어가 포함되어 있습니다");
            }
        }
        
        // 언어별 추가 검증
        switch (language.toLowerCase()) {
            case "python":
                validatePythonCode(code);
                break;
            case "java":
                validateJavaCode(code);
                break;
            case "javascript":
                validateJavaScriptCode(code);
                break;
            case "c":
            case "cpp":
                validateCCode(code);
                break;
        }
    }
    
    /**
     * Python 코드 검증
     */
    private void validatePythonCode(String code) {
        // 위험한 함수 호출 검사
        List<String> dangerousFunctions = Arrays.asList(
                "eval", "exec", "__import__", "compile", "open", "file"
        );
        
        for (String func : dangerousFunctions) {
            if (code.contains(func + "(")) {
                throw new SecurityException("금지된 함수가 포함되어 있습니다: " + func);
            }
        }
        
        // 금지된 import 검사
        for (Pattern pattern : FORBIDDEN_IMPORTS) {
            if (pattern.matcher(code).find()) {
                throw new SecurityException("금지된 import가 포함되어 있습니다");
            }
        }
    }
    
    /**
     * Java 코드 검증
     */
    private void validateJavaCode(String code) {
        // Runtime.exec() 검사
        if (code.contains("Runtime") && code.contains("exec")) {
            throw new SecurityException("Runtime.exec()는 사용할 수 없습니다");
        }
        
        // ProcessBuilder 검사
        if (code.contains("ProcessBuilder")) {
            throw new SecurityException("ProcessBuilder는 사용할 수 없습니다");
        }
        
        // File I/O 제한
        if (code.contains("java.io.File") || code.contains("java.nio.file")) {
            throw new SecurityException("파일 시스템 접근은 제한됩니다");
        }
    }
    
    /**
     * JavaScript 코드 검증
     */
    private void validateJavaScriptCode(String code) {
        // eval 검사
        if (code.contains("eval(")) {
            throw new SecurityException("eval()은 사용할 수 없습니다");
        }
        
        // require 검사 (Node.js 모듈)
        List<String> dangerousModules = Arrays.asList(
                "child_process", "fs", "net", "dgram", "cluster"
        );
        
        for (String module : dangerousModules) {
            if (code.contains("require('" + module + "'") || 
                code.contains("require(\"" + module + "\"")) {
                throw new SecurityException("금지된 모듈입니다: " + module);
            }
        }
    }
    
    /**
     * C/C++ 코드 검증
     */
    private void validateCCode(String code) {
        // system() 함수 검사
        if (code.contains("system(")) {
            throw new SecurityException("system() 함수는 사용할 수 없습니다");
        }
        
        // exec 계열 함수 검사
        List<String> execFunctions = Arrays.asList(
                "execl", "execle", "execlp", "execv", "execve", "execvp"
        );
        
        for (String func : execFunctions) {
            if (code.contains(func + "(")) {
                throw new SecurityException("exec 계열 함수는 사용할 수 없습니다");
            }
        }
        
        // 금지된 헤더 검사
        for (Pattern pattern : FORBIDDEN_IMPORTS) {
            if (pattern.matcher(code).find()) {
                throw new SecurityException("금지된 헤더 파일이 포함되어 있습니다");
            }
        }
    }
}