import React, { useState } from "react";
import styles from "../styles/Find.module.scss";
import { Link, useNavigate } from "react-router-dom";
import logoImg from "../assets/images/CodeWith.png";
import backIcon from "../assets/icons/back.svg";
import { signApi } from "../api/signApi";

function removeKorean(text: string) {
  return text.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, "");
}

const FindId: React.FC = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [foundId, setFoundId] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [tab, setTab] = useState(0);

  const handleTab = (idx: number) => {
    setTab(idx);
    if (idx === 1) {
      navigate("/find-pw");
    }
  };

  const handleFindId = async () => {
    if (!email.trim()) {
      setError("이메일을 입력해주세요.");
      return;
    }
    setIsLoading(true);
    setFoundId("");
    setError("");
    try {
      const memberId = await signApi.findId({ memberEmail: email });
      setFoundId(memberId);
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
          setError("일치하는 사용자가 없습니다.");
        } else {
          setError("아이디 찾기 중 오류가 발생했습니다.");
        }
      } else {
        setError("아이디 찾기 중 오류가 발생했습니다.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const processedValue = removeKorean(e.target.value.replace(/\s/g, ""));
    setEmail(processedValue);
    setFoundId("");
    setError("");
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
        <form
          className={styles.form}
          onSubmit={(e) => {
            e.preventDefault();
            handleFindId();
          }}
        >
          <div className={styles.inputGroup}>
            <label htmlFor="email">이메일</label>
            <input
              id="email"
              type="email"
              placeholder="이메일을 입력하세요"
              value={email}
              onChange={handleEmailChange}
              className={error ? styles.inputError : ""}
            />
            {error && <div className={styles.errorText}>{error}</div>}
          </div>
          {foundId && (
            <div className={styles.inputGroup}>
              <label htmlFor="id">ID</label>
              <div className={styles["id-box"]}>{foundId}</div>
            </div>
          )}
          <button
            className={styles.button}
            type={foundId ? "button" : "submit"}
            disabled={isLoading}
            onClick={foundId ? () => navigate("/") : undefined}
          >
            {isLoading
              ? "확인 중..."
              : foundId
              ? "로그인 하러 가기"
              : "아이디 찾기"}
          </button>
        </form>
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

export default FindId;
