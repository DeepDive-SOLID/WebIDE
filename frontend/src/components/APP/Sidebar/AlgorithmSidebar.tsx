import ContextMenu from "../ContextMenu";
import Bargraph from "../../UI/Bargraph";
import { FaUsers } from "react-icons/fa";
import styles from "../../../styles/AppSidebar.module.scss";
import { useState, useEffect } from "react";
import { getDirectoryList, createDirectory, renameDirectory, deleteDirectory } from "../../../api/directoryApi";
import { getContainerProgress, getDirectoryProgress, getQuestionProgressByMember } from "../../../api/progressApi";
import { getQuestionListByContainerId } from "../../../api/questionApi";
import { getContainerDetail } from "../../../api/homeApi";
import type { ProgressData } from "../../../types/progress";
import { IoIosArrowForward, IoIosArrowDown } from "react-icons/io";
import { CiFileOn } from "react-icons/ci";
import AddFileModal from "../AddFileModal";
import { useDispatch, useSelector } from "react-redux";
import { setDirectoryId, setRoot, setTeamId, setTtile } from "../../../stores/problemSlice";
import { resetProgressRefresh } from "../../../stores/progressSlice";
import type { RootState } from "../../../stores";
import { deleteQuestion } from "../../../api/questionApi";
import { getCurrentMemberId } from "../../../utils/auth";

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
  const loginId = getCurrentMemberId();
  const [containerOwner, setContainerOwner] = useState<string>("");

  const normalizePath = (path: string) => path.replace(/\/+/g, "/");
  const dispatch = useDispatch();

  // 문제 디렉터리인지 확인하는 함수
  const isProblemDirectory = (directoryName: string): boolean => {
    // 제외 패턴 (주차, 알고리즘 폴더 등)
    const excludePattern = /(주차|week|Week|root|알고리즘|Algorithm|단원|Chapter)/i;
    if (excludePattern.test(directoryName)) {
      return false;
    }
    
    // 문제 패턴
    const problemPatterns = [
      /^[A-Za-z][\+\-\*\/][A-Za-z]$/,  // A+B, a+b 등
      /^[0-9]+[_\-\s].*/,               // 1_문제, 1-문제
      /^\[.*\].*/,                      // [태그]문제
    ];
    
    // 패턴 매칭 또는 짧고 공백 없는 이름
    return problemPatterns.some(pattern => pattern.test(directoryName)) || 
           (!directoryName.includes(' ') && directoryName.length < 20);
  };

  const [progressData, setProgressData] = useState<ProgressData[]>([]);
  const [containerTeamId, setContainerTeamId] = useState<number | null>(null);
  const [containerName, setContainerName] = useState<string>('');
  const selectTeamId = useSelector((state: RootState) => state.problems.teamId);
  const questionId = useSelector((state: RootState) => state.problems.questionId);
  const shouldRefreshProgress = useSelector((state: RootState) => state.progress.shouldRefresh);
  const [directoryProgress, setDirectoryProgress] = useState<{[key: string]: number}>({});
  const [selectedDirectoryProgress, setSelectedDirectoryProgress] = useState<ProgressData[]>([]);
  const [questionProgress, setQuestionProgress] = useState<{[key: number]: number}>({});
  const [questionMap, setQuestionMap] = useState<{[key: string]: number}>({});
  const [directoryQuestionMap, setDirectoryQuestionMap] = useState<{[key: string]: {[key: string]: number}}>({});

  useEffect(() => {
    // console.log(boxList);
    if (!boxList?.length) return;

    const problems = boxList.filter((item) => item.id === selectedId);
    const id = selectedId?.split("-")[1];
    dispatch(setRoot(problems[0]?.directoryRoot.split("//")[1]));
    dispatch(setDirectoryId(id));
    dispatch(setTtile(problems[0]?.title));

    // 폴더가 선택되었을 때 해당 디렉토리의 문제별 진행률 조회 (root 제외)
    if (id && problems[0] && problems[0].title !== "root") {
      // 현재 사용자의 진행률
      if (loginId) {
        getDirectoryProgress(parseInt(id), loginId)
          .then(data => {
            // console.log('Directory progress:', data);
            // 디렉토리별 진행률 저장
            setDirectoryProgress(prev => ({
              ...prev,
              [id]: data.progressPercentage || 0
            }));
          })
          .catch(err => console.error('Failed to fetch directory progress:', err));
      }
      
      // 선택된 디렉토리의 모든 팀원 진행률 조회
      fetch(`/progress/directory/${id}`)
        .then(res => res.json())
        .then(data => {
          // console.log('Directory team progress:', data);
          // data가 배열인지 확인하고 처리
          if (Array.isArray(data)) {
            const formattedData = data.map((item: any) => ({
              memberId: item.memberId,
              memberName: item.memberName,
              averageProgress: item.progressComplete || 0
            }));
            setSelectedDirectoryProgress(formattedData);
          } else {
            console.error('Expected array but got:', data);
            setSelectedDirectoryProgress([]);
          }
        })
        .catch(err => console.error('Failed to fetch directory team progress:', err));
    }
  }, [selectedId, dispatch, boxList, loginId]);

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
    // console.log(1);
    const fetchDirectory = async () => {
      let list = await getDirectoryList({ containerId });
      const data = await getContainerDetail(containerId);
      setContainerOwner(data.ownerId);
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
      
      // 문제 디렉토리의 진행률만 가져오기
      if (loginId) {
        mapped.forEach(async (item) => {
          if (isProblemDirectory(item.title)) {
            try {
              const data = await getDirectoryProgress(item.directoryId, loginId);
              setDirectoryProgress(prev => ({
                ...prev,
                [item.directoryId]: data.progressPercentage || 0
              }));
            } catch (err) {
              console.error('Failed to fetch progress for directory:', item.directoryId, err);
            }
          }
        });
      }
    };

    if (containerTeamId !== null) {
      fetchDirectory();
    }
  }, [containerId, containerTeamId, loginId]);

  // 진행률 데이터 가져오기
  useEffect(() => {
    const fetchProgress = async () => {
      try {
        const progressResponse = await getContainerProgress(containerId);
        console.log('Container Progress Response:', progressResponse);
        console.log('Response type:', typeof progressResponse);
        console.log('Is array:', Array.isArray(progressResponse));
        
        if (progressResponse) {
          // ProgressResponse는 ProgressData[] 타입이므로 직접 사용
          const progressData = Array.isArray(progressResponse) ? progressResponse : [];
          
          // 각 멤버의 언어 정보 확인
          progressData.forEach((member, index) => {
            console.log(`Member ${index}: ID=${member.memberId}, Name=${member.memberName}, Language=${member.language}, Progress=${member.averageProgress}%, Problems=${member.directoryCount}`);
          });
          
          setProgressData(progressData);
        }
      } catch (error) {
        console.error('Failed to fetch progress:', error);
        setProgressData([]);
      }
    };

    fetchProgress();
  }, [containerId]);

  // 문제별 진행률 가져오기
  useEffect(() => {
    const fetchQuestionProgress = async () => {
      if (!loginId || !containerId) return;
      
      try {
        const questionProgressData = await getQuestionProgressByMember(containerId, loginId);
        if (questionProgressData && Array.isArray(questionProgressData)) {
          const progressMap: {[key: number]: number} = {};
          const titleMap: {[key: string]: number} = {};
          const dirQuestionMap: {[key: string]: {[key: string]: number}} = {};
          
          // 먼저 boxList를 순회하여 디렉토리 구조 파악
          const questionToDirectory: {[key: string]: string} = {};
          
          const findParentDirectory = (itemId: string): string => {
            const item = boxList.find(b => b.id === itemId);
            if (!item) return '';
            if (!item.isProblem && item.parentId === null) {
              // 최상위 디렉토리
              return item.directoryId.toString();
            }
            if (item.parentId) {
              return findParentDirectory(item.parentId);
            }
            return item.directoryId.toString();
          };
          
          // 각 문제가 속한 최상위 디렉토리 찾기
          boxList.forEach(item => {
            if (item.isProblem) {
              const parentDirId = findParentDirectory(item.id);
              questionToDirectory[item.title] = parentDirId;
            }
          });
          
          questionProgressData.forEach(item => {
            progressMap[item.questionId] = item.progressPercentage;
            titleMap[item.questionTitle] = item.questionId;
            
            // 디렉토리별 문제 매핑
            const dirId = questionToDirectory[item.questionTitle] || 'unknown';
            if (!dirQuestionMap[dirId]) {
              dirQuestionMap[dirId] = {};
            }
            dirQuestionMap[dirId][item.questionTitle] = item.questionId;
          });
          
          console.log('Container ID:', containerId);
          console.log('Question Progress Data:', questionProgressData);
          console.log('Directory Question Map:', dirQuestionMap);
          console.log('Question To Directory:', questionToDirectory);
          
          setQuestionProgress(progressMap);
          setQuestionMap(titleMap);
          setDirectoryQuestionMap(dirQuestionMap);
        }
      } catch (error) {
        console.error('Failed to fetch question progress:', error);
      }
    };

    fetchQuestionProgress();
  }, [containerId, loginId, shouldRefreshProgress]);

  // 진행률 업데이트 감지 및 재조회
  useEffect(() => {
    if (shouldRefreshProgress) {
      const fetchProgress = async () => {
        try {
          const progressResponse = await getContainerProgress(containerId);
          console.log('Progress update response:', progressResponse);
          if (progressResponse) {
            const progressData = Array.isArray(progressResponse) ? progressResponse : [];
            
            // 업데이트된 멤버의 언어 정보 확인
            progressData.forEach((member, index) => {
              console.log(`Updated Member ${index}: ID=${member.memberId}, Name=${member.memberName}, Language=${member.language}, Progress=${member.averageProgress}%`);
            });
            
            setProgressData(progressData);
          }
          
          // 현재 선택된 디렉토리의 진행률도 재조회
          if (selectedId) {
            const id = selectedId.split("-")[1];
            
            // 현재 사용자의 진행률
            if (loginId) {
              const data = await getDirectoryProgress(parseInt(id), loginId);
              setDirectoryProgress(prev => ({
                ...prev,
                [id]: data.progressPercentage || 0
              }));
            }
            
            // 선택된 디렉토리의 모든 팀원 진행률 재조회
            try {
              const response = await fetch(`/progress/directory/${id}`);
              const teamData = await response.json();
              if (Array.isArray(teamData)) {
                const formattedData = teamData.map((item: any) => ({
                  memberId: item.memberId,
                  memberName: item.memberName,
                  averageProgress: item.progressComplete || 0,
                  language: item.language || 'N/A'
                }));
                setSelectedDirectoryProgress(formattedData);
              }
            } catch (err) {
              console.error('Failed to fetch directory team progress:', err);
            }
          }
          
          // 각 디렉토리의 진행률도 재조회
          if (loginId) {
            boxList.forEach(async (item) => {
              if (isProblemDirectory(item.title)) {
                try {
                  const data = await getDirectoryProgress(item.directoryId, loginId);
                  setDirectoryProgress(prev => ({
                    ...prev,
                    [item.directoryId]: data.progressPercentage || 0
                  }));
                } catch (err) {
                  console.error('Failed to fetch progress for directory:', item.directoryId, err);
                }
              }
            });
          }
          
          // 문제별 진행률도 재조회
          if (loginId) {
            try {
              const questionProgressData = await getQuestionProgressByMember(containerId, loginId);
              if (questionProgressData && Array.isArray(questionProgressData)) {
                const progressMap: {[key: number]: number} = {};
                const titleMap: {[key: string]: number} = {};
                const dirQuestionMap: {[key: string]: {[key: string]: number}} = {};
                
                // 먼저 boxList를 순회하여 디렉토리 구조 파악
                const questionToDirectory: {[key: string]: string} = {};
                
                const findParentDirectory = (itemId: string): string => {
                  const item = boxList.find(b => b.id === itemId);
                  if (!item) return '';
                  if (!item.isProblem && item.parentId === null) {
                    return item.directoryId.toString();
                  }
                  if (item.parentId) {
                    return findParentDirectory(item.parentId);
                  }
                  return item.directoryId.toString();
                };
                
                // 각 문제가 속한 최상위 디렉토리 찾기
                boxList.forEach(item => {
                  if (item.isProblem) {
                    const parentDirId = findParentDirectory(item.id);
                    questionToDirectory[item.title] = parentDirId;
                  }
                });
                
                questionProgressData.forEach(item => {
                  progressMap[item.questionId] = item.progressPercentage;
                  titleMap[item.questionTitle] = item.questionId;
                  
                  // 디렉토리별 문제 매핑
                  const dirId = questionToDirectory[item.questionTitle] || 'unknown';
                  if (!dirQuestionMap[dirId]) {
                    dirQuestionMap[dirId] = {};
                  }
                  dirQuestionMap[dirId][item.questionTitle] = item.questionId;
                });
                
                setQuestionProgress(progressMap);
                setQuestionMap(titleMap);
                setDirectoryQuestionMap(dirQuestionMap);
              }
            } catch (error) {
              console.error('Failed to fetch question progress:', error);
            }
          }
        } catch (error) {
          console.error('Failed to fetch progress:', error);
        } finally {
          // 진행률 재조회 완료 후 플래그 리셋
          dispatch(resetProgressRefresh());
        }
      };
      
      fetchProgress();
    }
  }, [shouldRefreshProgress, containerId, dispatch, selectedId, loginId, boxList]);

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
              <span className={styles.treeTitle}>
                {item.title}
                {!item.isProblem && isProblemDirectory(item.title) && directoryProgress[item.directoryId] !== undefined && (
                  <span style={{ marginLeft: '8px', fontSize: '12px', color: '#666' }}>
                    ({directoryProgress[item.directoryId]}%)
                  </span>
                )}
                {item.isProblem && (() => {
                  // 문제가 속한 최상위 디렉토리 찾기
                  const findParentDirectory = (itemId: string): string => {
                    const currentItem = boxList.find(b => b.id === itemId);
                    if (!currentItem) return '';
                    if (!currentItem.isProblem && currentItem.parentId === null) {
                      return currentItem.directoryId.toString();
                    }
                    if (currentItem.parentId) {
                      return findParentDirectory(currentItem.parentId);
                    }
                    return currentItem.directoryId.toString();
                  };
                  
                  const parentDirId = findParentDirectory(item.id);
                  const dirMap = directoryQuestionMap[parentDirId] || {};
                  const mappedQuestionId = dirMap[item.title];
                  const progress = mappedQuestionId !== undefined ? questionProgress[mappedQuestionId] : undefined;
                  
                  console.log(`Problem: ${item.title}, Directory: ${parentDirId}, Mapped ID: ${mappedQuestionId}, Progress: ${progress}`);
                  
                  return progress !== undefined ? (
                    <span style={{ marginLeft: '8px', fontSize: '12px', color: '#666' }}>
                      ({progress}%)
                    </span>
                  ) : (
                    <span style={{ marginLeft: '8px', fontSize: '12px', color: '#666' }}>
                      (0%)
                    </span>
                  );
                })()}
              </span>
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
            {menuPos && loginId === containerOwner && (
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
                          // console.log("Deleting question:", questionId);
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
            <h3>팀원 현황({selectedId ? selectedDirectoryProgress.length : progressData.length} / 5)</h3>
            {/* 디렉토리가 선택되었을 때는 해당 디렉토리의 진행률, 아니면 전체 진행률 */}
            {(selectedId && selectedDirectoryProgress.length > 0 ? selectedDirectoryProgress : progressData).map((member, index) => {
              console.log(`Team member progress - Name: ${member.memberName}, Progress: ${member.averageProgress}%, Language: ${member.language}`);
              return (
                <Bargraph
                    key={member.memberId || index}
                    name={member.memberName || member.memberId || ''}
                    language={member.language || 'N/A'}
                    success={member.averageProgress || 0}
                    total={100}
                />
              )
            })}
            {/* 빈 슬롯 채우기 */}
            {[...Array(Math.max(0, 5 - (selectedId && selectedDirectoryProgress.length > 0 ? selectedDirectoryProgress.length : progressData.length)))].map((_, index) => (
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
