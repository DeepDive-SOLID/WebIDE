import React, { useState, useEffect } from "react";
import type { ReactNode } from "react";
import { AuthContext } from "./AuthContext";
import {
    isLoggedIn as checkIsLoggedIn,
    getCurrentUserInfo,
    refreshNewToken,
} from "../utils/auth";
import { signApi } from "../api/signApi";

interface AuthProviderProps {
    children: ReactNode;
}

export { AuthContext } from "./AuthContext";

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userInfo, setUserInfo] = useState<{
        memberId: string;
        authId?: string;
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

    const logout = async () => {
        try {
            // 백엔드에 로그아웃 요청 (세션의 refreshToken 제거)
            await signApi.logout();
        } catch (error) {
            console.error("로그아웃 요청 실패:", error);
        } finally {
            // 프론트엔드에서 accessToken 제거
            localStorage.removeItem("accessToken");
            setIsLoggedIn(false);
            setUserInfo(null);
            window.location.href = "/";
        }
    };

    const value = { isLoggedIn, userInfo, isLoading, login, logout };

    return (
        <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
    );
};
