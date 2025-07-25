package solid.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MypageDto {

    private String memberName;
    private String memberPassword;
    private String memberEmail;
    private String memberPhone;
    private LocalDate memberBirth;
    private String memberImg;
}
