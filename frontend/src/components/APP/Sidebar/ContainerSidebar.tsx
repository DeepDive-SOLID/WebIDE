import Box from "../../UI/Box";
import { LuBox } from "react-icons/lu";
import styles from "../../../styles/AppSidebar.module.scss";

const ContainerSidebar = () => {
  return (
    <div className={styles.section}>
      <h2 className={styles.heading}>Container</h2>
      <div className={styles.boxList}>
        <Box
          icon={<LuBox size={20} />}
          title="내 컨테이너"
          onClick={() => {}}
          cnt={10}
          className={styles.boxPrimary}
        />
        <Box
          icon={<LuBox size={20} />}
          title="공유받은 컨테이너"
          onClick={() => {}}
          cnt={5}
          className={styles.boxPrimary}
        />
      </div>
    </div>
  );
};

export default ContainerSidebar;
