import { useState } from "react";
import { IoHomeOutline } from "react-icons/io5";
import { PiListMagnifyingGlass } from "react-icons/pi";
import { profile } from "../../../assets";
import Toggle from "../../UI/Toggle";
import Button from "../../UI/Button";
import { useNavigate } from "react-router-dom";
import AppSidebar from "../Sidebar/AppSidebar";
import styles from "../../../styles/AppNav.module.scss";

interface AlgorithmNavProps {
  containerId: number;
}

const AlgorithmNav = ({ containerId }: AlgorithmNavProps) => {
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleNavClick = (target: "home" | "algorithm" | "mypage") => {
    switch (target) {
      case "home":
        navigate("/home/all-container");
        setSidebarOpen(false);
        break;
      case "algorithm":
        setSidebarOpen((prev) => !prev);
        break;
      case "mypage":
        navigate("/info");
        setSidebarOpen(false);
        break;
    }
  };

  return (
    <>
      <aside className={styles.appNav}>
        <div className={styles.appNavTop}>
          <Button icon={<IoHomeOutline size={30} />} onClick={() => handleNavClick("home")} className={styles.navBtn} />
          <Button icon={<PiListMagnifyingGlass size={30} />} onClick={() => handleNavClick("algorithm")} className={styles.navBtn} />
        </div>

        <div className={styles.appNavBottom}>
          <div className={styles.themeToggleWrapper}>
            <Toggle />
          </div>
          <Button icon={<img src={profile} alt='profile' />} onClick={() => handleNavClick("mypage")} className={styles.profileBtn} />
        </div>
      </aside>

      <AppSidebar isOpen={sidebarOpen} type='algorithm' containerId={containerId} />
    </>
  );
};

export default AlgorithmNav;
