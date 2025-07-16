import { IoMoon, IoSunny } from "react-icons/io5";
import "../../styles/Toggle.scss";
import { useDispatch, useSelector } from "react-redux";
import { toggleTheme } from "../../stores/themeSlice";
import type { RootState } from "../../stores";

const Toggle = () => {
  const dispatch = useDispatch();
  const isDark = useSelector((state: RootState) => state.theme.isDark);

  return (
    <div className="theme-toggle">
      <button
        className={`theme-btn ${!isDark ? "active" : ""}`}
        onClick={() => dispatch(toggleTheme())}
      >
        <IoSunny size={30} />
      </button>
      <button
        className={`theme-btn ${isDark ? "active" : ""}`}
        onClick={() => dispatch(toggleTheme())}
      >
        <IoMoon size={30} />
      </button>
    </div>
  );
};

export default Toggle;
