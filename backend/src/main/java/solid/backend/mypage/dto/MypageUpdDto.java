package solid.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypageUpdDto {

    private String memberId;
    private String memberName;
    private String memberPassword;
    private String memberEmail;
    private String memberPhone;
    private LocalDate memberBirth;
    private MultipartFile memberImg;
}
