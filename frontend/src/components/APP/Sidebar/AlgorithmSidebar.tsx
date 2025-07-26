import { useTreeManager } from "./useTreeManager";
import TreeView from "./TreeView";
import ContextMenu from "../ContextMenu";
import Bargraph from "../../UI/Bargraph";
import { FaUsers } from "react-icons/fa";
import styles from "../../../styles/AppSidebar.module.scss";
import { useState, useEffect } from "react";
import {
  getDirectoryList,
  createDirectory,
  renameDirectory,
  deleteDirectory,
} from "../../../api/directoryApi";

const AlgorithmSidebar = () => {
  const {
    boxList,
    openIds,
    setOpenIds,
    selectedId,
    setSelectedId,
    create,
    remove,
    rename,
    setBoxList,
  } = useTreeManager();

  useEffect(() => {
    const fetchDirectory = async () => {
      const list = await getDirectoryList({ containerId: 1 });
      const mapped = list.map((item) => ({
        id: `folder-${item.directoryId}`,
        directoryId: item.directoryId,
        title: item.directoryName,
        type: "folder" as const,
        parentId:
          item.directoryRoot === "root"
            ? null
            : `folder-${
                list.find((x) => x.directoryName === item.directoryRoot)
                  ?.directoryId ?? "unknown"
              }`,
      }));
      setBoxList(mapped);
    };

    fetchDirectory();
  }, []);

  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null);
  const [activeId, setActiveId] = useState<string | null>(null);

  const handleContextMenu = (id: string, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setSelectedId(id);
    setMenuPos({ x: e.clientX, y: e.clientY });
  };

  const toggleOpen = (id: string) => {
    setOpenIds((prev) =>
      prev.includes(id) ? prev.filter((v) => v !== id) : [...prev, id]
    );
  };

  useEffect(() => {
    const close = () => {
      setMenuPos(null);
      setSelectedId(null);
    };
    if (menuPos) document.addEventListener("click", close);
    return () => document.removeEventListener("click", close);
  }, [menuPos, setSelectedId]);

  return (
    <>
      <div className={`${styles.section} ${styles.topSection}`}>
        <h2 className={styles.heading}>Algorithm</h2>
        <div
          className={styles.boxArea}
          onContextMenu={(e) => handleContextMenu("", e)}
        >
          {menuPos && (
            <ContextMenu
              x={menuPos.x}
              y={menuPos.y}
              onClose={() => setMenuPos(null)}
              onCreate={async (type) => {
                const title = prompt(`${type} 이름을 입력하세요`);
                if (!title) return;

                const parent = boxList.find((b) => b.id === selectedId);

                if (type === "folder") {
                  try {
                    const res = await createDirectory({
                      containerId: 1,
                      teamId: 1,
                      directoryName: title,
                      directoryRoot: parent ? parent.title : "root",
                      directoryId: 0,
                    });

                    create("folder", res.directoryName, res.directoryId);
                  } catch (err) {
                    console.error("디렉터리 생성 실패:", err);
                  }
                } else {
                  // 파일은 백엔드 연동 없이 프론트 상태만 추가
                  create("file", title);
                }
              }}
              onRename={async (id) => {
                const item = boxList.find((b) => b.id === id);
                if (!item) return;

                const title = prompt("새 이름을 입력하세요", item.title);
                if (!title) return;

                try {
                  await renameDirectory({
                    directoryId: item.directoryId,
                    oldDirectoryName: item.title,
                    directoryName: title,
                  });
                  rename(id, title);
                } catch (err) {
                  console.error("디렉터리 이름 변경 실패:", err);
                }
              }}
              onDelete={async (id) => {
                const item = boxList.find((b) => b.id === id);
                if (!item) return;

                try {
                  await deleteDirectory({
                    directoryId: item.directoryId,
                    containerId: 1,
                    directoryRoot: item.parentId ?? "root",
                    directoryName: item.title,
                  });
                  remove(id);
                } catch (err) {
                  console.error("디렉터리 삭제 실패:", err);
                }
              }}
              selectedId={selectedId}
            />
          )}
          <div className={styles.boxList}>
            <TreeView
              boxList={boxList}
              openIds={openIds}
              selectedId={selectedId}
              activeId={activeId}
              setActiveId={setActiveId}
              setSelectedId={setSelectedId}
              onSelect={handleContextMenu}
              onToggle={toggleOpen}
            />
          </div>
        </div>
      </div>

      <div className={`${styles.section} ${styles.bottomSection}`}>
        <div className={styles.teamStatus}>
          <h3>팀원 현황(3 /5)</h3>
          <Bargraph name="user1" language="JS" success={4} total={4} />
          <Bargraph name="user2" language="JS" success={3} total={4} />
          <Bargraph name="user3" language="JS" success={2} total={4} />
          <Bargraph name="" language="" success={0} total={4} />
          <Bargraph name="" language="" success={0} total={4} />
        </div>
        <div className={styles.currentContainer}>
          <FaUsers className={styles.containerIcon} />
          <div className={styles.containerTexts}>
            <p className={styles.label}>현재 컨테이너</p>
            <p className={styles.name}>SOLID 컨테이너</p>
          </div>
        </div>
      </div>
    </>
  );
};

export default AlgorithmSidebar;
