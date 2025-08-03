import styles from "../../../styles/AppSidebar.module.scss";
import ContainerSidebar from "./ContainerSidebar";
import AlgorithmSidebar from "./AlgorithmSidebar";

export interface AppSidebarProps {
  isOpen: boolean;
  type: "container" | "algorithm" | null;
  containerId?: number;
}

const AppSidebar = ({ isOpen, type, containerId }: AppSidebarProps) => {
  if (!isOpen || !type) return null;

  return (
    <div className={styles.appSidebar}>
      {type === "container" && <ContainerSidebar />}
      {type === "algorithm" && containerId !== undefined && (
          <AlgorithmSidebar
              containerId={containerId}
              onSelectQuestionId={(id) => {
                // Add your question selection logic here
                console.log('Selected question ID:', id);
              }}
          />
      )}
    </div>
  );
};

export default AppSidebar;
