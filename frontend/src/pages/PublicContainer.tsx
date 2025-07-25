import React, { useState } from "react";
import styles from "../styles/Container.module.scss";
import { AiOutlineGlobal } from "react-icons/ai";
import ContainerCard from "../components/Container/ContainerCard";
import { getPublicContainers, getContainerMembers } from "../api/home";
import type { ContainerResponseDto, GroupMemberResponseDto } from "../api/home";

const AllContainer: React.FC = () => {
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

  const fetchContainers = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getPublicContainers();
      setContainers(data);
    } catch {
      setError("공개 컨테이너 목록을 불러오지 못했습니다.");
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

  React.useEffect(() => {
    fetchContainers();
  }, []);

  // 컨테이너 목록이 바뀔 때마다 각 컨테이너 멤버 fetch
  React.useEffect(() => {
    containers.forEach((container) => {
      fetchMembers(container.containerId);
    });
    // eslint-disable-next-line
  }, [containers]);

  return (
    <div className={styles.allContainerWrap}>
      <div className={styles.header}>
        <h2>
          <AiOutlineGlobal
            size={28}
            style={{
              verticalAlign: "middle",
              marginRight: 8,
              marginBottom: 3,
            }}
          />
          공개된 컨테이너
        </h2>
      </div>
      <div className={styles.containerList}>
        {loading ? (
          <div>로딩 중...</div>
        ) : error ? (
          <div style={{ color: "red" }}>{error}</div>
        ) : containers.length === 0 ? (
          <div>공개된 컨테이너가 없습니다.</div>
        ) : (
          containers.map((container) => (
            <ContainerCard
              key={container.containerId}
              container={container}
              members={membersMap[container.containerId]}
              membersLoading={membersLoading[container.containerId]}
              membersError={membersError[container.containerId]}
              showJoinBtn={true}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default AllContainer;
