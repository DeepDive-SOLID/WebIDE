import { useEffect } from "react";
import styles from "../../styles/Toast.module.scss";
import { profile } from "../../assets/";

export interface ChatToastProps {
  message: string;
  onClose: () => void;
}

const ChatToast = ({ message, onClose }: ChatToastProps) => {
  useEffect(() => {
    const timer = setTimeout(onClose, 3000);
    return () => clearTimeout(timer);
  }, [onClose]);

  return (
    <div className={styles.toastWrapper}>
      <div className={styles.toast}>
        <img src={profile} alt="Icon" className={styles.toastIcon} />
        <div className={styles.toastText}>
          <div className={styles.toastHeader}>
            <span className={styles.title}>user2</span>
            <span className={styles.time}>9:41 AM</span>
          </div>
          <div className={styles.message}>{message}</div>
        </div>
      </div>
    </div>
  );
};

export default ChatToast;
