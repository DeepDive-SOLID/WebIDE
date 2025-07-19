import React, { useState, useEffect } from "react";
import type { ReactNode } from "react";
import {
  isLoggedIn as checkIsLoggedIn,
  getCurrentUserInfo,
  refreshNewToken,
} from "../utils/auth";
import { AuthContext } from "./AuthContext";

// AuthProvider 컴포넌트의 props 타입 정의
interface AuthProviderProps {
  children: ReactNode; // AuthProvider로 감쌀 자식 컴포넌트들
}

// 로그인 상태를 전역적으로 관리하는 Provider 컴포넌트
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  // 로그인 상태를 관리하는 state (false: 로그인 안됨, true: 로그인됨)
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  // 사용자 정보를 관리하는 state
  const [userInfo, setUserInfo] = useState<{
    memberId: string;
    authId: string;
  } | null>(null);
  // 초기 로딩 상태
  const [isLoading, setIsLoading] = useState(true);

  // 컴포넌트가 처음 마운트될 때 실행되는 useEffect
  useEffect(() => {
    const initializeAuth = async () => {
      // 토큰이 유효한지 체크
      const loggedIn = checkIsLoggedIn();

      // 토큰이 유효한 경우
      if (loggedIn) {
        // 로그인한 사용자 정보 가져오기
        const currentUserInfo = getCurrentUserInfo();
        setUserInfo(currentUserInfo);
        setIsLoggedIn(true);

        // 유효한 토큰이 아닌 경우
      } else {
        const newToken = await refreshNewToken();

        // 사용자 정보가 있을 때
        if (newToken) {
          const currentUserInfo = getCurrentUserInfo();
          setUserInfo(currentUserInfo);
          setIsLoggedIn(true);

          // 사용자 정보가 없을 때
        } else {
          setIsLoggedIn(false);
          setUserInfo(null);
        }
      }

      // 초기화 완료
      setIsLoading(false);
    };

    initializeAuth();
  }, []);

  // 로그인 함수: Context의 로그인 상태를 true로 변경
  const login = () => {
    setIsLoggedIn(true);
    // 로그인 시 사용자 정보도 업데이트
    const currentUserInfo = getCurrentUserInfo();
    setUserInfo(currentUserInfo);
  };

  // 로그아웃 함수: 토큰 제거 + Context의 로그인 상태를 false로 변경 (나중에 구현)
  const logout = () => {
    // TODO: 로그아웃 API 구현 후 연결
    localStorage.removeItem("token");
    setIsLoggedIn(false); // Context 상태를 false로 변경
    setUserInfo(null); // 사용자 정보도 초기화
    window.location.href = "/";
  };

  // Context에 제공할 값들을 객체로 묶기
  const value = {
    isLoggedIn, // 현재 로그인 상태
    userInfo, // 사용자 정보 (ID, 권한)
    isLoading, // 초기 로딩 상태
    login, // 로그인 함수
    logout, // 로그아웃 함수
  };

  // Context.Provider로 자식 컴포넌트들을 감싸서 value를 전역적으로 제공
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
