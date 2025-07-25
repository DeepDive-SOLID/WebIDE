import axios from "axios";

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

    // 서버에서 토큰 만료된 경우
    if (error.response?.status === 401 && !isLoginRequest) {
      localStorage.removeItem("accessToken");
      alert("세션이 만료되었습니다. 다시 로그인 해주세요.");
      return;
    }

    // 프론트에서 토큰 만료라고 판단될 경우
    const isLoggedIn = () => {
      const token = localStorage.getItem("accessToken");
      return !!token;
    };

    if (!isLoggedIn() && !error.config._retry && !isLoginRequest) {
      error.config._retry = true;

      // 토큰 재발급 시도
      try {
        const refreshResponse = await axios.post("/api/token/refresh");
        const newToken = refreshResponse.data as string;
        localStorage.setItem("accessToken", newToken);
        error.config.headers["Authorization"] = `Bearer ${newToken}`;
        return api(error.config);
      } catch {
        // 토큰 재발급 실패 시 로그인 페이지로 이동
        localStorage.removeItem("accessToken");
        window.location.href = "/";
        return;
      }
    }
    return Promise.reject(error);
  }
);

export default api;
