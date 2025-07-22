import React from "react";
import styles from "../../styles/Modal.module.scss";
import profileImg from "../../assets/images/profile_img.png";

interface OwnerSettingProps {
  onClose: () => void;
  containerName: string;
}

const OwnerSetting: React.FC<OwnerSettingProps> = ({
  onClose,
  containerName,
}) => {
  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <button className={styles.closeBtn} onClick={onClose}>
          &times;
        </button>
        <h2 className={styles.modalTitle}>{containerName}</h2>
        <div className={styles.formGroup}>
          <label>컨테이너 공개 여부</label>
          <div className={styles.buttonRow}>
            <button className={styles.primaryBtn}>공개</button>
            <button className={styles.secondaryBtn}>비공개</button>
          </div>
        </div>
        <div className={styles.formGroup}>
          <label>팀원 초대</label>
          <div className={styles.buttonRow}>
            <input
              className={styles.inviteInput}
              placeholder="초대할 아이디를 작성해주세요"
            />
            <button className={styles.inviteBtn}>초대</button>
          </div>
        </div>
        <div>
          <div style={{ marginBottom: 8 }}>
            <span className={styles.memberBadge}>멤버 (3/5)</span>
          </div>
          <div className={styles.memberList}>
            <div className={styles.memberItem}>
              <img
                src={profileImg}
                alt="user"
                className={styles.memberAvatar}
              />
              user1
              <span className={`${styles.roleBadge} ${styles.owner}`}>
                Owner
              </span>
              <span className={styles.memberActivity}>
                활동일자 2025.07.11 (1시간 전)
              </span>
            </div>
            <div className={styles.memberItem}>
              <img
                src={profileImg}
                alt="user"
                className={styles.memberAvatar}
              />
              user2
              <span className={`${styles.roleBadge} ${styles.member}`}>
                Member
              </span>
              <span className={styles.memberActivity}>
                활동일자 2025.07.10 (1일 전)
              </span>
              <div className={styles.memberActions}>
                <button className={styles.roleBtn}>권한 넘기기</button>
                <button className={styles.removeBtn}>×</button>
              </div>
            </div>
            <div className={styles.memberItem}>
              <img
                src={profileImg}
                alt="user"
                className={styles.memberAvatar}
              />
              user3
              <span className={`${styles.roleBadge} ${styles.member}`}>
                Member
              </span>
              <span className={styles.memberActivity}>
                활동일자 2025.07.11 (2시간 전)
              </span>
              <div className={styles.memberActions}>
                <button className={styles.roleBtn}>권한 넘기기</button>
                <button className={styles.removeBtn}>×</button>
              </div>
            </div>
          </div>
        </div>
        <button className={styles.addBtn}>수정 완료</button>
      </div>
    </div>
  );
};

export default OwnerSetting;
