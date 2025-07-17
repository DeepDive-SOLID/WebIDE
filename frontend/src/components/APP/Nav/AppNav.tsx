import { useState } from "react";
import { IoHomeOutline, IoFolderOpenOutline } from "react-icons/io5";
import { AiOutlineGlobal } from "react-icons/ai";
import "../../../styles/AppNav.scss";
import { profile } from "../../../assets";
import Toggle from "../../UI/Toggle";
import { useNavigate } from "react-router-dom";
import Button from "../../UI/Button";
import AppSidebar from "../Sidebar/AppSidebar";

const AppNav = () => {
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [sidebarType, setSidebarType] = useState<
    "container" | "algorithm" | null
  >(null);

  const handleNavClick = (
    target: "home" | "container" | "global" | "mypage"
  ) => {
    switch (target) {
      case "home":
        navigate("/");
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
        setSidebarOpen(false);
        setSidebarType(null);
        break;
      case "mypage":
        navigate("/mypage");
        setSidebarOpen(false);
        setSidebarType(null);
        break;
    }
  };

  return (
    <>
      <aside className="app-nav">
        <div className="app-nav__top">
          <Button
            icon={<IoHomeOutline size={30} />}
            onClick={() => handleNavClick("home")}
            className="nav-btn"
          />
          <Button
            icon={<IoFolderOpenOutline size={30} />}
            onClick={() => handleNavClick("container")}
            className="nav-btn"
          />
          <Button
            icon={<AiOutlineGlobal size={30} />}
            onClick={() => handleNavClick("global")}
            className="nav-btn"
          />
        </div>

        <div className="app-nav__bottom">
          <div className="theme-toggle-wrapper">
            <Toggle />
          </div>
          <Button
            icon={<img src={profile} alt="profile" />}
            onClick={() => handleNavClick("mypage")}
            className="profile-btn"
          />
        </div>
      </aside>

      <AppSidebar isOpen={sidebarOpen} type={sidebarType} />
    </>
  );
};

export default AppNav;
