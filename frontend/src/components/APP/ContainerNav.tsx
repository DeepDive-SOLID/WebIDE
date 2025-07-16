import { useState } from "react";
import { IoHomeOutline } from "react-icons/io5";
import { PiListMagnifyingGlass } from "react-icons/pi";
import { profile } from "../../assets";
import Toggle from "../UI/Toggle";
import Button from "../UI/Button";
import { useNavigate } from "react-router-dom";
import AppSidebar from "./AppSidebar";
import "../../styles/AppNav.scss";

const ContainerNav = () => {
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleNavClick = (target: "home" | "algorithm" | "mypage") => {
    switch (target) {
      case "home":
        navigate("/");
        setSidebarOpen(false);
        break;
      case "algorithm":
        setSidebarOpen((prev) => !prev);
        break;
      case "mypage":
        navigate("/mypage");
        setSidebarOpen(false);
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
            icon={<PiListMagnifyingGlass size={30} />}
            onClick={() => handleNavClick("algorithm")}
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

      <AppSidebar isOpen={sidebarOpen} type="algorithm" />
    </>
  );
};

export default ContainerNav;
