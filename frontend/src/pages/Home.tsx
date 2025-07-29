import Chat from "../components/UI/Chat";
import ChatToast from "../components/UI/ChatToast";
import AlgorithmNav from "../components/APP/Nav/AlgorithmNav";
import AddFileModal from "../components/APP/AddFileModal";
import { useState } from "react";
import Ide from "./containerIde/Ide";
const Home = () => {
  const [showToast, setShowToast] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);
  return (
    <div style={{ display: "flex", flexDirection: "row" }}>
      <AlgorithmNav />
      <Ide />
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
  );
};
export default Home;
