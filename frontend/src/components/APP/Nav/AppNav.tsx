import { IoHomeOutline, IoFolderOpenOutline } from "react-icons/io5";
import { AiOutlineGlobal } from "react-icons/ai";
import { useEffect, useState, useContext } from "react";
import { getProfileDto } from "../../../api/mypageApi";
import { AuthContext } from "../../../contexts/AuthContext";
import { profile } from "../../../assets";
import Toggle from "../../UI/Toggle";
import { useNavigate } from "react-router-dom";
import Button from "../../UI/Button";
import AppSidebar from "../Sidebar/AppSidebar";
import styles from "../../../styles/AppNav.module.scss";

interface AppNavProps {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
  sidebarType: "container" | "algorithm" | null;
  setSidebarType: (type: "container" | "algorithm" | null) => void;
}

const AppNav = ({
  sidebarOpen,
  setSidebarOpen,
  sidebarType,
  setSidebarType,
}: AppNavProps) => {
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

  const navigate = useNavigate();

  const handleNavClick = (
    target: "home" | "container" | "global" | "mypage"
  ) => {
    switch (target) {
      case "home":
        navigate("/home/all-container");
        setSidebarOpen(false);
        setSidebarType(null);
        break;
      case "container":
        if (sidebarOpen && sidebarType === "container") {
          setSidebarOpen(false);
          setSidebarType(null);
        } else {
          setSidebarOpen(true);
          setSidebarType("container");
        }
        break;
      case "global":
        navigate("/home/public-container");
        setSidebarOpen(false);
        setSidebarType(null);
        break;
      case "mypage":
        navigate("/info");
        setSidebarOpen(false);
        setSidebarType(null);
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
            icon={<IoFolderOpenOutline size={30} />}
            onClick={() => handleNavClick("container")}
            className={styles.navBtn}
          />
          <Button
            icon={<AiOutlineGlobal size={30} />}
            onClick={() => handleNavClick("global")}
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

      <AppSidebar isOpen={sidebarOpen} type={sidebarType} />
    </>
  );
};

export default AppNav;
