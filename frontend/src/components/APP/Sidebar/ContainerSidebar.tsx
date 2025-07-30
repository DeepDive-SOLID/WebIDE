import { useEffect, useState } from "react";
import Box from "../../UI/Box";
import { LuBox } from "react-icons/lu";
import styles from "../../../styles/AppSidebar.module.scss";
import { useNavigate } from "react-router-dom";
import { getMyContainers, getSharedContainers } from "../../../api/home";

const ContainerSidebar = () => {
  const navigate = useNavigate();

  // 컨테이너 개수 상태
  const [myCount, setMyCount] = useState<number>(0);
  const [sharedCount, setSharedCount] = useState<number>(0);

  useEffect(() => {
    // API 호출해서 개수 가져오기
    const fetchCounts = async () => {
      try {
        // 두 API를 병렬 호출
        const [myContainers, sharedContainers] = await Promise.all([
          getMyContainers(),
          getSharedContainers(),
        ]);
        setMyCount(myContainers.length);
        setSharedCount(sharedContainers.length);
      } catch (error) {
        console.error("컨테이너 개수 로드 실패", error);
      }
    };

    fetchCounts();
  }, []);

  return (
    <div className={styles.section}>
      <h2 className={styles.heading}>Container</h2>
      <div className={styles.boxList}>
        <Box
          icon={<LuBox size={20} />}
          title="내 컨테이너"
          onClick={() => navigate("/home/my-container")}
          cnt={myCount}
          className={styles.boxPrimary}
        />
        <Box
          icon={<LuBox size={20} />}
          title="공유받은 컨테이너"
          onClick={() => navigate("/home/shared-container")}
          cnt={sharedCount}
          className={styles.boxPrimary}
        />
      </div>
    </div>
  );
};

export default ContainerSidebar;
