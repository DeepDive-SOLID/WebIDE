import { useTreeManager } from "./useTreeManager";

import TreeView from "./TreeView";
import ContextMenu from "../ContextMenu";
import Bargraph from "../../UI/Bargraph";
import { FaUsers } from "react-icons/fa";
import styles from "../../../styles/AppSidebar.module.scss";
import { useState, useEffect } from "react";

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
  } = useTreeManager();

  // 우클릭 메뉴 위치와 현재 활성 항목
  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null);
  const [activeId, setActiveId] = useState<string | null>(null);

  // 우클릭 메뉴 열기
  const handleContextMenu = (id: string, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setSelectedId(id);
    setMenuPos({ x: e.clientX, y: e.clientY });
  };

  // 폴더 열기/닫기 토글
  const toggleOpen = (id: string) => {
    setOpenIds((prev) =>
      prev.includes(id) ? prev.filter((v) => v !== id) : [...prev, id]
    );
  };

  // 우클릭 메뉴 외부 클릭 시 닫기
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
              onCreate={(type) => {
                const title = prompt(`${type} 이름을 입력하세요`);
                if (title) create(type, title);
              }}
              onRename={(id) => {
                const title = prompt("새 이름을 입력하세요");
                if (title) rename(id, title);
              }}
              onDelete={(id) => remove(id)}
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
