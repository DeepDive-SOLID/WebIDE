import styles from "../../styles/Chat.module.scss";
import ChatBox from "./ChatBox";
import { inputBtn } from "../../assets/";
import { useMemo, useState, useEffect, useRef } from "react";
import { IoChevronBack, IoChevronForward } from "react-icons/io5";

const Chat = () => {
  const [isOpen, setIsOpen] = useState(true);

  const messages = useMemo(
    () => [
      { type: "system", message: "user1이 입장하였습니다" },
      { type: "system", message: "user2이 입장하였습니다" },
      {
        type: "chat",
        user: "user1",
        message: "문제 해결하신 분",
        isUserMessage: true,
      },
      {
        type: "chat",
        user: "user2",
        message: "일단 전 아님",
        isUserMessage: false,
      },
      {
        type: "chat",
        user: "user2",
        message: "어렵네요",
        isUserMessage: false,
      },
    ],
    []
  );

  const chatListRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    const el = chatListRef.current;
    if (el) el.scrollTop = el.scrollHeight;
  }, [messages]);

  const [inputValue, setInputValue] = useState("");
  const handleSendMessage = () => {
    if (inputValue.trim() === "") return;
    console.log("전송된 메시지:", inputValue);
    setInputValue("");
  };

  return (
    <div className={styles.chatContainer}>
      <div className={`${styles.toggleButton} ${!isOpen ? styles.buttonClosed : ""}`} onClick={() => setIsOpen(!isOpen)}>
        {isOpen ? <IoChevronForward size={24} color='#fff' /> : <IoChevronBack size={24} color='#fff' />}
      </div>

      <div className={`${styles.chatWrapper} ${isOpen ? styles.chatOpen : styles.chatClosed}`}>
        <div className={styles.chatList} ref={chatListRef}>
          {messages.map((msg, idx) =>
            msg.type === "system" ? (
              <p key={idx} className={styles.systemMessage}>
                {msg.message}
              </p>
            ) : (
              <ChatBox key={idx} user={msg.user!} message={msg.message} isUserMessage={msg.isUserMessage!} />
            )
          )}
        </div>
        <div className={styles.inputWrapper}>
          <input
            type='text'
            placeholder='메시지를 입력하세요'
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                handleSendMessage();
              }
            }}
          />
          <img src={inputBtn} alt='Send' className={styles.inputBtn} onClick={handleSendMessage} />
        </div>
      </div>
    </div>
  );
};

export default Chat;
