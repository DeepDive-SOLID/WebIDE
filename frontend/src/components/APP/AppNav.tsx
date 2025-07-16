import { IoHomeOutline, IoFolderOpenOutline } from "react-icons/io5";
import { AiOutlineGlobal } from "react-icons/ai";
import "../../styles/AppNav.scss";
import { profile } from "../../assets";
import Toggle from "../UI/Toggle";
import { useNavigate } from "react-router-dom";
import Button from "../UI/Button";

const AppNav = () => {
  const navigate = useNavigate();
  return (
    <aside className="app-nav">
      <div className="app-nav__top">
        <Button
          icon={<IoHomeOutline size={30} />}
          onClick={() => navigate("/")}
          className="nav-btn"
        />
        <Button
          icon={<IoFolderOpenOutline size={30} />}
          onClick={() => navigate("/container")}
          className="nav-btn"
        />
        <Button
          icon={<AiOutlineGlobal size={30} />}
          onClick={() => navigate("/global")}
          className="nav-btn"
        />
      </div>

      <div className="app-nav__bottom">
        <div className="theme-toggle-wrapper">
          <Toggle />
        </div>
        <Button
          icon={<img src={profile} alt="profile" />}
          onClick={() => navigate("/mypage")}
          className="profile-btn"
        />
      </div>
    </aside>
  );
};

export default AppNav;
