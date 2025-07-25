import { useEffect, useState, useContext } from "react";
import styles from "../styles/Mypage.module.scss";
import Sidebar from "../components/APP/MypageNav";
import profileImg from "../assets/profile.svg";
import { getMemberDto } from "../api/mypageApi";
import type { MypageDto } from "../types/mypage";
import { AuthContext } from "../contexts/AuthContext";
import { Link } from "react-router-dom";

const Info = () => {
  const { userInfo } = useContext(AuthContext) || {};
  const memberId = userInfo?.memberId;

  const [memberInfo, setMemberInfo] = useState<MypageDto | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchMember = async () => {
      if (!memberId) {
        setIsLoading(false);
        return;
      }
      try {
        const data = await getMemberDto(memberId);
        setMemberInfo(data);
      } catch (error) {
        alert("회원 정보를 불러오는 데 실패했습니다.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchMember();
  }, [memberId]);

  return (
    <div className={styles.editProfilePage}>
      <div className={styles.mainContent}>
        <Sidebar />
        <div className={styles.profileFormContainer}>
          {isLoading || !memberInfo ? (
            <div className={styles.loadingContainer}>
              <p>회원 정보를 불러오는 중...</p>
            </div>
          ) : (
            <div className={styles.profileForm}>
              <div className={styles.profileSection}>
                <div className={styles.profileImageBox}>
                  <img
                    src={memberInfo.memberImg || profileImg}
                    alt="프로필"
                    className={styles.profileImage}
                  />
                </div>
              </div>
              <div className={styles.inputGroup}>
                <label>이름</label>
                <div className={styles.readOnlyValue}>
                  {memberInfo.memberName}
                </div>
              </div>
              <div className={styles.inputGroup}>
                <label>이메일</label>
                <div className={styles.readOnlyValue}>
                  {memberInfo.memberEmail}
                </div>
              </div>
              <div className={styles.inputGroup}>
                <label>전화번호</label>
                <div className={styles.readOnlyValue}>
                  {memberInfo.memberPhone}
                </div>
              </div>
              <div className={styles.inputGroup}>
                <label>생년월일</label>
                <div className={styles.readOnlyValue}>
                  {memberInfo.memberBirth}
                </div>
              </div>

              <Link to="/mypage/edit-profile" className={styles.saveButton}>
                회원정보 수정
              </Link>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Info;
