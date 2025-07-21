import styles from "../../styles/Bargraph.module.scss";

export interface BargraphProps {
  name: string;
  language: string;
  success: number;
  total: number;
}

const Bargraph = ({ name, language, success, total }: BargraphProps) => {
  const safeSuccess = Math.min(success, total);
  const percentage = total > 0 ? Math.round((safeSuccess / total) * 100) : 0;

  return (
    <div className={styles.bargraphWrapper}>
      <div className={styles.bargraphUserInfo}>
        <div className={styles.bargraphName}>{name || <>&nbsp;</>}</div>
        <div className={styles.bargraphLanguage}>
          {language ? `(${language})` : <>&nbsp;</>}
        </div>
      </div>
      <div className={styles.bargraphBarWrapper}>
        <div className={styles.bargraphBar}>
          <div
            className={styles.bargraphBarFill}
            style={{ width: `${percentage}%` }}
          />
        </div>
      </div>
      <span className={styles.bargraphPercent}>{percentage}%</span>
    </div>
  );
};

export default Bargraph;
