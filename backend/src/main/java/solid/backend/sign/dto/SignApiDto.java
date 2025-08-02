package solid.backend.sign.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth")
public class SignApiDto {

    private Kakao kakao;
    private Google google;

    @Getter
    @Setter
    public static class Kakao {
        private String clientId;
    }

    @Getter
    @Setter
    public static class Google {
        private String clientId;
        private String clientSecret;
    }
}
