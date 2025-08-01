import styles from "../../styles/Chat.module.scss";
import ChatBox from "./ChatBox";
import { inputBtn_dark } from "../../assets";
import { useState, useEffect, useRef } from "react";
import { IoChevronBack, IoChevronForward } from "react-icons/io5";
import { getCurrentMemberId, getToken } from "../../utils/auth";
import { useParams } from "react-router";
import { useStore } from "../../stores/store";
import ChatToast from "./ChatToast";
import type { ChatDto } from "../../types/chatDto";
import chatApi from "../../api/chatApi";

const Chat = () => {
  const [isOpen, setIsOpen] = useState(true);
  const memberId = getCurrentMemberId();
  const accessToken = getToken();
  const { chatRoomId } = useParams();
  const stompClient = useStore((state) => state.stompClient);
  const [joined, setJoined] = useState(false);
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState<ChatDto[]>([]);
  const inputRef = useRef<HTMLInputElement | null>(null);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");
  const [toastUser, setToastUser] = useState<string>("");

  // 채팅방에 입장한 경우, 이전 메시지들 불러오기
  useEffect(() => {
    if (!joined) return;

    const fetchMessages = async () => {
      try {
        const data = await chatApi.getChatList(chatRoomId!);
        setMessages(data);
      } catch (e) {
        console.error("[초기 메시지 로딩 실패]", e);
      }
    };

    fetchMessages();
  }, [joined, chatRoomId]);

  // 채팅방 입장 요청 보내기
  useEffect(() => {
    const joinChatRoom = async () => {
      try {
        const data = await chatApi.joinChatRoom(chatRoomId!, accessToken!);
        if (data) {
          setJoined(true);
        }
      } catch (e) {
        console.error(e);
      }
    };

    joinChatRoom();
  }, [stompClient, chatRoomId, accessToken]);

  // 실시간 메시지 수신 처리
  useEffect(() => {
    if (!joined || !stompClient) return;

    const callback = (message: { body: string }) => {
      console.log("받은 메시지:", message);
      if (message.body) {
        const newMessage: ChatDto = JSON.parse(message.body);
        setMessages((prevMessages) => [...prevMessages, newMessage]);

        // 상대방 메시지만 알림 표시(본인 및 시스템 알림은 제외)
        if (
          newMessage.chatType === "CHAT" &&
          newMessage.memberId !== memberId
        ) {
          setShowToast(true);
          triggerToast(newMessage.memberId, newMessage.chatText);
        }
      }
    };

    // 구독 시작
    const subscription = stompClient.subscribe(
      `/topic/chatRooms/${chatRoomId}`,
      callback
    );

    // 언마운트 시 구독 해제
    return () => {
      subscription.unsubscribe({
        destination: `/topic/chatRooms/${chatRoomId}`,
      });
    };
  }, [chatRoomId, joined, stompClient]);

  // 채팅 알림 제어 함수
  const triggerToast = (user: string, msg: string) => {
    setToastUser(user);
    setToastMessage(msg);
    setShowToast(true);
    setTimeout(() => {
      setShowToast(false);
    }, 3000);
  };

  // 메시지 수신 시 자동 스크롤
  const chatListRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    const el = chatListRef.current;
    if (el) el.scrollTop = el.scrollHeight;
  }, [messages]);

  // 메시지 전송 핸들러
  const handleSendMessage = () => {
    console.log("message:", message);
    console.log("trimmed:", message.trim());
    console.log("stompClient:", stompClient);

    if (!message.trim() || !stompClient || !stompClient.connected) return;

    stompClient.publish({
      destination: `/app/chatRooms/${chatRoomId}`,
      body: message,
    });

    setMessage("");
    inputRef.current?.focus();
  };

  return (
    <div className={styles.chatContainer}>
      {showToast && (
        <ChatToast
          user={toastUser}
          message={toastMessage}
          onClose={() => setShowToast(false)}
        />
      )}
      <div
        className={`${styles.toggleButton} ${
          !isOpen ? styles.buttonClosed : ""
        }`}
        onClick={() => setIsOpen(!isOpen)}
      >
        {isOpen ? (
          <IoChevronForward size={24} color="#fff" />
        ) : (
          <IoChevronBack size={24} color="#fff" />
        )}
      </div>

      <div
        className={`${styles.chatWrapper} ${
          isOpen ? styles.chatOpen : styles.chatClosed
        }`}
      >
        <div className={styles.chatList} ref={chatListRef}>
          {messages.map((msg, idx) => {
            if (!msg.chatText?.trim()) return null;
            return msg.chatType === "SYSTEM" ? (
              <p key={idx} className={styles.systemMessage}>
                {msg.chatText}
              </p>
            ) : (
              <ChatBox
                key={idx}
                user={msg.memberId!}
                message={msg.chatText}
                isUserMessage={msg.memberId === memberId}
              />
            );
          })}
        </div>
        <div className={styles.inputWrapper}>
          <input
            type="text"
            placeholder="메시지를 입력하세요"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                handleSendMessage();
              }
            }}
          />
          <img
            src={inputBtn_dark}
            alt="Send"
            className={styles.inputBtn}
            onClick={handleSendMessage}
          />
        </div>
      </div>
    </div>
  );
};

export default Chat;
