import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import styles from "../styles/Signup.module.scss";
import logoImg from "../assets/CodeWith.png";
import { signApi } from "../api/signApi";

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

function isValidPassword(password: string) {
  return /^(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/.test(
    password
  );
}
function isValidPhoneNumber(phone: string) {
  const phoneRegex = /^010-\d{4}-\d{4}$/;
  return phoneRegex.test(phone);
}
function isValidBirthDate(birth: string) {
  const selectedDate = new Date(birth);
  const today = new Date();
  today.setHours(23, 59, 59, 999);
  return selectedDate <= today;
}
function removeKorean(text: string) {
  return text.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, "");
}

const Signup: React.FC = () => {
  const [formData, setFormData] = useState({
    memberName: "",
    memberId: "",
    memberPw: "",
    memberEmail: "",
    memberPhone: "",
    memberBirth: "",
    authId: "USER",
  });
  const [agree, setAgree] = useState(false);
  const [idChecked, setIdChecked] = useState(false);
  const [idError, setIdError] = useState("");
  const [idSuccess, setIdSuccess] = useState(false);
  const [emailChecked, setEmailChecked] = useState(false);
  const [emailError, setEmailError] = useState("");
  const [emailSuccess, setEmailSuccess] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isCheckingId, setIsCheckingId] = useState(false);
  const [isCheckingEmail, setIsCheckingEmail] = useState(false);
  const [submitError, setSubmitError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({
    memberName: "",
    memberId: "",
    memberPw: "",
    memberEmail: "",
    memberPhone: "",
    memberBirth: "",
  });
  const passwordValid = isValidPassword(formData.memberPw);
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const processedValue =
      name === "memberId" ||
      name === "memberPw" ||
      name === "memberEmail" ||
      name === "memberPhone"
        ? removeKorean(value.replace(/\s/g, ""))
        : value;
    setFormData((prev) => ({ ...prev, [name]: processedValue }));
    setFieldErrors((prev) => ({ ...prev, [name]: "" }));
    if (name === "memberId") {
      setIdChecked(false);
      setIdError("");
      setIdSuccess(false);
    }
    if (name === "memberEmail") {
      setEmailChecked(false);
      setEmailError("");
      setEmailSuccess(false);
    }
  };
  // 아이디 중복 확인
  const handleIdCheck = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (!formData.memberId) return;
    setIsCheckingId(true);
    try {
      const isIdExists = await signApi.checkId({
        memberId: formData.memberId,
      });
      if (isIdExists) {
        setIdChecked(false);
        setIdError("이미 등록된 아이디 입니다.");
        setIdSuccess(false);
      } else {
        setIdChecked(true);
        setIdError("");
        setIdSuccess(true);
        setFieldErrors((prev) => ({ ...prev, memberId: "" }));
      }
    } catch {
      setIdChecked(false);
      setIdError("ID 중복확인 중 오류가 발생했습니다.");
      setIdSuccess(false);
    } finally {
      setIsCheckingId(false);
    }
  };
  // 이메일 중복 확인
  const handleEmailCheck = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (!formData.memberEmail) return;
    setIsCheckingEmail(true);
    try {
      const isEmailExists = await signApi.checkEmail({
        memberEmail: formData.memberEmail,
      });
      if (isEmailExists) {
        setEmailChecked(false);
        setEmailError("이미 등록된 이메일 입니다.");
        setEmailSuccess(false);
      } else {
        setEmailChecked(true);
        setEmailError("");
        setEmailSuccess(true);
        setFieldErrors((prev) => ({ ...prev, memberEmail: "" }));
      }
    } catch {
      setEmailChecked(false);
      setEmailError("이메일 중복확인 중 오류가 발생했습니다.");
      setEmailSuccess(false);
    } finally {
      setIsCheckingEmail(false);
    }
  };
  const validateForm = () => {
    const errors = {
      memberName: "",
      memberId: "",
      memberPw: "",
      memberEmail: "",
      memberPhone: "",
      memberBirth: "",
    };
    if (!formData.memberName.trim()) {
      errors.memberName = "이름을 입력해주세요";
    }
    if (!formData.memberId.trim()) {
      errors.memberId = "아이디를 입력해주세요";
    } else if (!idChecked) {
      errors.memberId = "아이디 중복확인을 해주세요";
    }
    if (!formData.memberPw) {
      errors.memberPw = "비밀번호를 입력해주세요";
    } else if (!passwordValid) {
      errors.memberPw =
        "비밀번호는 최소 8자 이상, 숫자와 특수문자를 포함해야 합니다";
    }
    if (!formData.memberEmail.trim()) {
      errors.memberEmail = "이메일을 입력해주세요";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.memberEmail)) {
      errors.memberEmail = "올바른 이메일 형식을 입력해주세요";
    } else if (!emailChecked) {
      errors.memberEmail = "이메일 중복확인을 해주세요";
    }
    if (!formData.memberPhone.trim()) {
      errors.memberPhone = "전화번호를 입력해주세요";
    } else if (!isValidPhoneNumber(formData.memberPhone)) {
      errors.memberPhone =
        "올바른 전화번호 형식을 입력해주세요 (010-XXXX-XXXX)";
    }
    if (!formData.memberBirth) {
      errors.memberBirth = "생년월일을 선택해주세요";
    } else if (!isValidBirthDate(formData.memberBirth)) {
      errors.memberBirth = "올바른 생년월일을 선택해주세요";
    }
    setFieldErrors(errors);
    return !Object.values(errors).some((error) => error !== "");
  };
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;
    if (!agree) {
      alert("이용약관에 동의해주세요.");
      return;
    }
    setIsLoading(true);
    setSubmitError("");
    try {
      const result = await signApi.signUp({
        memberId: formData.memberId,
        memberName: formData.memberName,
        memberPw: formData.memberPw,
        memberEmail: formData.memberEmail,
        memberPhone: formData.memberPhone,
        memberBirth: formData.memberBirth,
      });
      if (result === "SUCCESS") {
        alert("회원가입이 완료되었습니다!");
        window.location.href = "/";
      } else {
        setSubmitError("회원가입에 실패했습니다. 다시 시도해주세요.");
      }
    } catch {
      setSubmitError("회원가입 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };
  const navigate = useNavigate();
  return (
    <div className={styles.pageWrapper}>
      <div className={styles.signUpContainer}>
        <img
          src={logoImg}
          alt="로고"
          className={styles.logo}
          onClick={() => navigate("/")}
        />
        <h1 className={styles.title}>계정을 만들어 시작하세요</h1>
        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.inputGroup}>
            <label className={styles.label}>이름</label>
            <input
              className={`${styles.input} ${
                fieldErrors.memberName ? styles.inputError : ""
              }`}
              type="text"
              name="memberName"
              placeholder="이름을 입력해주세요"
              value={formData.memberName}
              onChange={handleChange}
              required
            />
            {fieldErrors.memberName && (
              <div className={styles.fieldError}>{fieldErrors.memberName}</div>
            )}
          </div>
          <div className={styles.inputGroup}>
            <label className={styles.label}>ID</label>
            <div className={styles.idInputWrapper}>
              <input
                className={`${styles.input} ${styles.inputWithButton} ${
                  fieldErrors.memberId ? styles.inputError : ""
                }`}
                type="text"
                name="memberId"
                placeholder="ID를 입력해주세요"
                value={formData.memberId}
                onChange={handleChange}
                required
              />
              <button
                type="button"
                className={styles.idCheckBtn}
                onClick={handleIdCheck}
                disabled={!formData.memberId || isCheckingId}
              >
                {isCheckingId ? "확인중..." : "중복확인"}
              </button>
            </div>
            {fieldErrors.memberId && !idError && (
              <div className={styles.fieldError}>{fieldErrors.memberId}</div>
            )}
            {idError && <div className={styles.idError}>{idError}</div>}
            {idSuccess && (
              <div className={styles.idSuccess}>사용 가능한 아이디 입니다.</div>
            )}
          </div>
          <div className={styles.inputGroup}>
            <label className={styles.label}>이메일</label>
            <div className={styles.emailInputWrapper}>
              <input
                className={`${styles.input} ${styles.inputWithButton} ${
                  fieldErrors.memberEmail ? styles.inputError : ""
                }`}
                type="email"
                name="memberEmail"
                placeholder="이메일을 입력해주세요"
                value={formData.memberEmail}
                onChange={handleChange}
                required
              />
              <button
                type="button"
                className={styles.emailCheckBtn}
                onClick={handleEmailCheck}
                disabled={!formData.memberEmail || isCheckingEmail}
              >
                {isCheckingEmail ? "확인중..." : "중복확인"}
              </button>
            </div>
            {fieldErrors.memberEmail && !emailError && (
              <div className={styles.fieldError}>{fieldErrors.memberEmail}</div>
            )}
            {emailError && (
              <div className={styles.emailError}>{emailError}</div>
            )}
            {emailSuccess && (
              <div className={styles.emailSuccess}>
                사용 가능한 이메일 입니다.
              </div>
            )}
          </div>
          <div className={styles.inputGroup}>
            <label className={styles.label}>비밀번호</label>
            <div className={styles.passwordInputWrapper}>
              <input
                className={`${styles.input} ${
                  fieldErrors.memberPw ? styles.inputError : ""
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
            {fieldErrors.memberPw && (
              <div className={styles.fieldError}>{fieldErrors.memberPw}</div>
            )}
            {!passwordValid && formData.memberPw && !fieldErrors.memberPw && (
              <div className={styles.passwordError}>
                최소 8자 이상, 숫자와 특수문자 포함
              </div>
            )}
          </div>
          <div className={styles.inputGroup}>
            <label className={styles.label}>전화번호</label>
            <input
              className={`${styles.input} ${
                fieldErrors.memberPhone ? styles.inputError : ""
              }`}
              type="tel"
              name="memberPhone"
              placeholder="010-XXXX-XXXX"
              value={formData.memberPhone}
              onChange={handleChange}
              required
            />
            {fieldErrors.memberPhone && (
              <div className={styles.fieldError}>{fieldErrors.memberPhone}</div>
            )}
          </div>
          <div className={styles.inputGroup}>
            <label className={styles.label}>생년월일</label>
            <input
              className={`${styles.input} ${
                fieldErrors.memberBirth ? styles.inputError : ""
              }`}
              type="date"
              name="memberBirth"
              value={formData.memberBirth}
              onChange={handleChange}
              required
            />
            {fieldErrors.memberBirth && (
              <div className={styles.fieldError}>{fieldErrors.memberBirth}</div>
            )}
          </div>
          <div className={`${styles.inputGroup} ${styles.agreeGroup}`}>
            <label className={styles.agreeLabel}>
              <input
                type="checkbox"
                checked={agree}
                onChange={(e) => setAgree(e.target.checked)}
                required
              />
              이용약관과 개인정보 처리방침에 동의합니다
            </label>
          </div>
          {submitError && (
            <div className={styles.submitError}>{submitError}</div>
          )}
          <button className={styles.button} type="submit" disabled={isLoading}>
            {isLoading ? "가입중..." : "가입하기"}
          </button>
        </form>
        <div className={styles.loginLink}>
          이미 계정이 있으신가요?
          <Link to="/">로그인</Link>
        </div>
      </div>
    </div>
  );
};

export default Signup;
