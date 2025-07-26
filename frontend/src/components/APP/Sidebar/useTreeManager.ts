import { useState } from "react";

export type BoxItemType = {
  id: string; // "folder-1" 형식의 문자열 id (뷰를 위한 유니크 키)
  directoryId: number; // 실제 백엔드 디렉터리 ID
  type: "folder" | "file";
  title: string;
  parentId: string | null; // 상위 폴더의 id ("folder-1" 같은)
};

export const useTreeManager = () => {
  const [boxList, setBoxList] = useState<BoxItemType[]>([]);
  const [openIds, setOpenIds] = useState<string[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);

  // 새 폴더/파일 생성
  const create = (
    type: "folder" | "file",
    title: string,
    directoryId: number
  ) => {
    const id = `${type}-${directoryId}`;
    const parent =
      selectedId &&
      boxList.find((box) => box.id === selectedId && box.type === "folder");
    const parentId = parent ? parent.id : null;

    setBoxList((prev) => [...prev, { id, directoryId, type, title, parentId }]);
    if (parentId && !openIds.includes(parentId)) {
      setOpenIds((prev) => [...prev, parentId]);
    }
  };

  // 아이템(및 하위 아이템들) 삭제
  const remove = (id: string) => {
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

  // 이름 변경
  const rename = (id: string, newTitle: string) => {
    setBoxList((prev) =>
      prev.map((box) => (box.id === id ? { ...box, title: newTitle } : box))
    );
  };

  return {
    boxList,
    setBoxList,
    openIds,
    setOpenIds,
    selectedId,
    setSelectedId,
    create,
    remove,
    rename,
  };
};
