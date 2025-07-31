import React, { useEffect, useState } from "react";
import { getContainerMembers, inviteMember } from "../../api/homeApi";
import type { GroupMemberResponseDto } from "../../types/home";
import { getContainerDetail } from "../../api/homeApi";
import { deleteMember } from "../../api/homeApi";
import { updateContainer } from "../../api/homeApi";
import styles from "../../styles/Modal.module.scss";
import profileImg from "../../assets/images/profile_img.png";

interface OwnerSettingProps {
  onClose: () => void;
  containerId: number;
  containerName: string;
  onSuccess?: () => void;
}

// 활동일자 포맷 함수
function formatActivityDate(dateStr: string) {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  const now = new Date();

  // 오늘 여부 판별
  const isToday =
    date.getFullYear() === now.getFullYear() &&
    date.getMonth() === now.getMonth() &&
    date.getDate() === now.getDate();

  const diffMs = now.getTime() - date.getTime();
  const diffMin = Math.floor(diffMs / 60000);
  const diffHour = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHour / 24) + 1;

  let ago = "";
  if (!isToday) {
    ago = `${diffDay}일 전`;
  } else if (diffMin < 1) {
    ago = "방금 전";
  } else if (diffMin < 60) {
    ago = `${diffMin}분 전`;
  } else {
    ago = `${diffHour}시간 전`;
  }

  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");

  return `${yyyy}.${mm}.${dd} (${ago})`;
}

const OwnerSetting: React.FC<OwnerSettingProps> = ({
  onClose,
  containerId,
  containerName,
  onSuccess,
}) => {
  const [members, setMembers] = useState<GroupMemberResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [isPublic, setIsPublic] = useState<boolean | null>(null);
  const [inviteInput, setInviteInput] = useState("");
  const [inviteError, setInviteError] = useState("");
  const [inviteLoading, setInviteLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState<string | null>(null);
  const [deleteError, setDeleteError] = useState<string>("");
  const [updateLoading, setUpdateLoading] = useState(false);
  const [updateError, setUpdateError] = useState("");

  // 컨테이너 상세 정보 조회 (isPublic)
  useEffect(() => {
    const fetchContainerDetail = async () => {
      try {
        const detail = await getContainerDetail(containerId);
        setIsPublic(detail.isPublic ?? null);
      } catch {
        setIsPublic(null);
      }
    };
    fetchContainerDetail();
  }, [containerId]);

  useEffect(() => {
    const fetchMembers = async () => {
      setLoading(true);
      try {
        const data = await getContainerMembers(containerId);
        setMembers(data);
      } catch {
        setMembers([]);
      } finally {
        setLoading(false);
      }
    };
    fetchMembers();
  }, [containerId]);

  // 멤버 초대 핸들러
  const handleInvite = async () => {
    setInviteError("");
    if (!inviteInput.trim()) {
      setInviteError("초대할 아이디를 입력하세요.");
      return;
    }
    if (members.some((m) => m.memberId === inviteInput.trim())) {
      setInviteError("이미 멤버입니다.");
      return;
    }
    setInviteLoading(true);
    try {
      const newMember = await inviteMember(containerId, inviteInput.trim());
      setMembers((prev) => [...prev, newMember]);
      setInviteInput("");
    } catch (e: unknown) {
      if (
        typeof e === "object" &&
        e !== null &&
        "response" in e &&
        typeof (e as { response?: { data?: { message?: string } } })
          .response === "object"
      ) {
        const err = e as { response?: { data?: { message?: string } } };
        const backendMsg = err.response?.data?.message || "";
        if (
          backendMsg.includes("입력값") ||
          backendMsg.includes("Validation")
        ) {
          setInviteError("일치하는 사용자가 없습니다.");
        } else {
          setInviteError(backendMsg || "초대에 실패했습니다.");
        }
      } else {
        setInviteError("초대에 실패했습니다.");
      }
    } finally {
      setInviteLoading(false);
    }
  };

  // 멤버 삭제 핸들러
  const handleDeleteMember = async (targetMemberId: string) => {
    setDeleteError("");
    setDeleteLoading(targetMemberId);
    try {
      await deleteMember(containerId, targetMemberId);
      setMembers((prev) => prev.filter((m) => m.memberId !== targetMemberId));
    } catch {
      setDeleteError("멤버 삭제에 실패했습니다.");
    } finally {
      setDeleteLoading(null);
    }
  };

  // 컨테이너 정보 수정 핸들러
  const handleUpdate = async () => {
    setUpdateError("");
    if (isPublic === null) return;
    setUpdateLoading(true);
    try {
      await updateContainer(containerId, { isPublic });
      if (onSuccess) onSuccess(); // 성공 시 새로고침
      onClose();
    } catch {
      setUpdateError("수정에 실패했습니다.");
    } finally {
      setUpdateLoading(false);
    }
  };

  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <button className={styles.closeBtn} onClick={onClose}>
          &times;
        </button>
        <h2 className={styles.modalTitle}>{containerName} 컨테이너</h2>
        <div className={styles.formGroup}>
          <label>컨테이너 공개 여부</label>
          <div className={styles.buttonRow}>
            <button
              className={
                isPublic === true ? styles.primaryBtn : styles.secondaryBtn
              }
              type="button"
              onClick={() => setIsPublic(true)}
            >
              공개
            </button>
            <button
              className={
                isPublic === false ? styles.primaryBtn : styles.secondaryBtn
              }
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
              disabled={inviteLoading}
            />
            <button
              className={styles.inviteBtn}
              type="button"
              onClick={handleInvite}
              disabled={inviteLoading}
            >
              {inviteLoading ? "초대 중..." : "초대"}
            </button>
          </div>
          {inviteError && (
            <div style={{ color: "red", marginTop: 4 }}>{inviteError}</div>
          )}
        </div>
        <div>
          <div style={{ marginBottom: 8 }}>
            <span className={styles.memberBadge}>
              멤버 ({members.length}/5)
            </span>
          </div>
          <div className={styles.memberList}>
            {loading ? (
              <div>멤버 로딩 중...</div>
            ) : members.length === 0 ? (
              <div>멤버가 없습니다.</div>
            ) : (
              members.map((member) => (
                <div className={styles.memberItem} key={member.teamUserId}>
                  <img
                    src={profileImg}
                    alt="user"
                    className={styles.memberAvatar}
                  />
                  {member.memberId}
                  <span
                    className={`${styles.roleBadge} ${
                      member.authority === "ROOT" ? styles.owner : styles.member
                    }`}
                  >
                    {member.authority === "ROOT" ? "Owner" : "Member"}
                  </span>
                  <span className={styles.memberActivity}>
                    {/* 활동일자: 마지막 활동일시 표시 */}
                    {member.lastActivityDate
                      ? `활동일자 ${formatActivityDate(
                          member.lastActivityDate
                        )}`
                      : "-"}
                  </span>
                  {/* 권한이 Member일 때만 삭제 버튼 예시 */}
                  {member.authority !== "ROOT" && (
                    <div className={styles.memberActions}>
                      <button
                        className={styles.removeBtn}
                        onClick={() => handleDeleteMember(member.memberId)}
                        disabled={deleteLoading === member.memberId}
                      >
                        {deleteLoading === member.memberId ? "삭제 중..." : "×"}
                      </button>
                    </div>
                  )}
                </div>
              ))
            )}
          </div>
        </div>
        {deleteError && (
          <div style={{ color: "red", marginTop: 8 }}>{deleteError}</div>
        )}
        <button
          className={styles.addBtn}
          onClick={handleUpdate}
          disabled={updateLoading || isPublic === null}
        >
          {updateLoading ? "수정 중..." : "수정 완료"}
        </button>
        {updateError && (
          <div style={{ color: "red", marginTop: 8 }}>{updateError}</div>
        )}
      </div>
    </div>
  );
};

export default OwnerSetting;
