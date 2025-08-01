import { useParams } from "react-router-dom";
import AlgorithmNav from "../components/APP/Nav/AlgorithmNav";
import Ide from "./containerIde/Ide";
import Chat from "../components/UI/Chat";
import ChatToast from "../components/UI/ChatToast";
import { useState } from "react";

const WebIde = () => {
  const { containerId } = useParams<{ containerId: string }>();
  const [showToast, setShowToast] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  return (
    <div>
      <div style={{ display: "flex", flexDirection: "row", overflow: "hidden" }}>
        <AlgorithmNav containerId={Number(containerId)} />
        <Ide containerId={Number(containerId)}/>
        <Chat />
        {showToast && (
          <ChatToast
            message='토스트 나왔다!'
            onClose={() => {
              setShowToast(false);
            }}
          />
        )}
      </div>
    </div>
  );
};

export default WebIde;
