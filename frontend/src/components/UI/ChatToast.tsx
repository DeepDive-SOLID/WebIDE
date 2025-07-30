import {useEffect, useState} from "react";
import styles from "../../styles/Toast.module.scss";
import { profile } from "../../assets/";

export interface ChatToastProps {
  user: string;
  message: string;
  onClose: () => void;
}

const ChatToast = ({ user, message, onClose }: ChatToastProps) => {
  const [time, setTime] = useState("");

  // 총 3초 렌더링
  useEffect(() => {
    const now = new Date();
    const formattedTime = formatTime(now);
    setTime(formattedTime);

    // 컴포넌트가 처음 렌더링될 때 현재 시간을 포맷하여 상태에 저장
    const timer = setTimeout(onClose, 3000);
    return () => clearTimeout(timer);
  }, [onClose]);

  // Date를 시간 문자열로 변환
  const formatTime = (date: Date) => {
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const isAM = hours < 12;
    const displayHours = hours % 12 || 12;
    const displayMinutes = minutes.toString().padStart(2, '0');
    return `${displayHours}:${displayMinutes} ${isAM ? 'AM' : 'PM'}`;
  };

  return (
      <div className={styles.toastWrapper}>
        <div className={styles.toast}>
          <img src={profile} alt="Icon" className={styles.toastIcon} />
          <div className={styles.toastText}>
            <div className={styles.toastHeader}>
              <span className={styles.title}>{user}</span>
              <span className={styles.time}>{time}</span>
            </div>
            <div className={styles.message}>{message}</div>
          </div>
        </div>
      </div>
  );
};

export default ChatToast;
