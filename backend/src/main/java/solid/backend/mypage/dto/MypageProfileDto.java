package solid.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypageProfileDto {

    private String memberId;
    private String memberName;
    private String memberImg;
}
