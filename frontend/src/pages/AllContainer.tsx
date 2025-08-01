import React, { useState } from "react";
import styles from "../styles/Container.module.scss";
import folderImg from "../assets/icons/folder.svg";
import folderDarkImg from "../assets/icons/folder_darkmode.svg";
import CreateContainer from "../components/Modal/CreateContainer";
import OwnerSetting from "../components/Modal/OwnerSetting";
import { getContainers, getContainerMembers, leaveContainer } from "../api/homeApi";
import type { ContainerResponseDto, GroupMemberResponseDto } from "../types/home";

import ContainerCard from "../components/Container/ContainerCard";
import emptyImg from "../assets/icons/empty.svg";
import { useSelector } from "react-redux";
import type { RootState } from "../stores";

const AllContainer: React.FC = () => {
  const isDark = useSelector((state: RootState) => state.theme.isDark);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSettingOpen, setIsSettingOpen] = useState(false);
  const [selectedContainerName, setSelectedContainerName] = useState<string>("");
  const [selectedContainerId, setSelectedContainerId] = useState<number | null>(null);
  const [containers, setContainers] = useState<ContainerResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // containerId별 멤버 리스트 상태
  const [membersMap, setMembersMap] = useState<{
    [containerId: number]: GroupMemberResponseDto[];
  }>({});
  const [membersLoading, setMembersLoading] = useState<{
    [containerId: number]: boolean;
  }>({});
  const [membersError, setMembersError] = useState<{
    [containerId: number]: string | null;
  }>({});

  const [leaveLoading, setLeaveLoading] = useState<number | null>(null);
  const [leaveError, setLeaveError] = useState<string>("");

  const fetchContainers = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getContainers();
      console.log(data);
      setContainers(data);
    } catch {
      setError("컨테이너 목록을 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // 컨테이너별 멤버 불러오기
  const fetchMembers = async (containerId: number) => {
    setMembersLoading((prev) => ({ ...prev, [containerId]: true }));
    setMembersError((prev) => ({ ...prev, [containerId]: null }));
    try {
      const members = await getContainerMembers(containerId);
      setMembersMap((prev) => ({ ...prev, [containerId]: members }));
    } catch {
      setMembersError((prev) => ({
        ...prev,
        [containerId]: "멤버 정보를 불러오지 못했습니다.",
      }));
    } finally {
      setMembersLoading((prev) => ({ ...prev, [containerId]: false }));
    }
  };

  // 컨테이너 탈퇴 핸들러
  const handleLeave = async (containerId: number) => {
    setLeaveError("");
    setLeaveLoading(containerId);
    try {
      await leaveContainer(containerId);
      await fetchContainers(); // 탈퇴 후 목록 새로고침
    } catch {
      setLeaveError("컨테이너 탈퇴에 실패했습니다.");
    } finally {
      setLeaveLoading(null);
    }
  };

  React.useEffect(() => {
    fetchContainers();
  }, []);

  // 컨테이너 목록이 바뀔 때마다 각 컨테이너 멤버 fetch
  React.useEffect(() => {
    containers.forEach((container) => {
      fetchMembers(container.containerId);
    });
  }, [containers]);

  return (
    <div className={styles.allContainerWrap}>
      <div className={styles.header}>
        <h2>
          <img
            src={isDark ? folderDarkImg : folderImg}
            alt='folder'
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
        <button className={styles.createBtn} onClick={() => setIsModalOpen(true)}>
          컨테이너 생성
        </button>
      </div>
      <div className={styles.containerList}>
        {loading ? (
          <div>로딩 중...</div>
        ) : error ? (
          <div style={{ color: "red" }}>{error}</div>
        ) : containers.length === 0 ? (
          <div className={styles.emptyContainer}>
            <img src={emptyImg} alt='빈 컨테이너' style={{ width: 135, marginBottom: 10 }} />
            <p>컨테이너가 없습니다!</p>
          </div>
        ) : (
          containers.map((container) => {
            const token = localStorage.getItem("accessToken");
            const decodedToken = token ? JSON.parse(atob(token.split(".")[1])) : null;

            const isSettingBtnVisible = membersMap[container.containerId]?.some((member) => member.authority === "ROOT" && member.memberId === decodedToken?.memberId);

            const isLeaveBtnVisible = membersMap[container.containerId]?.some((member) => member.memberId === decodedToken?.memberId && member.authority !== "ROOT");

            return (
              <ContainerCard
                key={container.containerId}
                container={container}
                members={membersMap[container.containerId]}
                membersLoading={membersLoading[container.containerId]}
                membersError={membersError[container.containerId]}
                showSettingBtn={isSettingBtnVisible}
                onSettingClick={() => {
                  setSelectedContainerName(container.containerName);
                  setSelectedContainerId(container.containerId);
                  setIsSettingOpen(true);
                }}
                showLeaveBtn={isLeaveBtnVisible}
                onLeaveClick={() => handleLeave(container.containerId)}
                leaveLoading={leaveLoading === container.containerId}
                leaveError={leaveError}
                showJoinBtn={true}
              />
            );
          })
        )}
      </div>
      {isModalOpen && <CreateContainer onClose={() => setIsModalOpen(false)} onSuccess={fetchContainers} />}
      {isSettingOpen && selectedContainerId && (
        <OwnerSetting onClose={() => setIsSettingOpen(false)} containerId={selectedContainerId} containerName={selectedContainerName} onSuccess={fetchContainers} />
      )}
    </div>
  );
};

export default AllContainer;
