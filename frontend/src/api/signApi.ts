import axios from "axios";
import type {
  SignUpDto,
  SignInCheckIdDto,
  SignInCheckEmailDto,
  SignInDto,
  SignFindIdDto,
  SignCheckIdEmailDto,
  SignUpdPwDto,
} from "../types/auth";

// signApi용 axios 인스턴스 (baseURL 없이 직접 요청)
const signAxios = axios.create({
  headers: {
    "Content-Type": "application/json",
  },
});

// 회원가입
export const signApi = {
  signUp: async (signUpDto: SignUpDto): Promise<"SUCCESS" | "FAIL"> => {
    try {
      const res = await signAxios.post("/sign/signUp", signUpDto);
      if (res.data === "SUCCESS") return "SUCCESS";
      return "FAIL";
    } catch (_) {
      return "FAIL";
    }
  },

  // 아이디 중복확인
  checkId: async (signInCheckIdDto: SignInCheckIdDto): Promise<boolean> => {
    try {
      const res = await signAxios.post("/sign/checkId", signInCheckIdDto);
      return res.data; // true면 아이디가 있음, false면 아이디가 없음
    } catch (_) {
      throw new Error("ID 중복확인 중 오류가 발생했습니다.");
    }
  },

  // 이메일 중복확인
  checkEmail: async (
    signInCheckEmailDto: SignInCheckEmailDto
  ): Promise<boolean> => {
    try {
      const res = await signAxios.post("/sign/checkEmail", signInCheckEmailDto);
      return res.data; // true면 이메일이 있음, false면 이메일이 없음
    } catch (_) {
      throw new Error("이메일 중복확인 중 오류가 발생했습니다.");
    }
  },

  // 로그인
  login: async (signInDto: SignInDto): Promise<any> => {
    try {
      const res = await signAxios.post("/sign/login", signInDto);
      return res.data;
    } catch (error) {
      throw error;
    }
  },

  // 아이디 찾기
  findId: async (signFindIdDto: SignFindIdDto): Promise<string> => {
    try {
      const res = await signAxios.post("/sign/findId", signFindIdDto);
      return res.data; // memberId 반환
    } catch (error) {
      throw error;
    }
  },

  // 비밀번호 찾기 - 인증
  checkIdEmail: async (
    signCheckIdEmailDto: SignCheckIdEmailDto
  ): Promise<any> => {
    try {
      const res = await signAxios.post(
        "/sign/checkIdEmail",
        signCheckIdEmailDto
      );
      return res.data;
    } catch (error) {
      throw error;
    }
  },

  // 비밀번호 재설정
  updPw: async (signUpdPwDto: SignUpdPwDto): Promise<any> => {
    try {
      const res = await signAxios.put("/sign/updPw", signUpdPwDto);
      return res.data;
    } catch (error) {
      throw error;
    }
  },

  // 토큰 재발급
  refreshToken: async (): Promise<string> => {
    try {
      const res = await signAxios.post("/token/refresh");
      return res.data; // 새로운 access token 반환
    } catch (error) {
      throw error;
    }
  },
};
