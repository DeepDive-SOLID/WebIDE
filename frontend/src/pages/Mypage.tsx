import React, { useState, useEffect, useContext } from "react";
import styles from "../styles/Mypage.module.scss";
import Sidebar from "../components/APP/Sidebar/MypageSidebar";
import { profile } from "../assets/";
import {
  getMemberDto,
  updateMemberDto,
  deleteMemberDto,
  checkEmail,
} from "../api/mypageApi";
import type { MypageDto } from "../types/mypage";
import { AuthContext } from "../contexts/AuthProvider";
import { useNavigate } from "react-router-dom";
import AppNav from "../components/APP/Nav/MypageNav";

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

const Mypage = () => {
  const authContext = useContext(AuthContext);
  const { userInfo, logout } = authContext || {};
  const memberId = userInfo?.memberId;
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    email: "",
    phone: "",
    password: "",
    birth: "",
    img: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [showFinalConfirm, setShowFinalConfirm] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedImageFile, setSelectedImageFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string>("");

  const [emailChecked, setEmailChecked] = useState(false);
  const [emailError, setEmailError] = useState("");
  const [emailSuccess, setEmailSuccess] = useState(false);
  const [isCheckingEmail, setIsCheckingEmail] = useState(false);
  const [originalEmail, setOriginalEmail] = useState("");

  const validatePhoneNumber = (phone: string): boolean =>
    /^010-\d{4}-\d{4}$/.test(phone);
  const validateEmail = (email: string): boolean =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  const validatePassword = (password: string): boolean =>
    /^(?=.*[0-9])(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z0-9!@#$%^&*(),.?":{}|<>]{8,}$/.test(
      password
    );
  const validateBirthDate = (birth: string): boolean =>
    new Date(birth) <= new Date();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const processedValue = ["email", "phone", "password"].includes(name)
      ? value.replace(/\s/g, "")
      : value;
    setForm((prev) => ({ ...prev, [name]: processedValue }));

    if (errors[name]) {
      const newErrors = { ...errors };
      delete newErrors[name];
      setErrors(newErrors);
    }

    if (name === "email") {
      setEmailChecked(false);
      setEmailError("");
      setEmailSuccess(false);
    }
  };

  useEffect(() => {
    if (!isEmailChanged() && form.email === originalEmail && originalEmail) {
      setEmailChecked(true);
      setEmailError("");
      setEmailSuccess(false);
    }
  }, [form.email, originalEmail]);

  const handleEmailCheck = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (!form.email) return;
    setIsCheckingEmail(true);
    setEmailError("");
    setEmailSuccess(false);

    try {
      const isDuplicate = await checkEmail(form.email);
      if (isDuplicate) {
        setEmailChecked(false);
        setEmailError("이미 등록된 이메일 입니다.");
        setEmailSuccess(false);
      } else {
        setEmailChecked(true);
        setEmailSuccess(true);
      }
    } catch {
      setEmailError("이메일 중복 확인 중 오류가 발생했습니다.");
    } finally {
      setIsCheckingEmail(false);
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm() || !memberId) return;

    try {
      const formData = new FormData();
      formData.append("memberId", memberId);
      formData.append("memberName", form.name);
      if (form.password.trim())
        formData.append("memberPassword", form.password);
      formData.append("memberEmail", form.email);
      formData.append("memberPhone", form.phone);
      formData.append("memberBirth", form.birth);
      if (selectedImageFile) formData.append("MemberImg", selectedImageFile);

      const result = await updateMemberDto(formData);
      alert(
        result === "SUCCESS"
          ? "회원정보가 성공적으로 저장되었습니다."
          : "회원정보 저장에 실패했습니다."
      );
      if (result === "SUCCESS") window.location.reload();
    } catch (err) {
      const error = err as { response: { data: string } };
      alert(
        typeof error.response?.data === "string"
          ? error.response.data
          : "회원정보 저장 중 오류가 발생했습니다."
      );
    }
  };

  const handleWithdraw = () => setShowWithdrawModal(true);
  const handleWithdrawConfirm = () => {
    setShowWithdrawModal(false);
    setShowFinalConfirm(true);
  };

  const handleFinalWithdraw = async () => {
    if (!memberId) return;
    try {
      const result = await deleteMemberDto(memberId);
      alert(
        result === "SUCCESS"
          ? "회원 탈퇴가 완료되었습니다."
          : "회원 탈퇴에 실패했습니다."
      );
      if (result === "SUCCESS" && logout) {
        logout();
        window.location.href = "/";
      }
    } catch {
      alert("회원 탈퇴 중 오류가 발생했습니다.");
    }
    setShowFinalConfirm(false);
  };

  const handleCancelWithdraw = () => {
    setShowWithdrawModal(false);
    setShowFinalConfirm(false);
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    if (file.size > 1 * 1024 * 1024)
      return alert("이미지 파일 크기는 1MB 이하여야 합니다.");
    if (file.name.length > 300) return alert("파일명은 300자 이하여야 합니다.");
    if (!file.type.startsWith("image/"))
      return alert("이미지 파일만 업로드 가능합니다.");

    setSelectedImageFile(file);
    const reader = new FileReader();
    reader.onload = (e) => setImagePreview(e.target?.result as string);
    reader.readAsDataURL(file);
  };

  useEffect(() => {
    const fetchMemberInfo = async () => {
      if (!memberId) {
        setIsLoading(false);
        return;
      }

      try {
        const memberInfo: MypageDto = await getMemberDto(memberId);
        setForm({
          name: memberInfo.memberName,
          email: memberInfo.memberEmail,
          phone: memberInfo.memberPhone,
          password: "",
          birth: memberInfo.memberBirth,
          img: memberInfo.memberImg,
        });
        setOriginalEmail(memberInfo.memberEmail);
      } catch {
        alert("회원 정보를 불러오는데 실패했습니다.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchMemberInfo();
  }, [memberId]);

  const validateForm = (): boolean => {
    const newErrors: { [key: string]: string } = {};
    if (!form.name.trim()) newErrors.name = "이름을 입력해주세요.";
    if (!form.email.trim()) newErrors.email = "이메일을 입력해주세요.";
    else if (!validateEmail(form.email))
      newErrors.email = "올바른 이메일 형식을 입력해주세요.";
    else if (isEmailChanged() && !emailChecked)
      newErrors.email = "이메일 중복확인을 해주세요.";

    if (!form.phone.trim()) newErrors.phone = "전화번호를 입력해주세요.";
    else if (!validatePhoneNumber(form.phone))
      newErrors.phone = "전화번호는 010-XXXX-XXXX 형식";

    if (form.password.trim() && !validatePassword(form.password))
      newErrors.password = "최소 8자 이상, 숫자와 특수문자 포함";

    if (!form.birth) newErrors.birth = "생년월일을 입력해주세요.";
    else if (!validateBirthDate(form.birth))
      newErrors.birth = "생년월일은 오늘 이후 날짜를 입력할 수 없습니다.";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const isEmailChanged = (): boolean => form.email !== originalEmail;

  return (
    <div className={styles.editProfilePage}>
      <AppNav />
      <div className={styles.mainContent}>
        <Sidebar />
        <div className={styles.profileFormContainer}>
          {isLoading ? (
            <div className={styles.loadingContainer}>
              <p>회원 정보를 불러오는 중...</p>
            </div>
          ) : (
            <form className={styles.profileForm} onSubmit={handleSave}>
              <div className={styles.profileSection}>
                <div className={styles.profileImageBox}>
                  <img
                    src={imagePreview || form.img || profile}
                    alt="프로필"
                    className={styles.profileImage}
                  />
                </div>
                <div className={styles.profileActions}>
                  <div className={styles.fileNotice}>
                    <p>
                      <strong>최대 첨부 파일 크기:</strong> 1MB
                    </p>
                    <p>
                      <strong>허용 확장자:</strong> jpg, png
                    </p>
                    <p>
                      <strong>파일명 길이 제한:</strong> 300자 이하
                    </p>
                  </div>
                  <div className={styles.fileInput}>
                    <input
                      id="imageInput"
                      type="file"
                      accept=".jpg,.jpeg,.png"
                      onChange={handleImageChange}
                    />
                  </div>
                </div>
              </div>
              <div className={styles.inputGroup}>
                <label>이름</label>
                <input
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="이름을 입력하세요"
                  className={errors.name ? styles.errorInput : ""}
                />
                {errors.name && (
                  <span className={styles.errorMessage}>{errors.name}</span>
                )}
              </div>
              <div className={styles.inputGroup}>
                <label>이메일</label>
                <div className={styles.emailInputWrapper}>
                  <input
                    name="email"
                    value={form.email}
                    onChange={handleChange}
                    placeholder="이메일을 입력하세요"
                    type="email"
                    className={`${errors.email ? styles.errorInput : ""} ${
                      isEmailChanged() ? styles.inputWithButton : ""
                    }`}
                  />
                  {isEmailChanged() && (
                    <button
                      type="button"
                      className={styles.emailCheckBtn}
                      onClick={handleEmailCheck}
                      disabled={!form.email || isCheckingEmail}
                    >
                      {isCheckingEmail ? "확인중..." : "중복확인"}
                    </button>
                  )}
                </div>
                {errors.email && (
                  <span className={styles.errorMessage}>{errors.email}</span>
                )}
                {emailError && (
                  <span className={styles.emailError}>{emailError}</span>
                )}
                {emailSuccess && (
                  <span className={styles.emailSuccess}>
                    사용 가능한 이메일 입니다.
                  </span>
                )}
              </div>
              <div className={styles.inputGroup}>
                <label>전화번호</label>
                <input
                  name="phone"
                  value={form.phone}
                  onChange={handleChange}
                  placeholder="010-XXXX-XXXX"
                  type="tel"
                  className={errors.phone ? styles.errorInput : ""}
                />
                {errors.phone && (
                  <span className={styles.errorMessage}>{errors.phone}</span>
                )}
              </div>
              <div className={styles.inputGroup}>
                <label>새 비밀번호</label>
                <div className={styles.passwordInputWrapper}>
                  <input
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                    placeholder="새 비밀번호를 입력하세요"
                    type={showPassword ? "text" : "password"}
                    className={errors.password ? styles.errorInput : ""}
                  />
                  <button
                    type="button"
                    className={styles.eyeBtn}
                    onClick={() => setShowPassword((v) => !v)}
                    tabIndex={-1}
                    aria-label={
                      showPassword ? "비밀번호 숨기기" : "비밀번호 보기"
                    }
                  >
                    <EyeIcon visible={showPassword} />
                  </button>
                </div>
                {errors.password && (
                  <span className={styles.errorMessage}>{errors.password}</span>
                )}
              </div>
              <div className={styles.inputGroup}>
                <label>생년월일</label>
                <input
                  name="birth"
                  value={form.birth}
                  onChange={handleChange}
                  placeholder="생년월일을 입력하세요"
                  type="date"
                  className={errors.birth ? styles.errorInput : ""}
                />
                {errors.birth && (
                  <span className={styles.errorMessage}>{errors.birth}</span>
                )}
              </div>
              <button
                className={styles.saveButton}
                type="submit"
                onClick={() => navigate("/info")}
              >
                저장하기
              </button>
              <button
                type="button"
                className={styles.withdrawButton}
                onClick={handleWithdraw}
              >
                회원 탈퇴
              </button>
            </form>
          )}
        </div>
      </div>

      {/* 회원 탈퇴 확인 모달 */}
      {showWithdrawModal && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <h3>회원 탈퇴 확인</h3>
            <p>정말로 회원 탈퇴를 하시겠습니까?</p>
            <p>
              탈퇴 시{" "}
              <span className={styles.warningText}>모든 개인정보가 삭제</span>
              되며,{" "}
              <span className={styles.warningText}>복구할 수 없습니다.</span>
            </p>
            <div className={styles.modalButtons}>
              <button
                onClick={handleWithdrawConfirm}
                className={styles.confirmButton}
              >
                탈퇴하기
              </button>
              <button
                onClick={handleCancelWithdraw}
                className={styles.cancelButton}
              >
                취소
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 최종 확인 모달 */}
      {showFinalConfirm && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <h3>최종 확인</h3>
            <p>회원 탈퇴를 진행하시겠습니까?</p>
            <p>이 작업은 되돌릴 수 없습니다.</p>
            <div className={styles.modalButtons}>
              <button
                onClick={handleFinalWithdraw}
                className={styles.confirmButton}
              >
                탈퇴 완료
              </button>
              <button
                onClick={handleCancelWithdraw}
                className={styles.cancelButton}
              >
                취소
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Mypage;
