import { useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../contexts/AuthContext";
import { signApi } from "../../api/signApi";
import { setToken } from "../../utils/auth";
import styles from "../../styles/KakaoLogin.module.scss";

const KakaoLogin = () => {
    const REST_API_KEY = import.meta.env.VITE_KAKAO_REST_API_KEY;
    const REDIRECT_URI = "http://localhost:5173/login/kakao/callback";
    const { login } = useContext(AuthContext)!;
    const navigate = useNavigate();

    useEffect(() => {
        const code = new URL(window.location.href).searchParams.get("code");
        const pathname = window.location.pathname;

        if (pathname === "/login/kakao/callback" && code) {
            (async () => {
                try {
                    const result = await signApi.loginKakao(code);
                    setToken(result);
                    login();
                    navigate("/home/all-container");
                } catch {
                    alert("카카오 로그인 실패");
                    navigate("/");
                }
            })();
        }
    }, [navigate]);

    const handleLogin = () => {
        const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code&prompt=login&force_signup=true`;
        window.location.href = KAKAO_AUTH_URL;
    };

    if (window.location.pathname === "/login/kakao/callback") return null;

    return (
        <button
            type="button"
            onClick={handleLogin}
            className={styles.kakaoButton}
        >
            <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
            >
                <path d="M12 0C5.372 0 0 4.85 0 10.833c0 3.17 1.706 6.01 4.423 7.923L3 24l6.3-3.485c.92.252 1.89.388 2.7.388 6.628 0 12-4.85 12-10.833S18.628 0 12 0z" />
            </svg>
            카카오로 로그인
        </button>
    );
};

export default KakaoLogin;