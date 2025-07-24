package solid.backend.sign.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solid.backend.sign.dto.*;
import solid.backend.sign.service.SignService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sign")
public class SignController {

    private final SignService signService;

    /**
     * 설명: 회원가입
     * @param signUpDto
     * @return ResponseEntity<String>
     */
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpDto) {
        try {
            signService.signUpDto(signUpDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("FAIL");
        }
    }

    /**
     * 설명: 회원가입 아이디 중복 확인
     * @param signInCheckIdDto
     * @return ResponseEntity<Boolean> (아이디가 있으면 true, 없으면 false)
     */
    @PostMapping("/checkId")
    public ResponseEntity<Boolean> checkId(@RequestBody SignUpCheckIdDto signInCheckIdDto) {
        boolean isDuplicate = signService.isDuplicatedId(signInCheckIdDto.getMemberId());
        return ResponseEntity.ok(isDuplicate);
    }

    /**
     * 설명: 회원가입 이메일 중복 확인
     * @param signInCheckEmailDto
     * @return ResponseEntity<Boolean> (이메일이 있으면 true, 없으면 false)
     */
    @PostMapping("/checkEmail")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmail(@RequestBody SignUpCheckEmailDto signInCheckEmailDto) {
        boolean isDuplicate = signService.isDuplicatedEmail(signInCheckEmailDto.getMemberEmail());
        return ResponseEntity.ok(isDuplicate);
    }

    /**
     * 설명: 로그인 시 토큰 발급
     * @param signInDto
     * @return ResponseEntity<String>
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody SignInDto signInDto, HttpServletRequest request) {
        try {
            String token = signService.login(signInDto, request);
            return ResponseEntity.ok(token);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("LOGIN_FAIL: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("LOGIN_ERROR: 알 수 없는 오류가 발생했습니다.");
        }
    }

    /**
     * 설명: 아이디 찾기 (이메일 확인 후 아이디 반환)
     * @param signFindIdDto
     * @return String
     */
    @ResponseBody
    @PostMapping("/findId")
    public String findMemberId(@RequestBody SignFindIdDto signFindIdDto) {
        return signService.findMemberId(signFindIdDto);
    }

    /**
     * 설명: 아이디, 이메일 확인
     * @param signCheckIdEmailDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PostMapping("/checkIdEmail")
    public ResponseEntity<String> checkIdEmail(@RequestBody SignCheckIdEmailDto signCheckIdEmailDto) {
        try {
            signService.checkIdEmail(signCheckIdEmailDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("FAIL");
        }
    }

    /**
     * 설명: 비밀번호 재설정
     * @param signUpdPwDto
     * @return ResponseEntity<String>
     */
    @ResponseBody
    @PutMapping("/updPw")
    public ResponseEntity<String> updPw(@RequestBody SignUpdPwDto signUpdPwDto) {
        try {
            signService.updPw(signUpdPwDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("FAIL");
        }
    }

    /**
     * 설명: 로그아웃 (세션에 저장된 refresh token 삭제)
     * @param request
     * @return ResponseEntity<String>
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("refreshToken");
        }
        return ResponseEntity.ok("로그아웃되었습니다.");
    }
}