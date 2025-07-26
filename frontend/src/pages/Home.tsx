import AlgorithmNav from "../components/APP/Nav/AlgorithmNav";
import AddFileModal from "../components/APP/AddFileModal";
import { useState } from "react";

const Home = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  return (
    <div>
      {/* <button onClick={openModal}>모달창 열기</button>
      {isModalOpen && <AddFileModal onClose={closeModal} />} */}

      <AlgorithmNav />
    </div>
  );
};
export default Home;
