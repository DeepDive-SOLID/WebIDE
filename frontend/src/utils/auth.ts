import { signApi } from "../api/signApi";

// JWT 토큰 페이로드 타입 정의
interface TokenPayload {
  memberId: string;
  authId: string;
  exp?: number;
  iat?: number;
  [key: string]: unknown;
}

// 토큰이 존재하고 유효한지 확인 (만료 시간 포함)
export const isLoggedIn = (): boolean => {
  const token = localStorage.getItem("accessToken");
  if (!token) return false;

  // 토큰 디코딩하여 만료 시간 확인
  const decoded = decodeToken(token);
  if (!decoded) return false;

  // 만료 시간 확인
  if (decoded.exp) {
    const currentTime = Math.floor(Date.now() / 1000);
    if (currentTime >= decoded.exp) {
      return false;
    }
  }
  return true;
};

// 토큰 가져오기
export const getToken = (): string | null => {
  return localStorage.getItem("accessToken");
};

// 토큰 저장하기
export const setToken = (token: string): void => {
  localStorage.setItem("accessToken", token);
};

// JWT 토큰 디코딩 (더 안전한 버전)
export const decodeToken = (token: string): TokenPayload | null => {
  try {
    // 토큰 형식 검증
    if (!token || typeof token !== "string") {
      console.error("유효하지 않은 토큰 형식");
      return null;
    }

    const parts = token.split(".");
    if (parts.length !== 3) {
      console.error("JWT 토큰 형식이 올바르지 않습니다");
      return null;
    }

    const base64Url = parts[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");

    // Base64 디코딩
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );

    const payload = JSON.parse(jsonPayload);
    return payload;
  } catch (error) {
    console.error("토큰 디코딩 실패:", error);
    return null;
  }
};

// 현재 로그인한 사용자의 memberId 가져오기
export const getCurrentMemberId = (): string | null => {
  const token = getToken();
  if (!token) return null;

  const decoded = decodeToken(token);
  if (!decoded) return null;

  return decoded.memberId || null;
};

// 현재 로그인한 사용자의 정보 가져오기
export const getCurrentUserInfo = (): {
  memberId: string;
  authId: string;
} | null => {
  const token = getToken();
  if (!token) return null;

  const decoded = decodeToken(token);
  if (!decoded?.memberId || !decoded?.authId) return null;

  return {
    memberId: decoded.memberId,
    authId: decoded.authId,
  };
};

// 토큰 유효성 체크 후, 필요 시 재발급
export const refreshNewToken = async (): Promise<string | null> => {
  const currentUserInfo = getCurrentUserInfo();

  // 사용자 정보가 없는 경우에는 재발급하지 않음
  if (!currentUserInfo) return null;

  try {
    const newToken = await signApi.refreshToken();
    localStorage.setItem("accessToken", newToken);
    return newToken;
  } catch (error) {
    console.error("토큰 재발급 실패", error);
    return null;
  }
};
