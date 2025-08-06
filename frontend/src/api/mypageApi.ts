import type { MypageDto, MypageProfileDto } from "../types/mypage";
import api from "./axios";


// 회원 정보 조회 (프로필)
export const getProfileDto = async (
  memberId: string
): Promise<MypageProfileDto> => {
  const response = await api.post<MypageProfileDto>(
    "/mypage/member/getProfileDto",
    memberId,
    {
      headers: { "Content-Type": "text/plain" },
    }
  );
  return response.data;
};

// 회원 정보 조회
export const getMemberDto = async (memberId: string): Promise<MypageDto> => {
  const response = await api.post<MypageDto>(
    "/mypage/member/getMemberDto",
    memberId,
    {
      headers: { "Content-Type": "text/plain" },
    }
  );
  return response.data;
};

// 회원 정보 수정
export const updateMemberDto = async (formData: FormData): Promise<string> => {
  const response = await api.put<string>(
    "/mypage/member/updateMemberDto",
    formData,
    {
      headers: { "Content-Type": "multipart/form-data" },
    }
  );
  return response.data;
};

// 이메일 중복 확인
export const checkEmail = async (email: string): Promise<boolean> => {
  const response = await api.post<boolean>(
    "/mypage/member/checkEmail",
    email,
    {
      headers: { "Content-Type": "text/plain" },
    }
  );
  return response.data;
};

// 회원 정보 삭제
export const deleteMemberDto = async (memberId: string): Promise<string> => {
  const response = await api.delete<string>(
    "/mypage/member/deleteMemberDto",
    {
      data: memberId,
      headers: { "Content-Type": "text/plain" },
    } as any // 여기에 타입 오류가 있을 경우 이렇게 임시 우회 가능
  );
  return response.data;
};
