package solid.backend.sign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    private String memberId;
    private String memberName;
    private String memberPw;
    private String memberEmail;
    private String memberPhone;
    private String memberBirth;
}
