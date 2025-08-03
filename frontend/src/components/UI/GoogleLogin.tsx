import { FaGoogle } from "react-icons/fa";
import styles from "../../styles/GoogleLogin.module.scss";
import { useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { signApi } from "../../api/signApi";
import { AuthContext } from "../../contexts/AuthContext";
import { setToken } from "../../utils/auth";

const GoogleLogin = () => {
    const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;
    const REDIRECT_URI = "http://15.164.250.218/login/google/callback";
    const { login } = useContext(AuthContext)!;
    const navigate = useNavigate();

    useEffect(() => {
        const code = new URL(window.location.href).searchParams.get("code");
        const pathname = window.location.pathname;

        if (pathname === "/login/google/callback" && code) {
            (async () => {
                try {
                    const result = await signApi.loginGoogle(code);
                    setToken(result);
                    login();
                    navigate("/home/all-container");
                } catch {
                    alert("구글 로그인 실패");
                    navigate("/");
                }
            })();
        }
    }, [navigate]);

    const handleLogin = () => {
        const baseUrl = "https://accounts.google.com/o/oauth2/v2/auth";
        const params = new URLSearchParams({
            client_id: GOOGLE_CLIENT_ID,
            redirect_uri: REDIRECT_URI,
            response_type: "code",
            scope: "profile email",
            access_type: "offline",
            prompt: "consent select_account",
        });

        window.location.href = `${baseUrl}?${params.toString()}`;
    };

    if (window.location.pathname === "/login/google/callback") return null;

    return (
        <button type="button" className={styles.googleLoginButton} onClick={handleLogin}>
            <FaGoogle />
            구글로 로그인
        </button>
    );
};

export default GoogleLogin;
