package solid.backend.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 도커 클라이언트 설정
 */
@Configuration
public class DockerConfig {
    
    @Value("${docker.host:unix:///var/run/docker.sock}")
    private String dockerHost;
    
    @Value("${docker.api.version:1.41}")
    private String apiVersion;
    
    /**
     * 도커 클라이언트 빈 생성
     */
    @Bean
    public DockerClient dockerClient() {
        System.out.println("Docker Host Configuration: " + dockerHost);
        System.out.println("Docker API Version: " + apiVersion);
        
        // Docker Desktop for Windows in WSL2 환경에서는 환경 변수를 사용
        String effectiveDockerHost = dockerHost;
        
        // DOCKER_HOST 환경 변수 확인
        String dockerHostEnv = System.getenv("DOCKER_HOST");
        if (dockerHostEnv != null && !dockerHostEnv.isEmpty()) {
            effectiveDockerHost = dockerHostEnv;
            System.out.println("Using DOCKER_HOST from environment: " + effectiveDockerHost);
        }
        
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(effectiveDockerHost)
                .withApiVersion(apiVersion)
                .build();
                
        System.out.println("Final Docker Host URI: " + config.getDockerHost());
                
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
                
        return DockerClientImpl.getInstance(config, httpClient);
    }
}