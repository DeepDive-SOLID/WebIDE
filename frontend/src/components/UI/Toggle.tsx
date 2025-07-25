import { IoMoon, IoSunny } from "react-icons/io5";
import styles from "../../styles/Toggle.module.scss";
import { useDispatch, useSelector } from "react-redux";
import { toggleTheme } from "../../stores/themeSlice";
import type { RootState } from "../../stores";

const Toggle = () => {
  const dispatch = useDispatch();
  const isDark = useSelector((state: RootState) => state.theme.isDark);

  return (
    <div className={styles.themeToggle}>
      <button
        className={`${styles.themeBtn} ${!isDark ? styles.active : ""}`}
        onClick={() => dispatch(toggleTheme())}
      >
        <IoSunny size={30} />
      </button>
      <button
        className={`${styles.themeBtn} ${isDark ? styles.active : ""}`}
        onClick={() => dispatch(toggleTheme())}
      >
        <IoMoon size={30} />
      </button>
    </div>
  );
};

export default Toggle;
