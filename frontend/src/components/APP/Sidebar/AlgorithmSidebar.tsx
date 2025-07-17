import Box from "../../UI/Box";
import Bargraph from "../../UI/Bargraph";
import ContextMenu from "../ContextMenu";
import { IoListOutline } from "react-icons/io5";
import { CiFileOn } from "react-icons/ci";
import { FaUsers } from "react-icons/fa";
import React, { useState } from "react";

type BoxItem = {
  id: string;
  type: "folder" | "file";
  title: string;
};

const AlgorithmSidebar = () => {
  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null);
  const [boxList, setBoxList] = useState<BoxItem[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);

  const handleCreate = (type: "folder" | "file", title: string) => {
    const id = `${type}-${Date.now()}-${Math.random()}`;
    setBoxList((prev) => [...prev, { id, type, title }]);
  };

  const handleContextMenu = (e: React.MouseEvent) => {
    e.preventDefault();
    setMenuPos({ x: e.clientX, y: e.clientY });
  };

  const closeMenu = () => {
    setMenuPos(null);
  };

  return (
    <>
      <div className="section top-section">
        <h2>Algorithm</h2>
        <div className="box-area" onContextMenu={handleContextMenu}>
          {menuPos && (
            <ContextMenu
              x={menuPos.x}
              y={menuPos.y}
              onClose={closeMenu}
              onCreate={handleCreate}
              selectedId={selectedId}
              onRename={(id) => {
                const newTitle = prompt("새 이름을 입력하세요");
                if (newTitle) {
                  setBoxList((prev) =>
                    prev.map((box) =>
                      box.id === id ? { ...box, title: newTitle } : box
                    )
                  );
                }
              }}
              onDelete={(id) => {
                setBoxList((prev) => prev.filter((box) => box.id !== id));
              }}
            />
          )}
          <div className="box-list">
            {boxList.map((box) => (
              <Box
                key={box.id}
                icon={
                  box.type === "folder" ? (
                    <IoListOutline size={20} />
                  ) : (
                    <CiFileOn size={20} />
                  )
                }
                title={box.title}
                onClick={() => {}}
                onContextMenu={(e) => {
                  e.preventDefault();
                  setSelectedId(box.id);
                  setMenuPos({ x: e.clientX, y: e.clientY });
                }}
                className={box.type === "folder" ? "box-primary" : ""}
              />
            ))}
          </div>
        </div>
      </div>

      <div className="section bottom-section">
        <div className="team-status">
          <h3>팀원 현황(3 /5)</h3>
          <Bargraph name="user1" language="JS" success={4} total={4} />
          <Bargraph name="user2" language="JS" success={3} total={4} />
          <Bargraph name="user3" language="JS" success={2} total={4} />
          <Bargraph name="" language="" success={0} total={4} />
          <Bargraph name="" language="" success={0} total={4} />
        </div>

        <div className="current-container">
          <FaUsers className="container-icon" />
          <div className="container-texts">
            <p className="label">현재 컨테이너</p>
            <p className="name">SOLID 컨테이너</p>
          </div>
        </div>
      </div>
    </>
  );
};

export default AlgorithmSidebar;
