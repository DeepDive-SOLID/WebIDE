package solid.backend.mypage.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solid.backend.mypage.dto.MypageDto;
import solid.backend.mypage.dto.MypageProfileDto;
import solid.backend.mypage.dto.MypageUpdDto;
import solid.backend.mypage.service.MypageService;

@Controller
@AllArgsConstructor
@RequestMapping("/mypage/member")
public class MypageController {
    
    private final MypageService mypageService;

    /**
     * 설명 : 회원 정보 조회(프로필)
     * @param memberId 회원 ID
     * @return MypageProfileDto
     */
    @ResponseBody
    @PostMapping("/getProfileDto")
    public MypageProfileDto getProfileDto(@RequestBody String memberId) {
        return mypageService.getProfileDto(memberId);
    }

    /**
     * 설명 : 회원 정보 조회
     * @param memberId 회원 ID
     * @return MypageDto
     */
    @ResponseBody
    @PostMapping("/getMemberDto")
    public MypageDto getMemberDto(@RequestBody String memberId) {
        return mypageService.getMemberDto(memberId);
    }

    /**
     * 설명 : 회원 정보 수정
     * @param memberDto 회원 DTO
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PutMapping("/updateMemberDto")
    public ResponseEntity<String> updateMemberDto(@ModelAttribute MypageUpdDto memberDto) {
        try {
            mypageService.updateMemberDto(memberDto);
            return ResponseEntity.ok("SUCCESS");

            // 사용자가 잘못 입력한 경우 (ex: 파일 크기 초과 등)
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

            // 그 외 서버 오류
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    /**
     * 설명 : 이메일 중복 체크
     * @param memberEmail 회원 이메일
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PostMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestBody String memberEmail) {
        return ResponseEntity.ok(mypageService.checkEmail(memberEmail));
    }

    /**
     * 설명 : 회원 정보 삭제
     * @param memberId 회원 ID
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @DeleteMapping("/deleteMemberDto")
    public ResponseEntity<String> deleteMemberDto(@RequestBody String memberId) {
        try {
            mypageService.deleteMemberDto(memberId);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }
}
