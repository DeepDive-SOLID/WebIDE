import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import styles from "../styles/Login.module.scss";
import { logoImg } from "../assets";
import { signApi } from "../api/signApi";

// EyeIcon 컴포넌트 (비밀번호 보기/숨기기)
const EyeIcon = ({ visible }: { visible: boolean }) =>
  visible ? (
    <svg
      width="24"
      height="24"
      fill="none"
      stroke="#6b7280"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      viewBox="0 0 24 24"
    >
      <ellipse cx="12" cy="12" rx="8" ry="5" />
      <circle cx="12" cy="12" r="2.5" />
    </svg>
  ) : (
    <svg
      width="24"
      height="24"
      fill="none"
      stroke="#6b7280"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      viewBox="0 0 24 24"
    >
      <ellipse cx="12" cy="12" rx="8" ry="5" />
      <circle cx="12" cy="12" r="2.5" />
      <line x1="4" y1="4" x2="20" y2="20" />
    </svg>
  );

// 한글 제거 함수
function removeKorean(text: string) {
  return text.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, "");
}

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    memberId: "",
    memberPw: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [idError, setIdError] = useState("");
  const [pwError, setPwError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [submitError, setSubmitError] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const processedValue =
      name === "memberId" || name === "memberPw"
        ? removeKorean(value.replace(/\s/g, ""))
        : value;
    setFormData((prev) => ({ ...prev, [name]: processedValue }));
    if (name === "memberId") setIdError("");
    if (name === "memberPw") setPwError("");
    setSubmitError("");
  };

  const validateForm = () => {
    let valid = true;
    if (!formData.memberId.trim()) {
      setIdError("아이디를 입력하세요");
      valid = false;
    }
    if (!formData.memberPw) {
      setPwError("비밀번호를 입력하세요");
      valid = false;
    }
    return valid;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;
    setIsLoading(true);
    setSubmitError("");
    try {
      const token = await signApi.login({
        memberId: formData.memberId,
        memberPw: formData.memberPw,
      });
      // 로그인 성공 시 access token 저장
      localStorage.setItem("accessToken", token);
      navigate("/home/all-container"); // 모든 컨테이너 화면으로 이동
    } catch (error: unknown) {
      // 로그인 실패 시 처리
      if (
        error &&
        typeof error === "object" &&
        "response" in error &&
        error.response &&
        typeof error.response === "object" &&
        "status" in error.response &&
        error.response.status === 401
      ) {
        setSubmitError("아이디 또는 비밀번호가 올바르지 않습니다.");
      } else {
        setSubmitError("로그인 중 오류가 발생했습니다.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.pageWrapper}>
      <div className={styles.signInContainer}>
        <img
          src={logoImg}
          alt="로고"
          className={styles.logo}
          onClick={() => navigate("/")}
        />
        <h1 className={styles.title}>계정에 로그인하세요</h1>
        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.inputGroup}>
            <label className={styles.label}>ID</label>
            <input
              className={`${styles.input} ${idError ? styles.inputError : ""}`}
              type="text"
              name="memberId"
              placeholder="ID를 입력하세요"
              value={formData.memberId}
              onChange={handleChange}
              required
            />
            {idError && <div className={styles.fieldError}>{idError}</div>}
          </div>
          <div className={styles.inputGroup}>
            <label className={styles.label}>비밀번호</label>
            <div className={styles.passwordInputWrapper}>
              <input
                className={`${styles.input} ${
                  pwError ? styles.inputError : ""
                }`}
                type={showPassword ? "text" : "password"}
                name="memberPw"
                placeholder="비밀번호를 입력하세요"
                value={formData.memberPw}
                onChange={handleChange}
                required
              />
              <button
                type="button"
                className={styles.eyeBtn}
                onClick={() => setShowPassword((v) => !v)}
                tabIndex={-1}
                aria-label={showPassword ? "비밀번호 숨기기" : "비밀번호 보기"}
              >
                <EyeIcon visible={showPassword} />
              </button>
            </div>
            {pwError && <div className={styles.fieldError}>{pwError}</div>}
          </div>
          <Link to="/find-id" className={styles.forgotPwLink}>
            비밀번호를 잊으셨나요?
          </Link>
          {submitError && (
            <div className={styles.submitError}>{submitError}</div>
          )}
          <button className={styles.button} type="submit" disabled={isLoading}>
            {isLoading ? "로그인 중..." : "로그인"}
          </button>
        </form>
        <div className={styles.signUpGuide}>
          계정이 없으신가요?{" "}
          <Link to="/signup" className={styles.signUpLink}>
            회원가입
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
