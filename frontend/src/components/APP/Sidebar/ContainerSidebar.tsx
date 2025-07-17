import Box from "../../UI/Box";
import { LuBox } from "react-icons/lu";

const ContainerSidebar = () => {
  return (
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
  );
};

export default ContainerSidebar;
