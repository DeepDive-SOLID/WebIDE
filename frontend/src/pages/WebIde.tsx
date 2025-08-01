import { useParams } from "react-router-dom";
import AlgorithmNav from "../components/APP/Nav/AlgorithmNav";
import Ide from "./containerIde/Ide";
import Chat from "../components/UI/Chat";

const WebIde = () => {
  const { chatRoomId } = useParams();
  return (
    <div>
      <div style={{ display: "flex", flexDirection: "row", overflow: "hidden" }}>
        <AlgorithmNav containerId={Number(chatRoomId)} />
        <Ide containerId={Number(chatRoomId)} />
        <Chat />
      </div>
    </div>
  );
};

export default WebIde;
