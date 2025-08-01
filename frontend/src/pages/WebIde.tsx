import { useParams } from "react-router-dom";
import AlgorithmNav from "../components/APP/Nav/AlgorithmNav";
import Ide from "./containerIde/Ide";
import Chat from "../components/UI/Chat";
import { useState } from "react";

const WebIde = () => {
  const { chatRoomId } = useParams();
  const [modal, setModal] = useState(false);

  return (
    <div>
      <div style={{ display: "flex", flexDirection: "row", overflow: "hidden" }}>
        <AlgorithmNav containerId={Number(chatRoomId)} modal={modal} setModal={setModal} />
        <Ide containerId={Number(chatRoomId)} modal={modal} />
        <Chat />
      </div>
    </div>
  );
};

export default WebIde;
