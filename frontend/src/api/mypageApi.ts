import axios from "axios";
import type { MypageDto, MypageProfileDto } from "../types/mypage";

// mypageApi용 axios 인스턴스 (baseURL 없이 프록시 사용)
const mypageAxios = axios.create({
  headers: {
    "Content-Type": "application/json",
  },
});

// 회원 정보 조회 (프로필)
export const getProfileDto = async (
  memberId: string
): Promise<MypageProfileDto> => {
  const response = await mypageAxios.post(
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
  const response = await mypageAxios.post(
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
  const response = await mypageAxios.put(
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
  const response = await mypageAxios.post("/mypage/member/checkEmail", email, {
    headers: { "Content-Type": "text/plain" },
  });
  return response.data;
};

// 회원 정보 삭제
export const deleteMemberDto = async (memberId: string): Promise<string> => {
  const response = await mypageAxios.delete("/mypage/member/deleteMemberDto", {
    data: memberId,
    headers: { "Content-Type": "text/plain" },
  });
  return response.data;
};
