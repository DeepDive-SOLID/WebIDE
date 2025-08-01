import styles from "../../styles/Chat.module.scss";

export interface ChatBoxProps {
  user: string;
  message: string;
  isUserMessage: boolean;
}

const ChatBox = ({ user, message, isUserMessage }: ChatBoxProps) => {
  const wrapperClass = isUserMessage ? styles.userMessage : styles.otherMessage;

  return (
    <div className={`${styles.chatBox} ${wrapperClass}`}>
      <span className={styles.user}>{user}</span>
      <div className={styles.message}>{message}</div>
    </div>
  );
};

export default ChatBox;
