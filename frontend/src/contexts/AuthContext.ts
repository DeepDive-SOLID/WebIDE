import { createContext } from "react";

// AuthContext에서 제공할 데이터의 타입 정의
interface AuthContextType {
    isLoggedIn: boolean; // 현재 로그인 상태 (true: 로그인됨, false: 로그인 안됨)
    userInfo: { memberId: string; authId?: string } | null; // 사용자 정보 (ID, 권한)
    isLoading: boolean; // 초기 로딩 상태
    login: () => void; // 로그인 함수 (Context 상태를 true로 변경)
    logout: () => Promise<void>; // 로그아웃 함수 (토큰 제거 + Context 상태를 false로 변경)
}

// React Context 생성 (초기값은 undefined)
export const AuthContext = createContext<AuthContextType | undefined>(
    undefined
);
