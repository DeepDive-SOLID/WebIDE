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
import { createCodeFile, getCodeFileList } from "../../../api/codefileApi";
import { IoIosArrowForward, IoIosArrowDown } from "react-icons/io";
import { CiFileOn } from "react-icons/ci";
import AddFileModal from "../AddFileModal";

interface AlgorithmSidebarProps {
  containerId: number;
}

// 타입 정의: 각 박스 아이템은 폴더 혹은 파일이며, 뷰 렌더링 및 식별을 위한 id를 가짐
export type BoxItemType = {
  id: string;
  directoryId: number;
  type: "folder" | "file";
  title: string;
  parentId: string | null;
};

const AlgorithmSidebar = ({ containerId }: AlgorithmSidebarProps) => {
  // 상태 관리 변수들 정의
  const [boxList, setBoxList] = useState<BoxItemType[]>([]); // 전체 트리 데이터
  const [openIds, setOpenIds] = useState<string[]>([]); // 열려있는 폴더 id 목록
  const [selectedId, setSelectedId] = useState<string | null>(null); // 선택된 항목 id

  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null); // 컨텍스트 메뉴 위치
  const [activeId, setActiveId] = useState<string | null>(null); // 클릭된 항목 id
  const [isAddFileModalOpen, setIsAddFileModalOpen] = useState(false); // 파일 생성 모달 상태
  const [selectedFolder, setSelectedFolder] = useState<{
    directoryId: number;
    title: string;
  } | null>(null); // 파일 생성 시 선택된 폴더 정보

  // 마운트 시 디렉터리 목록 불러오기
  useEffect(() => {
    const fetchDirectory = async () => {
      let list = await getDirectoryList({ containerId });

      if (list.length === 0) {
        await createDirectory({
          containerId,
          teamId: myTeamId,
          directoryName: "root",
          directoryRoot: "/",
          directoryId: 0,
        });

        list = await getDirectoryList({ containerId });
      }

      const filtered = list.filter((item) => item.containerId === containerId);

      const mappedFolders = filtered.map((item) => {
        const fullParentPath =
          item.directoryRoot === "/" || item.directoryRoot === "root"
            ? null
            : filtered.find(
                (x) =>
                  `${
                    x.directoryRoot === "/" || x.directoryRoot === "root"
                      ? ""
                      : x.directoryRoot
                  }/${x.directoryName}`.replaceAll("//", "/") ===
                  item.directoryRoot
              )?.directoryId;

        return {
          id: `folder-${item.directoryId}`,
          directoryId: item.directoryId,
          title: item.directoryName,
          type: "folder" as const,
          parentId: fullParentPath ? `folder-${fullParentPath}` : null,
          teamId: item.teamId,
        };
      });

      // 코드 파일 불러오기
      const codeFileList = await getCodeFileList();

      const mappedFiles = codeFileList
        .filter((file) =>
          filtered.find((dir) => dir.directoryId === file.directoryId)
        )
        .map((file) => ({
          id: `file-${file.codeFileId}`,
          directoryId: file.directoryId,
          title: file.codeFileName,
          type: "file" as const,
          parentId: `folder-${file.directoryId}`,
        }));

      setBoxList([...mappedFolders, ...mappedFiles]);
    };

    fetchDirectory();
  }, [containerId]);

  // 새 항목(파일/폴더) 추가 함수
  const create = (
    type: "folder" | "file",
    title: string,
    directoryId: number,
    parentIdOverride?: string
  ) => {
    const id = `${type}-${directoryId}`;
    const parentId =
      parentIdOverride ??
      (selectedId &&
        boxList.find((box) => box.id === selectedId && box.type === "folder")
          ?.id) ??
      null;

    setBoxList((prev) => [...prev, { id, directoryId, type, title, parentId }]);
    if (parentId && !openIds.includes(parentId)) {
      setOpenIds((prev) => [...prev, parentId]);
    }
  };

  // 항목 삭제 함수 (재귀적으로 하위 항목도 삭제)
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

  // 항목 이름 변경 함수
  const rename = (id: string, newTitle: string) => {
    setBoxList((prev) =>
      prev.map((box) => (box.id === id ? { ...box, title: newTitle } : box))
    );
  };

  // 우클릭 메뉴 열기 처리
  const handleContextMenu = (id: string, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setSelectedId(id);
    setMenuPos({ x: e.clientX, y: e.clientY });
  };

  // 폴더 열기/닫기 토글 처리
  const toggleOpen = (id: string) => {
    setOpenIds((prev) =>
      prev.includes(id) ? prev.filter((v) => v !== id) : [...prev, id]
    );
  };

  // 외부 클릭 시 컨텍스트 메뉴 닫기
  useEffect(() => {
    const close = () => {
      setMenuPos(null);
      setSelectedId(null);
    };
    if (menuPos) document.addEventListener("click", close);
    return () => document.removeEventListener("click", close);
  }, [menuPos, setSelectedId]);

  // 트리 렌더링 함수 (재귀적)
  const renderTree = (parentId: string | null) => {
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
              if (item.type === "folder") toggleOpen(item.id);
            }}
            onContextMenu={(e) => handleContextMenu(item.id, e)}
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
            renderTree(item.id)}
        </div>
      ));
  };

  return (
    <>
      {/* 상단 트리 영역 */}
      <div className={`${styles.section} ${styles.topSection}`}>
        <h2 className={styles.heading}>Algorithm</h2>
        <div
          className={styles.boxArea}
          onContextMenu={(e) => handleContextMenu("", e)}
        >
          {/* 우클릭 메뉴 */}
          {menuPos && (
            <ContextMenu
              x={menuPos.x}
              y={menuPos.y}
              onClose={() => setMenuPos(null)}
              onCreate={async (type) => {
                const parent = boxList.find((b) => b.id === selectedId);
                const directoryRoot = parent ? `${parent.title}` : "/";
                const teamId = parent?.teamId ?? boxList[0]?.teamId ?? 0;
                const parentId = parent?.id ?? null;

                if (type === "folder") {
                  const title = prompt(`${type} 이름을 입력하세요`);
                  if (!title) return;

                  try {
                    const res = await createDirectory({
                      containerId,
                      teamId,
                      directoryName: title,
                      directoryRoot,
                      directoryId: 0,
                    });

                    create(
                      "folder",
                      res.directoryName,
                      res.directoryId,
                      parentId
                    );
                  } catch (err) {
                    console.error("디렉터리 생성 실패:", err);
                  }
                } else {
                  if (!parent) {
                    alert("파일을 생성할 폴더를 먼저 선택해주세요.");
                    return;
                  }

                  const fileName = prompt(
                    "파일 이름을 확장자까지 입력하세요 (예: solution.py)"
                  );
                  if (!fileName || !fileName.includes(".")) {
                    alert("파일 이름에 확장자를 포함해서 입력해주세요.");
                    return;
                  }

                  const trimmed = fileName.trim();

                  try {
                    await createCodeFile({
                      directoryId: parent.directoryId,
                      codeFileName: trimmed,
                      codeContent: "",
                    });

                    create("file", trimmed, parent.directoryId, parent.id);

                    // 모달 열기 - 생성된 파일명을 전달해 추가 설정 수행
                    setSelectedFolder({
                      directoryId: parent.directoryId,
                      title: trimmed,
                    });
                    setIsAddFileModalOpen(true);
                  } catch (err) {
                    console.error("파일 생성 실패:", err);
                    alert("파일 생성에 실패했습니다.");
                  }
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
                    directoryRoot: item.parentId
                      ? boxList.find((b) => b.id === item.parentId)?.title ??
                        "root"
                      : "root",

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
          <div className={styles.boxList}>{renderTree(null)}</div>
        </div>
      </div>

      {/* 하단 팀원 및 컨테이너 정보 */}
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
            <p className={styles.name}>SOLID</p>
          </div>
        </div>
      </div>

      {/* 파일 생성 후 추가 설정용 모달 */}
      {isAddFileModalOpen && selectedFolder && (
        <AddFileModal
          onClose={() => {
            setIsAddFileModalOpen(false);
            setSelectedFolder(null);
          }}
          directoryId={selectedFolder.directoryId}
          onCreateComplete={(newFile) => {
            create(
              "file",
              newFile.title,
              newFile.directoryId,
              `folder-${newFile.directoryId}`
            );
          }}
        />
      )}
    </>
  );
};

export default AlgorithmSidebar;
