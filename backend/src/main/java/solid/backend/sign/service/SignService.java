package solid.backend.sign.service;

import jakarta.servlet.http.HttpServletRequest;
import solid.backend.sign.dto.*;

public interface SignService {
    /**
     * 설명: 회원가입
     * @param signUpDto
     */
    void signUpDto(SignUpDto signUpDto);

    /**
     * 설명: 회원가입 아이디 중복 확인
     * @param memberId
     */
    Boolean isDuplicatedId(String memberId);

    /**
     * 설명: 회원가입 이메일 중복 확인
     * @param memberEmail
     */
    Boolean isDuplicatedEmail(String memberEmail);

    /**
     * 설명: 로그인
     * @param signInDto
     * @param request
     */
    String login(SignInDto signInDto, HttpServletRequest request);

    /**
     * 설명: 아이디 찾기
     * @param signFindIdDto
     */
    String findMemberId(SignFindIdDto signFindIdDto);

    /**
     * 설명: 아이디 이메일 체크
     * @param signCheckIdEmailDto
     */
    void checkIdEmail(SignCheckIdEmailDto signCheckIdEmailDto);

    /**
     * 설명: 비밀번호 재설정
     * @param signUpdPwDto
     */
    void updPw(SignUpdPwDto signUpdPwDto);

    /**
     * 설명 : 카카오 로그인
     * @param code
     * @param request
     * @return String
     */
    String loginKakao(String code, HttpServletRequest request);

    /**
     * 설명 : 구글 로그인
     * @param code
     * @param request
     * @return String
     */
    String loginGoogle(String code, HttpServletRequest request);
}
