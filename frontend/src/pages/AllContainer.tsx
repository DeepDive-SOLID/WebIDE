import React, { useState } from "react";
import styles from "../styles/Container.module.scss";
import { FaCrown, FaUser, FaCog, FaPlay } from "react-icons/fa";
import folderImg from "../assets/icons/folder.svg";
import CreateContainer from "../components/Modal/CreateContainer";
import OwnerSetting from "../components/Modal/OwnerSetting";

// 더미 데이터
const containers = [
  {
    id: 1,
    name: "SOLID 컨테이너",
    members: [
      { name: "이나영", active: true, isOwner: true },
      { name: "user1", active: false, isOwner: false },
      { name: "user2", active: true, isOwner: false },
      { name: "user3", active: true, isOwner: false },
    ],
    maxMembers: 5,
  },
  {
    id: 2,
    name: "나영이의 컨테이너",
    members: [
      { name: "이나영", active: true, isOwner: true },
      { name: "user1", active: false, isOwner: false },
      { name: "user2", active: true, isOwner: false },
      { name: "user3", active: true, isOwner: false },
      { name: "user4", active: true, isOwner: false },
    ],
    maxMembers: 5,
  },
];

const AllContainer: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSettingOpen, setIsSettingOpen] = useState(false);
  const [selectedContainerName, setSelectedContainerName] =
    useState<string>("");
  return (
    <div className={styles.allContainerWrap}>
      <div className={styles.header}>
        <h2>
          <img
            src={folderImg}
            alt="folder"
            style={{
              width: 28,
              height: 22,
              verticalAlign: "middle",
              marginRight: 8,
              marginBottom: 3,
            }}
          />
          모든 컨테이너
        </h2>
        <button
          className={styles.createBtn}
          onClick={() => setIsModalOpen(true)}
        >
          컨테이너 생성
        </button>
      </div>
      <div className={styles.containerList}>
        {containers.map((container) => (
          <div className={styles.containerCard} key={container.id}>
            <div className={styles.cardHeader}>
              <span
                className={styles.statusDot}
                style={{ background: "#34C759" }}
              />
              <span className={styles.containerName}>{container.name}</span>
              <span className={styles.memberCount}>
                ({container.members.length}/{container.maxMembers})
              </span>
              <button
                className={styles.settingBtn}
                onClick={() => {
                  setSelectedContainerName(container.name);
                  setIsSettingOpen(true);
                }}
              >
                <FaCog />
              </button>
            </div>
            <button className={styles.leaveBtn}>탈퇴하기</button>
            <div className={styles.memberList}>
              {container.members.map((member) => (
                <div className={styles.member} key={member.name}>
                  <span
                    className={styles.statusDot}
                    style={{
                      background: member.active ? "#34C759" : "#F44336",
                    }}
                  />
                  <span className={styles.memberName}>{member.name}</span>
                  {member.isOwner ? (
                    <FaCrown className={styles.crownIcon} />
                  ) : (
                    <FaUser className={styles.userIcon} />
                  )}
                </div>
              ))}
            </div>
            <button className={styles.joinBtn}>
              <FaPlay /> 참가하기
            </button>
          </div>
        ))}
      </div>
      {isModalOpen && <CreateContainer onClose={() => setIsModalOpen(false)} />}
      {isSettingOpen && (
        <OwnerSetting
          onClose={() => setIsSettingOpen(false)}
          containerName={selectedContainerName}
        />
      )}
    </div>
  );
};

export default AllContainer;
