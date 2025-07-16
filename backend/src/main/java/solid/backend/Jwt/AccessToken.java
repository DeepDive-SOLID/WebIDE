package solid.backend.Jwt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessToken {
    private String memberId;
    private String authId;
}
