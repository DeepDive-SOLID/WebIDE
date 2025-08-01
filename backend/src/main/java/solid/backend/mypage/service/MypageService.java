package solid.backend.mypage.service;

import solid.backend.mypage.dto.MypageDto;
import solid.backend.mypage.dto.MypageProfileDto;
import solid.backend.mypage.dto.MypageUpdDto;

public interface MypageService {

    /**
     * 설명 : 회원 정보 조회(프로필)
     * @param memberId 회원 ID
     * @return MypageProfileDto
     */
    MypageProfileDto getProfileDto(String memberId);

    /**
     * 설명 : 회원 정보 조회
     * @param memberId 회원 ID
     * @return MypageDto
     */
    MypageDto getMemberDto(String memberId);

    /**
     * 설명 : 회원 정보 수정
     * @param memberDto 회원 DTO
     */
    void updateMemberDto(MypageUpdDto memberDto);

    /**
     * 설명 : 이메일 중복 체크
     * @param memberEmail 회원 이메일
     * @return Boolean
     */
    Boolean checkEmail(String memberEmail);

    /**
     * 설명 : 회원 정보 삭제
     * @param memberId 회원 ID
     */
    void deleteMemberDto(String memberId);
}
