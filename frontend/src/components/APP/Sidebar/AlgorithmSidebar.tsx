import Bargraph from "../../UI/Bargraph";
import ContextMenu from "../ContextMenu";
import { FaUsers } from "react-icons/fa";
import React, { useEffect, useState } from "react";
import { IoIosArrowForward, IoIosArrowDown } from "react-icons/io";
import { CiFileOn } from "react-icons/ci";

type BoxItemType = {
  id: string;
  type: "folder" | "file";
  title: string;
  parentId: string | null;
};

const AlgorithmSidebar = () => {
  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null);
  const [boxList, setBoxList] = useState<BoxItemType[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [openIds, setOpenIds] = useState<string[]>([]);
  const [activeId, setActiveId] = useState<string | null>(null);

  const handleCreate = (type: "folder" | "file", title: string) => {
    const id = `${type}-${Date.now()}-${Math.random()}`;
    const parent =
      selectedId &&
      boxList.find((box) => box.id === selectedId && box.type === "folder");
    const parentId = parent ? parent.id : null;

    setBoxList((prev) => [...prev, { id, type, title, parentId }]);
    if (parentId && !openIds.includes(parentId)) {
      setOpenIds((prev) => [...prev, parentId]);
    }
  };

  const handleContextMenu = (e: React.MouseEvent) => {
    e.preventDefault();
    setSelectedId(null);
    setMenuPos({ x: e.clientX, y: e.clientY });
  };

  const closeMenu = () => {
    setMenuPos(null);
  };

  const toggleOpen = (id: string) => {
    setOpenIds((prev) =>
      prev.includes(id) ? prev.filter((v) => v !== id) : [...prev, id]
    );
  };

  const deleteRecursive = (id: string) => {
    setBoxList((prev) => {
      const toDelete = [id];
      const collectChildren = (parentId: string) => {
        prev.forEach((item) => {
          if (item.parentId === parentId) {
            toDelete.push(item.id);
            if (item.type === "folder") collectChildren(item.id);
          }
        });
      };
      collectChildren(id);
      return prev.filter((item) => !toDelete.includes(item.id));
    });
    setSelectedId(null);
  };

  const renderTree = (items: BoxItemType[], parentId: string | null) => {
    return items
      .filter((item) => item.parentId === parentId)
      .map((item) => (
        <div key={item.id} className="tree-node">
          <div
            className={`tree-item ${item.type} ${
              selectedId === item.id ? "selected" : ""
            } ${activeId === item.id ? "active" : ""}`}
            onClick={() => {
              setActiveId(item.id);
              if (item.type === "folder") {
                toggleOpen(item.id);
              }
            }}
            onContextMenu={(e) => {
              e.preventDefault();
              e.stopPropagation();
              setSelectedId(item.id);
              setMenuPos({ x: e.clientX, y: e.clientY });
            }}
          >
            {item.type === "folder" ? (
              <span className="tree-label">
                {openIds.includes(item.id) ? (
                  <IoIosArrowDown className="tree-arrow" />
                ) : (
                  <IoIosArrowForward className="tree-arrow" />
                )}
                <span className="tree-title">{item.title}</span>
              </span>
            ) : (
              <span className="tree-label">
                <CiFileOn className="tree-icon" />
                <span className="tree-title">{item.title}</span>
              </span>
            )}
          </div>

          {item.type === "folder" &&
            openIds.includes(item.id) &&
            renderTree(items, item.id)}
        </div>
      ));
  };

  useEffect(() => {
    const handleClickOutside = () => {
      setMenuPos(null);
      setSelectedId(null);
    };

    if (menuPos) {
      document.addEventListener("click", handleClickOutside);
    }

    return () => {
      document.removeEventListener("click", handleClickOutside);
    };
  }, [menuPos]);

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
              onCreate={(type) => {
                const title = prompt(
                  `${type === "file" ? "파일" : "폴더"} 이름을 입력하세요`,
                  ""
                );
                if (title && title.trim() !== "") {
                  handleCreate(type, title.trim());
                }
                closeMenu();
              }}
              onRename={(id) => {
                const current = boxList.find((box) => box.id === id);
                const newTitle = prompt(
                  "새 이름을 입력하세요",
                  current?.title ?? ""
                );
                if (newTitle && newTitle.trim() !== "") {
                  setBoxList((prev) =>
                    prev.map((box) =>
                      box.id === id ? { ...box, title: newTitle.trim() } : box
                    )
                  );
                }
                closeMenu();
              }}
              onDelete={(id) => {
                deleteRecursive(id);
                closeMenu();
              }}
              selectedId={selectedId}
            />
          )}
          <div className="box-list">{renderTree(boxList, null)}</div>
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
