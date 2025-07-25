import styles from "../../styles/MypageNav.module.scss";
import { FiUser, FiLogOut } from "react-icons/fi";
import { useEffect, useState } from "react";
import { getProfileDto } from "../../api/mypageApi";
import type { MypageProfileDto } from "../../types/mypage";
import profileImg from "../../assets/profile.svg";
import { useContext } from "react";
import { AuthContext } from "../../contexts/AuthContext";
import { useNavigate } from "react-router-dom";

const MypageNav = () => {
  const authContext = useContext(AuthContext);

  const navigate = useNavigate();

  const [memberProfile, setMemberProfile] = useState<MypageProfileDto | null>(
    null
  );

  const currentMemberId = authContext?.userInfo?.memberId;
  const isAuthLoading = authContext?.isLoading;

  useEffect(() => {
    if (isAuthLoading || !currentMemberId) return;

    const fetchProfile = async () => {
      try {
        const profile = await getProfileDto(currentMemberId);

        setMemberProfile(profile);
      } catch (err) {
        console.error("프로필 정보를 불러오는 데 실패했습니다:", err);
      }
    };

    fetchProfile();
  }, [currentMemberId, isAuthLoading]);

  return (
    <aside className={styles.sidebar}>
      <div className={styles.profileBox}>
        <img
          src={memberProfile?.memberImg || profileImg}
          alt="프로필"
          className={styles.profileImg}
        />
        <div className={styles.greeting}>
          <span className={styles.userName}>
            {memberProfile?.memberName || "사용자"}
          </span>{" "}
          님, 반갑습니다
        </div>
      </div>

      <ul className={styles.menuList}>
        <li className={styles.item} onClick={() => navigate("/mypage")}>
          <FiUser />
          <span>회원 정보 수정</span>
        </li>
        <li
          className={styles.item}
          onClick={() => {
            authContext?.logout?.();
          }}
        >
          <FiLogOut />
          <span>로그아웃</span>
        </li>
      </ul>
    </aside>
  );
};

export default MypageNav;
