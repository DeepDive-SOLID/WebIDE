import React, { useState, useEffect } from "react";
import type { ReactNode } from "react";
import { AuthContext } from "./AuthContext";
import {
  isLoggedIn as checkIsLoggedIn,
  getCurrentUserInfo,
  refreshNewToken,
} from "../utils/auth";

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

  useEffect(() => {
    const initializeAuth = async () => {
      const loggedIn = checkIsLoggedIn();
      if (loggedIn) {
        const currentUserInfo = getCurrentUserInfo();
        setUserInfo(currentUserInfo);
        setIsLoggedIn(true);
      } else {
        const newToken = await refreshNewToken();
        if (newToken) {
          const currentUserInfo = getCurrentUserInfo();
          setUserInfo(currentUserInfo);
          setIsLoggedIn(true);
        } else {
          setIsLoggedIn(false);
          setUserInfo(null);
        }
      }
      setIsLoading(false);
    };

    initializeAuth();
  }, []);

  const login = () => {
    setIsLoggedIn(true);
    const currentUserInfo = getCurrentUserInfo();
    setUserInfo(currentUserInfo);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    setUserInfo(null);
    window.location.href = "/";
  };

  const value = { isLoggedIn, userInfo, isLoading, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
