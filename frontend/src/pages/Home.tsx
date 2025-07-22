import { useState } from "react";
import Chat from "../components/UI/Chat";
import ChatToast from "../components/UI/ChatToast";

const Home = () => {
  const [showToast, setShowToast] = useState(true);

  return (
    <div>
      <Chat />
      {showToast && (
        <ChatToast
          message="토스트 나왔다!"
          onClose={() => {
            setShowToast(false);
          }}
        />
      )}
    </div>
  );
};
export default Home;
