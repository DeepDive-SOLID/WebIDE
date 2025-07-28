import { useContext } from "react";
import { AuthContext } from "../contexts/AuthContext.ts";

// 다른 컴포넌트에서 AuthContext를 쉽게 사용할 수 있도록 하는 커스텀 훅
export const useAuth = () => {
  const context = useContext(AuthContext);

  // AuthProvider로 감싸지 않은 컴포넌트에서 useAuth를 사용할 경우 에러 발생
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
};
