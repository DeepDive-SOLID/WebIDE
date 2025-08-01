import React from "react";
import type { BoxItemType } from "./useTreeManager";
import { IoIosArrowForward, IoIosArrowDown } from "react-icons/io";
import { CiFileOn } from "react-icons/ci";
import styles from "../../../styles/AppSidebar.module.scss";

interface TreeViewProps {
  boxList: BoxItemType[];
  openIds: string[];
  selectedId: string | null;
  activeId: string | null;
  setActiveId: (id: string) => void;
  setSelectedId: (id: string) => void;
  onSelect: (id: string, e: React.MouseEvent) => void;
  onToggle: (id: string) => void;
}

const TreeView = ({
  boxList,
  openIds,
  selectedId,
  activeId,
  onSelect,
  onToggle,
  setActiveId,
  setSelectedId,
}: TreeViewProps) => {
  const render = (parentId: string | null) => {
    return boxList
      .filter((item) => item.parentId === parentId)
      .map((item) => (
        <div key={item.id} className={styles.treeNode}>
          <div
            className={`${styles.treeItem} ${
              item.type === "folder" ? styles.folder : styles.file
            } ${selectedId === item.id ? styles.selected : ""} ${
              activeId === item.id ? styles.treeItemActive : ""
            }`}
            onClick={() => {
              setActiveId(item.id);
              setSelectedId(item.id);
              if (item.type === "folder") onToggle(item.id);
            }}
            onContextMenu={(e) => onSelect(item.id, e)}
          >
            {item.type === "folder" ? (
              <span className={styles.treeLabel}>
                {openIds.includes(item.id) ? (
                  <IoIosArrowDown className={styles.treeArrow} />
                ) : (
                  <IoIosArrowForward className={styles.treeArrow} />
                )}
                <span className={styles.treeTitle}>{item.title}</span>
              </span>
            ) : (
              <span className={styles.treeLabel}>
                <CiFileOn className={styles.treeIcon} />
                <span className={styles.treeTitle}>{item.title}</span>
              </span>
            )}
          </div>
          {item.type === "folder" &&
            openIds.includes(item.id) &&
            render(item.id)}
        </div>
      ));
  };

  return <>{render(null)}</>;
};

export default TreeView;
