import axios from "axios";
import { signApi } from "./signApi";

// axios 기본 설정
const api = axios.create({
    baseURL: "/api",
    headers: {
        "Content-Type": "application/json",
    },
});

// 요청 인터셉터
api.interceptors.request.use(
    (config) => {
        // 모든 API 요청에 토큰 추가 (토큰이 있는 경우)
        const token = localStorage.getItem("accessToken");
        if (token) {
            config.headers = config.headers || {};
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 응답 인터셉터
api.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        // 로그인, 회원가입, 아이디/비밀번호 찾기 요청은 토큰 재발급 로직에서 제외
        const isLoginRequest =
            error.config.url?.includes("/sign/login") ||
            error.config.url?.includes("/sign/signUp") ||
            error.config.url?.includes("/sign/findId") ||
            error.config.url?.includes("/sign/checkIdEmail") ||
            error.config.url?.includes("/sign/updPw");

        // 토큰 재발급 요청 자체는 재발급 로직에서 제외
        const isRefreshRequest = error.config.url?.includes("/token/refresh");

        // 404 에러가 발생하고, 로그인/회원가입 요청이 아니고, 재발급 요청이 아니고, 아직 재시도하지 않은 경우
        if (
            error.response?.status === 404 &&
            !isLoginRequest &&
            !isRefreshRequest &&
            !error.config._retry
        ) {
            error.config._retry = true;

            // 토큰 재발급 시도
            try {
                const newToken = await signApi.refreshToken();
                localStorage.setItem("accessToken", newToken);
                error.config.headers["Authorization"] = `Bearer ${newToken}`;
                return api(error.config);
            } catch (refreshError) {
                console.error("토큰 재발급 실패:", refreshError);
                // 토큰 재발급 실패 시 로그인 페이지로 이동
                localStorage.removeItem("accessToken");
                window.location.href = "/";
                alert("세션이 만료되었습니다. 다시 로그인 해주세요.");
                return;
            }
        }

        return Promise.reject(error);
    }
);

export default api;
