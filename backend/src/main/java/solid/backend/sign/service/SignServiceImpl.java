package solid.backend.sign.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import solid.backend.Jwt.AccessToken;
import solid.backend.Jwt.JwtUtil;
import solid.backend.entity.Member;
import solid.backend.jpaRepository.MemberRepository;
import solid.backend.sign.dto.*;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final MemberRepository signRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 설명: 회원가입
     * @param signUpDto
     */
    @Override
    public void signUpDto(SignUpDto signUpDto) {
        Member member = new Member();

        member.setMemberId(signUpDto.getMemberId());
        member.setMemberName(signUpDto.getMemberName());
        String encodedPw = passwordEncoder.encode(signUpDto.getMemberPw());
        member.setMemberPassword(encodedPw);
        member.setMemberEmail(signUpDto.getMemberEmail());
        member.setMemberPhone(signUpDto.getMemberPhone());
        member.setMemberBirth(LocalDate.parse(signUpDto.getMemberBirth()));

        signRepository.save(member);
    }

    /**
     * 설명: 회원가입 아이디 중복 확인
     * @param memberId
     * @return Boolean (중복이면 true, 아니면 false)
     */
    @Override
    public Boolean isDuplicatedId(String memberId) {
        return signRepository.findById(memberId).isPresent();
    }

    /**
     * 설명: 회원가입 이메일 중복 확인
     * @param memberEmail
     * @return Boolean (중복이면 true, 아니면 false)
     */
    @Override
    public Boolean isDuplicatedEmail(String memberEmail) {
        return signRepository.findByMemberEmail(memberEmail).isPresent();
    }

    /**
     * 설명: 로그인 시 토큰 발급
     * @param signInDto
     * @param request
     * @return token
     */
    @Override
    public String login(SignInDto signInDto, HttpServletRequest request) {
        // 1. 사용자 조회
        Optional<Member> optionalMember = signRepository.findById(signInDto.getMemberId());
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }

        Member member = optionalMember.get();

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(signInDto.getMemberPw(), member.getMemberPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰에 담을 최소한의 사용자 정보 구성
        AccessToken dto = AccessToken.builder()
                .memberId(member.getMemberId()) // 우선은 memberId만
                .build();

        // 4. JWT 발급
        String accessToken = jwtUtil.createAccessToken(dto);

        // refreshToken은 백엔드에서 저장 필요.
        String refreshToken = jwtUtil.createRefreshToken(dto);

        System.out.println("accessToken: " + accessToken);
        System.out.println("refreshToken: " + refreshToken);


        // 세션에 refreshToken 저장
        HttpSession session = request.getSession(true);
        session.setAttribute("refreshToken", refreshToken);

        // 5. access token 반환
        return accessToken;
    }

    /**
     * 설명: 아이디 찾기
     * @param signFindIdDto
     * @return memberId
     */
    @Override
    public String findMemberId(SignFindIdDto signFindIdDto) {
        if(signFindIdDto.getMemberEmail() == null)
            throw new IllegalArgumentException("회원이 없습니다.");
        Member member = signRepository.findByMemberEmail(signFindIdDto.getMemberEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        return member.getMemberId();
    }

    /**
     * 설명: 아이디 이메일 체크
     * @param signCheckIdEmailDto
     */
    @Override
    public void checkIdEmail(SignCheckIdEmailDto signCheckIdEmailDto) {
        Member member = signRepository.findById(signCheckIdEmailDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        if (!member.getMemberEmail().equals(signCheckIdEmailDto.getMemberEmail())) {
            throw new IllegalArgumentException("아이디와 이메일이 일치하지 않습니다.");
        }
    }

    /**
     * 설명: 비밀번호 재설정
     * @param signUpdPwDto
     */
    @Override
    public void updPw(SignUpdPwDto signUpdPwDto) {
        Member member = signRepository.findById(signUpdPwDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        String encodedPw = passwordEncoder.encode(signUpdPwDto.getMemberPw());
        member.setMemberPassword(encodedPw);

        signRepository.save(member);
    }
}
