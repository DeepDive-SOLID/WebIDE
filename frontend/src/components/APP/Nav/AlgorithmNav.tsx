import { IoHomeOutline } from "react-icons/io5";
import { PiListMagnifyingGlass } from "react-icons/pi";
import { profile } from "../../../assets";
import Toggle from "../../UI/Toggle";
import Button from "../../UI/Button";
import { useEffect, useState, useContext } from "react";
import { getProfileDto } from "../../../api/mypageApi";
import { AuthContext } from "../../../contexts/AuthContext";
import { useNavigate } from "react-router-dom";
import AppSidebar from "../Sidebar/AppSidebar";
import styles from "../../../styles/AppNav.module.scss";

interface AlgorithmNavProps {
  containerId: number;
  modal: boolean;
  setModal: (modal: boolean) => void;
}

const AlgorithmNav = ({ containerId, modal, setModal }: AlgorithmNavProps) => {
  const navigate = useNavigate();
  // const [sidebarOpen, setSidebarOpen] = useState(false);
  const authContext = useContext(AuthContext);
  const [profileImg, setProfileImg] = useState<string>(profile);
  const memberId = authContext?.userInfo?.memberId;
  const isAuthLoading = authContext?.isLoading;

  useEffect(() => {
    if (isAuthLoading || !memberId) return;

    const fetchProfile = async () => {
      try {
        const res = await getProfileDto(memberId);
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

  const handleNavClick = (target: "home" | "algorithm" | "mypage") => {
    switch (target) {
      case "home":
        navigate("/home/all-container");
        setModal(false);
        break;
      case "algorithm":
        setModal(!modal);
        break;
      case "mypage":
        navigate("/info");
        setModal(false);
        break;
    }
  };

  return (
    <>
      <aside className={styles.appNav}>
        <div className={styles.appNavTop}>
          <Button
            icon={<IoHomeOutline size={30} />}
            onClick={() => handleNavClick("home")}
            className={styles.navBtn}
          />
          <Button
            icon={<PiListMagnifyingGlass size={30} />}
            onClick={() => handleNavClick("algorithm")}
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

      <AppSidebar isOpen={modal} type="algorithm" containerId={containerId} />
    </>
  );
};

export default AlgorithmNav;
