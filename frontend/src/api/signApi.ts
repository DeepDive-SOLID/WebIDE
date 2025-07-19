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

  checkId: async (signInCheckIdDto: SignInCheckIdDto): Promise<boolean> => {
    try {
      const res = await signAxios.post("/sign/checkId", signInCheckIdDto);
      return res.data; // true면 아이디가 있음, false면 아이디가 없음
    } catch (_) {
      throw new Error("ID 중복확인 중 오류가 발생했습니다.");
    }
  },

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

  login: async (signInDto: SignInDto): Promise<any> => {
    try {
      const res = await signAxios.post("/sign/login", signInDto);
      return res.data;
    } catch (error) {
      throw error;
    }
  },

  findId: async (signFindIdDto: SignFindIdDto): Promise<string> => {
    try {
      const res = await signAxios.post("/sign/findId", signFindIdDto);
      return res.data; // memberId 반환
    } catch (error) {
      throw error;
    }
  },

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

  updPw: async (signUpdPwDto: SignUpdPwDto): Promise<any> => {
    try {
      const res = await signAxios.put("/sign/updPw", signUpdPwDto);
      return res.data;
    } catch (error) {
      throw error;
    }
  },
};
