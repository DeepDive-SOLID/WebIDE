import React from "react";
import styles from "../../styles/Container.module.scss";
import { FaCog, FaPlay, FaCrown, FaUser } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

import type {
  ContainerResponseDto,
  GroupMemberResponseDto,
} from "../../types/home";

interface ContainerCardProps {
  container: ContainerResponseDto;
  members?: GroupMemberResponseDto[];
  membersLoading?: boolean;
  membersError?: string | null;
  showSettingBtn?: boolean;
  onSettingClick?: () => void;
  showLeaveBtn?: boolean;
  onLeaveClick?: () => void;
  leaveLoading?: boolean;
  leaveError?: string;
  showJoinBtn?: boolean;
  onJoinClick?: () => void;
}

const ContainerCard: React.FC<ContainerCardProps> = ({
  container,
  members,
  membersLoading,
  membersError,
  showSettingBtn,
  onSettingClick,
  showLeaveBtn,
  onLeaveClick,
  leaveLoading,
  leaveError,
  showJoinBtn,
  onJoinClick,
}) => {
  const navigate = useNavigate();

  // 멤버들의 온라인 상태에 따라 컨테이너 상태 결정
  const containerStatus =
    members && members.length > 0
      ? members.some((member) => member.isOnline)
      : false;

  return (
    <div className={styles.containerCard}>
      <div className={styles.cardHeader}>
        <span
          className={styles.statusDot}
          style={{
            background: containerStatus ? "#34C759" : "#F44336",
          }}
        />
        <span className={styles.containerName}>
          {container.containerName} 컨테이너
        </span>
        <span className={styles.memberCount}>({container.memberCount})</span>
        {showSettingBtn && (
          <button className={styles.settingBtn} onClick={onSettingClick}>
            <FaCog />
          </button>
        )}
        {showLeaveBtn && (
          <button
            className={styles.leaveBtn}
            onClick={onLeaveClick}
            disabled={leaveLoading}
          >
            {leaveLoading ? "탈퇴 중..." : "탈퇴하기"}
          </button>
        )}
        {leaveError && (
          <div style={{ color: "red", marginTop: 8 }}>{leaveError}</div>
        )}
      </div>
      <div className={styles.memberList}>
        {membersLoading ? (
          <span>멤버 로딩 중...</span>
        ) : membersError ? (
          <span style={{ color: "red" }}>{membersError}</span>
        ) : members && members.length > 0 ? (
          members.map((member) => (
            <div className={styles.member} key={member.teamUserId}>
              <span
                className={styles.statusDot}
                style={{
                  background: member.isOnline ? "#34C759" : "#F44336",
                  marginRight: 6,
                }}
              />
              <span className={styles.memberName}>{member.memberName}</span>
              {member.authority === "ROOT" ? (
                <FaCrown className={styles.crownIcon} />
              ) : (
                <FaUser className={styles.userIcon} />
              )}
            </div>
          ))
        ) : (
          <span>멤버가 없습니다.</span>
        )}
      </div>
      {showJoinBtn && (
        <button
          className={styles.joinBtn}
          onClick={() => {
            onJoinClick?.();
            // navigate(`${location.pathname}/${container.containerId}`);
            navigate(`/container/${container.containerId}`);
          }}
        >
          <FaPlay /> 참가하기
        </button>
      )}
    </div>
  );
};

export default ContainerCard;
