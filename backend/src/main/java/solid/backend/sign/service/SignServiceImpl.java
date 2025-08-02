package solid.backend.sign.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import solid.backend.Jwt.AccessToken;
import solid.backend.Jwt.JwtUtil;
import solid.backend.entity.Member;
import solid.backend.jpaRepository.MemberRepository;
import solid.backend.sign.dto.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final MemberRepository signRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SignApiDto signApiDto;

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

    /**
     * 설명 : 카카오 로그인
     * @param code
     * @param request
     * @return String
     */
    public String loginKakao(String code, HttpServletRequest request) {

        // 1. 엑세스 토큰 요청
        String kakaoTokenUrl = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("client_id", signApiDto.getKakao().getClientId());
        tokenParams.add("redirect_uri", "http://localhost:5173/login/kakao/callback");
        tokenParams.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(kakaoTokenUrl, tokenRequest, Map.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("카카오 액세스 토큰을 가져오지 못했습니다.");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. 사용자 정보 요청
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userInfoRequest, Map.class);

        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("카카오 사용자 정보를 가져오지 못했습니다.");
        }

        Map<String, Object> body = userInfoResponse.getBody();
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        // 이메일 신청을 못해서 전달받은 고유 id값 사용
        String id = String.valueOf(body.get("id"));
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        // 3. 회원 정보 확인 및 없으면 등록
        Member member = signRepository.findByKakaoId(id)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .memberId(id)
                            .kakaoId(id)
                            .memberEmail(email == null ? "카카오_이메일_없음" : email)
                            .memberName(nickname)
                            .memberPassword("카카오_비밀번호_없음")
                            .memberPhone("카카오_전화번호_없음")
                            .memberBirth(LocalDate.parse("9999-12-31"))
                            .build();
                    return signRepository.save(newMember);
                });

        // 4. JWT 토큰 생성
        AccessToken tokenPayload = AccessToken.builder()
                .memberId(member.getMemberId())
                .build();

        String newAccessToken = jwtUtil.createAccessToken(tokenPayload);
        String refreshToken = jwtUtil.createRefreshToken(tokenPayload);

        // 5. refreshToken 세션 저장
        HttpSession session = request.getSession(true);
        session.setAttribute("refreshToken", refreshToken);

        // 6. accessToken 반환
        return newAccessToken;
    }

    /**
     * 설명 : 구글 로그인
     * @param code 인가 코드
     * @param request HttpServletRequest (세션 저장용)
     * @return accessToken (JWT)
     */
    public String loginGoogle(String code, HttpServletRequest request) {

        // 1. 엑세스 토큰 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", signApiDto.getGoogle().getClientId());
        params.add("client_secret", signApiDto.getGoogle().getClientSecret());
        params.add("redirect_uri", "http://localhost:5173/login/google/callback");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                tokenRequest,
                Map.class
        );

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("구글 토큰 요청 실패");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. 사용자 정보 요청
        HttpHeaders infoHeaders = new HttpHeaders();
        infoHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> infoRequest = new HttpEntity<>(infoHeaders);

        ResponseEntity<Map> infoResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                infoRequest,
                Map.class
        );

        if (!infoResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("구글 사용자 정보 요청 실패");
        }

        Map<String, Object> userInfo = infoResponse.getBody();
        String email = (String) userInfo.get("email");

        // 이메일 @앞에 정보로 id 생성
        String googleId = email.split("@")[0];

        // 3. 회원 정보 확인 및 없으면 등록
        Member member = signRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .memberId(googleId)
                            .googleId(googleId)
                            .memberEmail(email)
                            .memberName(googleId)
                            .memberPassword("구글_비밀번호_없음")
                            .memberPhone("구글_전화번호_없음")
                            .memberBirth(LocalDate.parse("9999-12-31"))
                            .build();
                    return signRepository.save(newMember);
                });

        // 4. JWT 토큰 생성
        AccessToken tokenPayload = AccessToken.builder()
                .memberId(member.getMemberId())
                .build();

        String newAccessToken = jwtUtil.createAccessToken(tokenPayload);
        String refreshToken = jwtUtil.createRefreshToken(tokenPayload);

        // 5. refreshToken 세션 저장
        HttpSession session = request.getSession(true);
        session.setAttribute("refreshToken", refreshToken);

        // 6. accessToken 반환
        return newAccessToken;
    }
}