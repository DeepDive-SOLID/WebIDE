import "../../styles/AppSidebar.scss";
import Box from "../UI/box";
import { LuBox } from "react-icons/lu";

import { IoListOutline } from "react-icons/io5";
import { CiFileOn } from "react-icons/ci";

export interface AppSidebarProps {
  isOpen: boolean;
  type: "container" | "algorithm" | null;
}

const AppSidebar = ({ isOpen, type }: AppSidebarProps) => {
  if (!isOpen || !type) return null;
  return (
    <div className="app-sidebar">
      {type === "container" && (
        <div className="section">
          <h2>Container</h2>
          <div className="box-list">
            <Box
              icon={<LuBox size={20} />}
              title="내 컨테이너"
              onClick={() => {}}
              cnt={10}
              className="box-primary"
            />
            <Box
              icon={<LuBox size={20} />}
              title="공유받은 컨테이너"
              onClick={() => {}}
              cnt={5}
              className="box-primary"
            />
          </div>
        </div>
      )}

      {type === "algorithm" && (
        <div className="section">
          <h2>Algorithm</h2>
          <div className="box-list">
            <Box
              icon={<IoListOutline size={20} />}
              title="기초수학"
              onClick={() => {}}
              className="box-primary"
            />
            <Box
              icon={<CiFileOn size={20} />}
              title="더하기"
              onClick={() => {}}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default AppSidebar;
