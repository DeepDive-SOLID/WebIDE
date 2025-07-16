import "../../styles/AppSidebar.scss";
import Box from "../UI/box";
import Bargraph from "../UI/Bargraph";
import { LuBox } from "react-icons/lu";
import { IoListOutline } from "react-icons/io5";
import { CiFileOn } from "react-icons/ci";
import { FaUsers } from "react-icons/fa";

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
        <>
          <div className="section top-section">
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

          <div className="section bottom-section">
            <div className="team-status">
              <h3>팀원 현황(3 /5)</h3>
              <Bargraph name="user1" language="JS" success={4} total={4} />
              <Bargraph name="user2" language="JS" success={3} total={4} />
              <Bargraph name="user3" language="JS" success={2} total={4} />
              <Bargraph name="" language="" success={0} total={4} />
              <Bargraph name="" language="" success={0} total={4} />
            </div>

            <div className="current-container">
              <FaUsers className="container-icon" />
              <div className="container-texts">
                <p className="label">현재 컨테이너</p>
                <p className="name">SOLID 컨테이너</p>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default AppSidebar;
