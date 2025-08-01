import React, { useState, useMemo } from "react";
import styles from "../../styles/Modal.module.scss";
import profileImg from "../../assets/images/profile_img.png";
import { createContainer } from "../../api/homeApi";
import type { CreateContainerDto } from "../../types/home";

interface CreateContainerProps {
  onClose: () => void;
  onSuccess?: () => void;
}

const CreateContainer: React.FC<CreateContainerProps> = ({
  onClose,
  onSuccess,
}) => {
  // 토큰에서 사용자 ID 직접 추출
  const userInfo = useMemo(() => {
    const token = localStorage.getItem("accessToken");
    const decodedToken = token ? JSON.parse(atob(token.split(".")[1])) : null;
    return decodedToken ? { memberId: decodedToken.memberId } : null;
  }, []);
  const [containerName, setContainerName] = useState("");
  const [containerContent, setContainerContent] = useState("");
  const [isPublic, setIsPublic] = useState(true);
  const [inviteInput, setInviteInput] = useState("");
  const [invitedMemberIds, setInvitedMemberIds] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // userInfo가 바뀔 때마다 Owner가 리스트에서 제거되도록 보장 (백엔드에서 자동 추가)
  React.useEffect(() => {
    if (userInfo?.memberId) {
      setInvitedMemberIds((prev) =>
        prev.filter((id) => id !== userInfo.memberId)
      );
    }
  }, [userInfo]);

  // 초대 멤버 추가 (Owner는 추가 불가)
  const handleInvite = () => {
    if (
      inviteInput &&
      !invitedMemberIds.includes(inviteInput) &&
      inviteInput !== userInfo?.memberId
    ) {
      setInvitedMemberIds([...invitedMemberIds, inviteInput]);
      setInviteInput("");
    }
  };

  // 초대 멤버 삭제 (Owner는 삭제 불가)
  const handleRemoveInvite = (id: string) => {
    if (id === userInfo?.memberId) return;
    setInvitedMemberIds(invitedMemberIds.filter((m) => m !== id));
  };

  // 컨테이너 생성
  const handleCreate = async () => {
    setError("");
    if (!containerName.trim()) {
      setError("컨테이너 명을 입력하세요.");
      return;
    }
    if (containerName.length < 1 || containerName.length > 20) {
      setError("컨테이너 명은 1~20자여야 합니다.");
      return;
    }
    if (containerContent.length > 200) {
      setError("설명은 200자 이내여야 합니다.");
      return;
    }
    setLoading(true);
    const body: CreateContainerDto = {
      containerName,
      containerContent,
      isPublic,
      invitedMemberIds:
        invitedMemberIds.length > 0 ? invitedMemberIds : undefined,
    };
    try {
      await createContainer(body);
      if (onSuccess) onSuccess(); // 성공 시 목록 새로고침
      onClose(); // 성공 시 모달 닫기
    } catch (e: unknown) {
      if (typeof e === "object" && e !== null && "response" in e) {
        const err = e as { response?: { data?: { message?: string } } };
        setError(err.response?.data?.message || "생성에 실패했습니다.");
      } else {
        setError("생성에 실패했습니다.");
      }
    } finally {
      setLoading(false);
    }
  };

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
            value={containerName}
            onChange={(e) => setContainerName(e.target.value)}
          />
        </div>
        <div className={styles.formGroup}>
          <label>컨테이너 설명</label>
          <textarea
            className={styles.textarea}
            placeholder="컨테이너에 대한 설명을 작성하세요"
            value={containerContent}
            onChange={(e) => setContainerContent(e.target.value)}
          />
        </div>
        <div className={styles.formGroup}>
          <label>컨테이너 공개 여부</label>
          <div className={styles.buttonRow}>
            <button
              className={isPublic ? styles.primaryBtn : styles.secondaryBtn}
              type="button"
              onClick={() => setIsPublic(true)}
            >
              공개
            </button>
            <button
              className={!isPublic ? styles.primaryBtn : styles.secondaryBtn}
              type="button"
              onClick={() => setIsPublic(false)}
            >
              비공개
            </button>
          </div>
        </div>
        <div className={styles.formGroup}>
          <label>팀원 초대</label>
          <div className={styles.buttonRow}>
            <input
              className={styles.inviteInput}
              placeholder="초대할 아이디를 작성해주세요"
              value={inviteInput}
              onChange={(e) => setInviteInput(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleInvite();
              }}
            />
            <button
              className={styles.inviteBtn}
              style={{ maxWidth: 80 }}
              type="button"
              onClick={handleInvite}
            >
              초대
            </button>
          </div>
        </div>
        <div>
          <div style={{ marginBottom: 8 }}>
            <span className={styles.memberBadge}>
              초대 멤버 (
              {invitedMemberIds.length + (userInfo?.memberId ? 1 : 0)})
            </span>
          </div>
          <div className={styles.memberList}>
            {/* Owner (로그인한 사용자) 표시 */}
            {userInfo?.memberId && (
              <div className={styles.memberItem}>
                <img
                  src={profileImg}
                  alt="user"
                  className={styles.memberAvatar}
                />
                {userInfo.memberId}
                <span className={`${styles.roleBadge} ${styles.owner}`}>
                  Owner
                </span>
              </div>
            )}
            {/* 초대된 멤버들 표시 */}
            {invitedMemberIds.map((id) => (
              <div className={styles.memberItem} key={id}>
                <img
                  src={profileImg}
                  alt="user"
                  className={styles.memberAvatar}
                />
                {id}
                <span className={`${styles.roleBadge} ${styles.member}`}>
                  Member
                </span>
                <button
                  className={styles.removeBtn}
                  onClick={() => handleRemoveInvite(id)}
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        </div>
        {error && <div style={{ color: "red", margin: "8px 0" }}>{error}</div>}
        <button
          className={styles.addBtn}
          onClick={handleCreate}
          disabled={loading}
        >
          {loading ? "생성 중..." : "컨테이너 추가"}
        </button>
      </div>
    </div>
  );
};

export default CreateContainer;
