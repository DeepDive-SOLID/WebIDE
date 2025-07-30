import React, { useState, useEffect } from "react";
import type { ReactNode } from "react";
import { AuthContext } from "./AuthContext";
import {
  isLoggedIn as checkIsLoggedIn,
  getCurrentUserInfo,
  refreshNewToken,
} from "../utils/auth";
import {useStore} from "../stores/store.ts";
import {useStomp} from "../hooks/useStomp.ts";

interface AuthProviderProps {
  children: ReactNode;
}

export { AuthContext } from "./AuthContext";

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState<{
    memberId: string;
    authId: string;
  } | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  // STOMP 클라이언트 설정 함수 가져오기
  const setStompClient = useStore((state) => state.setStompClient);
  // WebSocket 연결을 위한 connect 함수 가져오기
  const { connect } = useStomp();

  useEffect(() => {
    const initializeAuth = async () => {
      const loggedIn = checkIsLoggedIn();
      if (loggedIn) {
        const currentUserInfo = getCurrentUserInfo();
        setUserInfo(currentUserInfo);
        setIsLoggedIn(true);

        // WebSocket 연결
        connect({
          onConnect: () => console.log("STOMP 연결됨"),
          onError: (e) => console.error(e),
          onDisconnect: () => console.log("STOMP 연결 해제됨"),
        });
      } else {
        const newToken = await refreshNewToken();
        if (newToken) {
          const currentUserInfo = getCurrentUserInfo();
          setUserInfo(currentUserInfo);
          setIsLoggedIn(true);

          // WebSocket 연결
          connect({
            onConnect: () => console.log("STOMP 연결됨"),
            onError: (e) => console.error(e),
            onDisconnect: () => console.log("STOMP 연결 해제됨"),
          });
        } else {
          setIsLoggedIn(false);
          setUserInfo(null);
        }
      }
      setIsLoading(false);
    };

    initializeAuth();

    // 컴포넌트가 사라질 때 WebSocket 연결을 안전하게 끊어주는 역할
    return () => {
      const client = useStore.getState().stompClient;
      client?.deactivate();
    };
  }, [connect, setStompClient]);

  const login = () => {
    setIsLoggedIn(true);
    const currentUserInfo = getCurrentUserInfo();
    setUserInfo(currentUserInfo);

    // WebSocket 연결
    connect({
      onConnect: () => console.log("STOMP 연결됨 (login)"),
      onError: (e) => console.error(e),
      onDisconnect: () => console.log("STOMP 연결 해제됨 (login)"),
    });
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    setIsLoggedIn(false);
    setUserInfo(null);
    window.location.href = "/";
  };

  const value = { isLoggedIn, userInfo, isLoading, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
