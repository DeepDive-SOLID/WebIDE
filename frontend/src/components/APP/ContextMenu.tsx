import styles from "../../styles/ContextMenu.module.scss";

export interface ContextMenuProps {
  onClose: () => void;
  x: number;
  y: number;
  onCreate: (type: "file" | "folder") => void;
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
  // 이름 수정 핸들러
  const handleRename = () => {
    if (!selectedId) return;
    onRename(selectedId);
    onClose();
  };

  // 삭제 핸들러
  const handleDelete = () => {
    if (!selectedId) return;
    onDelete(selectedId);
    onClose();
  };

  return (
    <ul className={styles.contextMenu} style={{ left: x, top: y }}>
      <li onClick={() => onCreate("file")}>새 파일</li>
      <li onClick={() => onCreate("folder")}>새 폴더</li>

      <li onClick={handleRename}>이름 수정</li>
      <li onClick={handleDelete}>파일 삭제</li>
    </ul>
  );
};
export default ContextMenu;
