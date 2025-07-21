import styles from "../../../styles/AppSidebar.module.scss";
import ContainerSidebar from "./ContainerSidebar";
import AlgorithmSidebar from "./AlgorithmSidebar";

export interface AppSidebarProps {
  isOpen: boolean;
  type: "container" | "algorithm" | null;
}

const AppSidebar = ({ isOpen, type }: AppSidebarProps) => {
  if (!isOpen || !type) return null;

  return (
    <div className={styles.appSidebar}>
      {type === "container" && <ContainerSidebar />}
      {type === "algorithm" && <AlgorithmSidebar />}
    </div>
  );
};

export default AppSidebar;
