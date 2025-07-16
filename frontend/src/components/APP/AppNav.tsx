import { IoHomeOutline, IoFolderOpenOutline } from "react-icons/io5";
import { AiOutlineGlobal } from "react-icons/ai";
import "../../styles/AppNav.scss";
import { profile } from "../../assets";
import Toggle from "../UI/Toggle";
import { useNavigate } from "react-router-dom";

const AppNav = () => {
  const navigate = useNavigate();
  return (
    <aside className="app-nav">
      <div className="app-nav__top">
        <button className="nav-btn" onClick={() => navigate("/")}>
          <IoHomeOutline size={30} />
        </button>
        <button className="nav-btn">
          <IoFolderOpenOutline
            size={30}
            onClick={() => navigate("/container")}
          />
        </button>
        <button className="nav-btn">
          <AiOutlineGlobal size={30} onClick={() => navigate("/global")} />
        </button>
      </div>

      <div className="app-nav__bottom">
        <div className="theme-toggle-wrapper">
          <Toggle />
        </div>

        <button className="mascot-btn" onClick={() => navigate("/mypage")}>
          <img src={profile} alt="profile" />
        </button>
      </div>
    </aside>
  );
};

export default AppNav;
