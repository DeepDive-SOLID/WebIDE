import { useEffect, useState, useContext } from "react";
import { IoHomeOutline } from "react-icons/io5";
import Toggle from "../../UI/Toggle";
import Button from "../../UI/Button";
import styles from "../../../styles/AppNav.module.scss";
import { useNavigate } from "react-router-dom";
import { getProfileDto } from "../../../api/mypageApi";
import { AuthContext } from "../../../contexts/AuthContext";
import { profile } from "../../../assets";
import type { MypageProfileDto } from "../../../types/mypage";

const MypageNav = () => {
  const navigate = useNavigate();
  const authContext = useContext(AuthContext);

  const [profileImg, setProfileImg] = useState<string>(profile);
  const memberId = authContext?.userInfo?.memberId;
  const isAuthLoading = authContext?.isLoading;

  useEffect(() => {
    if (isAuthLoading || !memberId) return;

    const fetchProfile = async () => {
      try {
        const res: MypageProfileDto = await getProfileDto(memberId);
        if (res?.memberImg) {
          setProfileImg(res.memberImg);
        }
      } catch (err) {
        console.error("프로필 이미지 로딩 실패:", err);
        setProfileImg(profile);
      }
    };

    fetchProfile();
  }, [memberId, isAuthLoading]);

  const handleNavClick = (target: "home" | "mypage") => {
    navigate(`/${target}`);
  };

  return (
    <aside className={styles.appNav}>
      <div className={styles.appNavTop}>
        <Button
          icon={<IoHomeOutline size={30} />}
          onClick={() => handleNavClick("home")}
          className={styles.navBtn}
        />
      </div>

      <div className={styles.appNavBottom}>
        <div className={styles.themeToggleWrapper}>
          <Toggle />
        </div>
        <Button
          icon={<img src={profileImg} alt="profile" />}
          onClick={() => handleNavClick("mypage")}
          className={styles.profileBtn}
        />
      </div>
    </aside>
  );
};

export default MypageNav;
