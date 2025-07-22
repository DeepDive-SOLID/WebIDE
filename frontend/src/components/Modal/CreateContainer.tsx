import React from "react";
import styles from "../../styles/Modal.module.scss";
import profileImg from "../../assets/images/profile_img.png";

interface CreateContainerProps {
  onClose: () => void;
}

const CreateContainer: React.FC<CreateContainerProps> = ({ onClose }) => {
  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <button className={styles.closeBtn} onClick={onClose}>
          &times;
        </button>
        <h2>컨테이너 생성</h2>
        <div className={styles.formGroup}>
          <label>컨테이너 명</label>
          <input
            className={styles.containerNameInput}
            placeholder="컨테이너 명을 입력하세요"
          />
        </div>
        <div className={styles.formGroup}>
          <label>컨테이너 설명</label>
          <textarea
            className={styles.textarea}
            placeholder="컨테이너에 대한 설명을 작성하세요"
          />
        </div>
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
            <button className={styles.inviteBtn} style={{ maxWidth: 80 }}>
              초대
            </button>
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
              <button className={styles.removeBtn}>×</button>
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
              <button className={styles.removeBtn}>×</button>
            </div>
          </div>
        </div>
        <button className={styles.addBtn}>컨테이너 추가</button>
      </div>
    </div>
  );
};

export default CreateContainer;
