import ContextMenu from "../ContextMenu";
import Bargraph from "../../UI/Bargraph";
import { FaUsers } from "react-icons/fa";
import styles from "../../../styles/AppSidebar.module.scss";
import { useState, useEffect } from "react";
import { getDirectoryList, createDirectory, renameDirectory, deleteDirectory } from "../../../api/directoryApi";
import { getContainerProgress } from "../../../api/progressApi";
import { getContainerDetail } from "../../../api/homeApi";
import type { ProgressData } from "../../../types/progress";
import { IoIosArrowForward, IoIosArrowDown } from "react-icons/io";
import { CiFileOn } from "react-icons/ci";
import AddFileModal from "../AddFileModal";
import { useDispatch, useSelector } from "react-redux";
import { setDirectoryId, setRoot, setTeamId, setTtile } from "../../../stores/problemSlice";
import type { RootState } from "../../../stores";
import { deleteQuestion } from "../../../api/questionApi";

interface AlgorithmSidebarProps {
  containerId: number;
  onSelectQuestionId: (id: number) => void;
}

export type BoxItemType = {
  id: string;
  directoryId: number;
  type: "folder";
  title: string;
  parentId: string | null;
  isProblem?: boolean;
  teamId?: number;
  directoryRoot: string;
};

const AlgorithmSidebar = ({ containerId, onSelectQuestionId }: AlgorithmSidebarProps) => {
  const [boxList, setBoxList] = useState<BoxItemType[]>([]);
  const [openIds, setOpenIds] = useState<string[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null);
  const [activeId, setActiveId] = useState<string | null>(null);
  const [isAddFileModalOpen, setIsAddFileModalOpen] = useState(false);
  const [selectedFolder, setSelectedFolder] = useState<{
    directoryId: number;
    title: string;
  } | null>(null);

  const normalizePath = (path: string) => path.replace(/\/+/g, "/");
  const dispatch = useDispatch();
  const [progressData, setProgressData] = useState<ProgressData[]>([]);
  const [containerTeamId, setContainerTeamId] = useState<number | null>(null);
  const [containerName, setContainerName] = useState<string>('');
  const selectTeamId = useSelector((state: RootState) => state.problems.teamId);
  const questionId = useSelector((state: RootState) => state.problems.questionId);

  useEffect(() => {
    console.log(boxList);
    if (!boxList?.length) return;

    const problems = boxList.filter((item) => item.id === selectedId);
    const id = selectedId?.split("-")[1];
    dispatch(setRoot(problems[0]?.directoryRoot.split("//")[1]));
    dispatch(setDirectoryId(id));
    dispatch(setTtile(problems[0]?.title));
  }, [selectedId, dispatch, boxList]);

  // 컨테이너 정보 가져오기
  useEffect(() => {
    const fetchContainerInfo = async () => {
      try {
        const containerInfo = await getContainerDetail(containerId);
        setContainerTeamId(containerInfo.teamId);
        setContainerName(containerInfo.containerName);
      } catch (error) {
        console.error('Failed to fetch container info:', error);
      }
    };

    fetchContainerInfo();
  }, [containerId]);

  useEffect(() => {
    console.log(1);
    const fetchDirectory = async () => {
      let list = await getDirectoryList({ containerId });
      const data = await getContainerDetail(containerId);
      dispatch(setTeamId(data.teamId));

      if (list.length === 0 && containerTeamId !== null) {
        await createDirectory({
          containerId,
          teamId: containerTeamId,
          directoryName: "root",
          directoryRoot: "/",
          directoryId: 0,
        });
        list = await getDirectoryList({ containerId });
      }

      const filtered = list.filter((item) => item.containerId === containerId);

      const mapped = filtered.map((item) => {
        const parent = filtered.find((x) => normalizePath(`${x.directoryRoot === "/" ? "" : x.directoryRoot}/${x.directoryName}`) === normalizePath(item.directoryRoot));

        return {
          id: `folder-${item.directoryId}`,
          directoryId: item.directoryId,
          title: item.directoryName,
          type: "folder" as const,
          parentId: parent ? `folder-${parent.directoryId}` : null,
          teamId: item.teamId,
          directoryRoot: item.directoryRoot,
        };
      });

      setBoxList(mapped);
    };

    if (containerTeamId !== null) {
      fetchDirectory();
    }
  }, [containerId, containerTeamId]);

  // 진행률 데이터 가져오기
  useEffect(() => {
    const fetchProgress = async () => {
      try {
        const progressResponse = await getContainerProgress(containerId);
        if (progressResponse) {
          // ProgressResponse는 ProgressData[] 타입이므로 직접 사용
          const progressData = Array.isArray(progressResponse) ? progressResponse : [];
          setProgressData(progressData);
        }
      } catch (error) {
        console.error('Failed to fetch progress:', error);
        setProgressData([]);
      }
    };

    fetchProgress();
  }, [containerId]);

  const create = (title: string, directoryId: number, parentId: string | null, isProblem: boolean = false, directoryRoot: string) => {
    const id = `folder-${directoryId}`;

    setBoxList((prev) => [
      ...prev,
      {
        id,
        directoryId,
        type: "folder",
        title,
        parentId,
        isProblem,
        teamId: selectTeamId,
        directoryRoot,
      },
    ]);

    if (parentId && !openIds.includes(parentId)) {
      setOpenIds((prev) => [...prev, parentId]);
    }
  };

  const remove = (id: string) => {
    setBoxList((prev) => {
      const toDelete = [id];
      const collectChildren = (pid: string) => {
        prev.forEach((item) => {
          if (item.parentId === pid) {
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

  const rename = (id: string, newTitle: string) => {
    setBoxList((prev) => prev.map((box) => (box.id === id ? { ...box, title: newTitle } : box)));
  };

  const handleContextMenu = (id: string, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setSelectedId(id);
    setMenuPos({ x: e.clientX, y: e.clientY });
  };

  const toggleOpen = (id: string) => {
    setOpenIds((prev) => (prev.includes(id) ? prev.filter((v) => v !== id) : [...prev, id]));
  };

  useEffect(() => {
    const close = () => {
      setMenuPos(null);
      setSelectedId(null);
    };
    if (menuPos) document.addEventListener("click", close);
    return () => document.removeEventListener("click", close);
  }, [menuPos]);

  const renderTree = (parentId: string | null): JSX.Element[] => {
    return boxList
      .filter((item) => item.parentId === parentId)
      .map((item) => (
        <div key={item.id} className={styles.treeNode}>
          <div
            className={`${styles.treeItem} ${item.isProblem ? styles.file : styles.folder} ${selectedId === item.id ? styles.selected : ""} ${activeId === item.id ? styles.treeItemActive : ""}`}
            onClick={() => {
              setActiveId(item.id);
              setSelectedId(item.id);
              if (!item.isProblem) toggleOpen(item.id);
            }}
            onContextMenu={(e) => handleContextMenu(item.id, e)}
          >
            <span className={styles.treeLabel}>
              {item.isProblem ? (
                <CiFileOn className={styles.treeIcon} />
              ) : openIds.includes(item.id) ? (
                <IoIosArrowDown className={styles.treeArrow} />
              ) : (
                <IoIosArrowForward className={styles.treeArrow} />
              )}
              <span className={styles.treeTitle}>{item.title}</span>
            </span>
          </div>
          {item.type === "folder" && openIds.includes(item.id) && renderTree(item.id)}
        </div>
      ));
  };

  return (
    <>
      <div className={`${styles.section} ${styles.topSection}`}>
        <h2 className={styles.heading}>Algorithm</h2>
        <div className={styles.boxArea} onContextMenu={(e) => handleContextMenu("", e)}>
          {menuPos && (
            <ContextMenu
              x={menuPos.x}
              y={menuPos.y}
              onClose={() => setMenuPos(null)}
              selectedId={selectedId}
              onCreate={async (type) => {
                const parent = boxList.find((b) => b.id === selectedId);
                const directoryRoot = parent ? normalizePath(`${parent.directoryRoot}/${parent.title}`) : "/";
                const teamId = parent?.teamId ?? boxList[0]?.teamId ?? selectTeamId ?? containerTeamId ?? 1;
                const parentId = parent?.id ?? null;

                if (type === "folder") {
                  const title = prompt("폴더 이름을 입력하세요");
                  if (!title) return;

                  try {
                    const res = await createDirectory({
                      containerId: containerId,
                      teamId: teamId,
                      directoryName: title,
                      directoryRoot,
                      directoryId: 0,
                    });

                    create(title, res.directoryId, parentId, false, directoryRoot);
                  } catch (err) {
                    console.error("디렉터리 생성 실패:", err);
                  }
                } else {
                  if (!parent) {
                    alert("파일을 생성할 폴더를 먼저 선택해주세요.");
                    return;
                  }

                  setSelectedFolder({
                    directoryId: parent.directoryId,
                    title: "",
                  });
                  setIsAddFileModalOpen(true);
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
                  // questionId가 있을 때만 deleteQuestion 호출
                  if (questionId) {
                    console.log("Deleting question:", questionId);
                    await deleteQuestion(questionId);
                  }
                  
                  await deleteDirectory({
                    directoryId: item.directoryId,
                    containerId,
                    directoryRoot: item.directoryRoot,
                    directoryName: item.title,
                  });

                  remove(id);
                } catch (err) {
                  console.error("디렉터리 삭제 실패:", err);
                }
              }}
            />
          )}
          <div className={styles.boxList}>{renderTree(null)}</div>
        </div>
      </div>

      <div className={`${styles.section} ${styles.bottomSection}`}>
        <div className={styles.teamStatus}>
          <h3>팀원 현황({progressData.length} / 5)</h3>
          {progressData.map((member, index) => (
            <Bargraph
              key={member.memberId || index}
              name={member.memberName || member.memberId || ''}
              language='JS'
              success={member.averageProgress || 0}
              total={100}
            />
          ))}
          {/* 빈 슬롯 채우기 */}
          {[...Array(Math.max(0, 5 - progressData.length))].map((_, index) => (
            <Bargraph
              key={`empty-${index}`}
              name=''
              language=''
              success={0}
              total={100}
            />
          ))}
        </div>
        <div className={styles.currentContainer}>
          <FaUsers className={styles.containerIcon} />
          <div className={styles.containerTexts}>
            <p className={styles.label}>현재 컨테이너</p>
            <p className={styles.name}>{containerName}</p>
          </div>
        </div>
      </div>

      {isAddFileModalOpen && selectedFolder && (
        <AddFileModal
          onClose={() => {
            setIsAddFileModalOpen(false);
            setSelectedFolder(null);
          }}
          directoryId={selectedFolder.directoryId}
          onCreateComplete={(newFile) => {
            const parent = boxList.find((b) => b.id === selectedId);
            const directoryRoot = parent ? normalizePath(`${parent.directoryRoot}/${parent.title}`) : "/";
            create(newFile.title, newFile.directoryId, selectedId, true, directoryRoot);
          }}
          selectedId={selectedId}
          boxList={boxList}
          create={(t, d, s, b, root) => create(t, d, s, b, root)}
          normalizePath={normalizePath}
          containerId={containerId}
        />
      )}
    </>
  );
};

export default AlgorithmSidebar;
