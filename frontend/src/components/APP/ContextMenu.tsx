import styles from "../../styles/ContextMenu.module.scss";

export interface ContextMenuProps {
  onClose: () => void;
  x: number;
  y: number;
  onCreate: (type: "file" | "folder", title: string) => void;
  selectedId: string | null;
  onRename: (id: string) => void;
  onDelete: (id: string) => void;
}

const ContextMenu = ({
  onClose,
  x,
  y,
  onCreate,
  selectedId,
  onRename,
  onDelete,
}: ContextMenuProps) => {
  const handleClick = async (type: "file" | "folder") => {
    const title = prompt(
      `${type === "file" ? "파일" : "폴더"} 이름을 입력하세요`
    );
    if (title) {
      onCreate(type, title);
    }
    onClose();
  };

  const handleRename = () => {
    if (!selectedId) return;
    onRename(selectedId);
    onClose();
  };

  const handleDelete = () => {
    if (!selectedId) return;
    onDelete(selectedId);
    onClose();
  };

  return (
    <ul className={styles.contextMenu} style={{ left: x, top: y }}>
      <li onClick={() => handleClick("file")}>새 파일</li>
      <li onClick={() => handleClick("folder")}>새 폴더</li>
      <li onClick={() => {}}>문제 수정</li>
      <li onClick={handleRename}>이름 수정</li>
      <li onClick={handleDelete}>파일 삭제</li>
    </ul>
  );
};
export default ContextMenu;
