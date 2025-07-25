import React, { useState } from "react";
import styles from "../styles/Find.module.scss";
import { Link, useNavigate } from "react-router-dom";
import { logoImg } from "../assets";
import { backIcon } from "../assets";
import { signApi } from "../api/signApi";

function isValidPassword(password: string) {
  return /^(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/.test(
    password
  );
}
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
function removeKorean(text: string) {
  return text.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, "");
}

const FindPw: React.FC = () => {
  const navigate = useNavigate();
  const [tab, setTab] = useState(1);
  const [isVerified, setIsVerified] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [id, setId] = useState("");
  const [email, setEmail] = useState("");
  const [verificationError, setVerificationError] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [showNewPassword, setShowNewPassword] = useState(false);

  const handleTab = (idx: number) => {
    setTab(idx);
    if (idx === 0) {
      navigate("/find-id");
    }
  };

  const handleVerification = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id.trim() || !email.trim()) {
      setVerificationError("아이디와 이메일을 모두 입력해주세요.");
      return;
    }
    setIsLoading(true);
    setVerificationError("");
    try {
      await signApi.checkIdEmail({ memberId: id, memberEmail: email });
      setIsVerified(true);
      setVerificationError("");
    } catch (error: unknown) {
      if (
        error &&
        typeof error === "object" &&
        "response" in error &&
        error.response &&
        typeof error.response === "object" &&
        "status" in error.response
      ) {
        const status = error.response.status;
        if (status === 404 || status === 500) {
          setVerificationError("일치하는 사용자 정보가 없습니다.");
        } else {
          setVerificationError("인증 중 오류가 발생했습니다.");
        }
      } else {
        setVerificationError("인증 중 오류가 발생했습니다.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handlePasswordReset = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPassword) {
      setPasswordError("새 비밀번호를 입력해주세요.");
      return;
    }
    if (!isValidPassword(newPassword)) {
      setPasswordError(
        "비밀번호는 최소 8자 이상, 숫자와 특수문자를 포함해야 합니다."
      );
      return;
    }
    setIsLoading(true);
    setPasswordError("");
    try {
      await signApi.updPw({
        memberId: id,
        memberPw: newPassword,
      });
      alert("비밀번호가 성공적으로 재설정되었습니다.");
      navigate("/");
    } catch (error: unknown) {
      if (
        error &&
        typeof error === "object" &&
        "response" in error &&
        error.response &&
        typeof error.response === "object" &&
        "status" in error.response
      ) {
        const status = error.response.status;
        if (status === 404 || status === 500) {
          setPasswordError("사용자 정보를 찾을 수 없습니다.");
        } else {
          setPasswordError("비밀번호 재설정 중 오류가 발생했습니다.");
        }
      } else {
        setPasswordError("비밀번호 재설정 중 오류가 발생했습니다.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.pageWrapper}>
      <div className={styles.findPwContainer}>
        <button
          type="button"
          className={styles.backBtn}
          onClick={() => navigate("/")}
          aria-label="뒤로가기"
        >
          <img
            src={backIcon}
            alt="뒤로가기"
            style={{ width: 28, height: 28 }}
          />
        </button>
        <img
          src={logoImg}
          alt="로고"
          className={styles.logo}
          onClick={() => navigate("/")}
        />
        <div className={styles.tabs}>
          <div
            className={
              tab === 0 ? `${styles.tab} ${styles.active}` : styles.tab
            }
            onClick={() => handleTab(0)}
            role="button"
            tabIndex={0}
          >
            아이디 찾기
          </div>
          <div
            className={
              tab === 1 ? `${styles.tab} ${styles.active}` : styles.tab
            }
            onClick={() => handleTab(1)}
            role="button"
            tabIndex={0}
          >
            비밀번호 찾기
          </div>
        </div>
        {!isVerified ? (
          <form className={styles.form} onSubmit={handleVerification}>
            <div className={styles.inputGroup}>
              <label htmlFor="id">ID</label>
              <input
                id="id"
                type="text"
                placeholder="아이디를 입력하세요"
                value={id}
                onChange={(e) => {
                  const processedValue = removeKorean(
                    e.target.value.replace(/\s/g, "")
                  );
                  setId(processedValue);
                  setVerificationError("");
                }}
              />
            </div>
            <div className={styles.inputGroup}>
              <label htmlFor="email">이메일</label>
              <input
                id="email"
                type="email"
                placeholder="이메일을 입력하세요"
                value={email}
                onChange={(e) => {
                  const processedValue = removeKorean(
                    e.target.value.replace(/\s/g, "")
                  );
                  setEmail(processedValue);
                  setVerificationError("");
                }}
              />
            </div>
            {verificationError && (
              <div className={styles.errorText}>{verificationError}</div>
            )}
            <button
              className={styles.button}
              type="submit"
              disabled={isLoading}
            >
              {isLoading ? "확인 중..." : "인증하기"}
            </button>
          </form>
        ) : (
          <form className={styles.form} onSubmit={handlePasswordReset}>
            <div className={styles.inputGroup}>
              <label htmlFor="newPassword">새 비밀번호</label>
              <div className={styles.passwordInputWrapper}>
                <input
                  id="newPassword"
                  type={showNewPassword ? "text" : "password"}
                  placeholder="새 비밀번호 (8자 이상, 숫자, 특수문자 포함)"
                  value={newPassword}
                  onChange={(e) => {
                    const processedValue = removeKorean(
                      e.target.value.replace(/\s/g, "")
                    );
                    setNewPassword(processedValue);
                    setPasswordError("");
                  }}
                />
                <button
                  type="button"
                  className={styles.eyeBtn}
                  onClick={() => setShowNewPassword((v) => !v)}
                  tabIndex={-1}
                >
                  <EyeIcon visible={showNewPassword} />
                </button>
              </div>
            </div>
            {passwordError && (
              <div className={styles.errorText}>{passwordError}</div>
            )}
            <button
              className={styles.button}
              type="submit"
              disabled={isLoading}
            >
              {isLoading ? "변경 중..." : "비밀번호 재설정"}
            </button>
          </form>
        )}
        <div className={styles["bottom-text"]}>
          계정이 없으신가요?
          <Link to="/signup" className={styles.signup}>
            회원가입
          </Link>
        </div>
      </div>
    </div>
  );
};

export default FindPw;
