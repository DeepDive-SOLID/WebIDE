import React, { useEffect, useState } from "react";
import lang from "../../../assets/defaultLang.json";
import CodeEditor from "../codeEditor/CodeEditor";
import XtermComponent from "../terminal/XtermComponent";
import styles from "../../../styles/container.module.scss";
import { useDispatch, useSelector } from "react-redux";
import { setRestart, setRunInput, setRunOutput, setSubmitOutput } from "../../../stores/terminalSlice";
import type { codeFileList, ContainerProp, testApi } from "../../../types/ide";
import type { RootState } from "../../../stores";
import TestResult from "../terminal/TestResult";
import { createCodeFile, getCodeFileContent, getCodeFileList, updateCodeFile } from "../../../api/codefileApi";
import type { CodeFileListDto } from "../../../types/codefile";
import { DockerRun, DockerTest } from "../../../api/dockerApi";

const Container = ({ activeMember }: ContainerProp) => {
  const [code, setCode] = useState<string>("");
  const [codeFile, setCodeFile] = useState<CodeFileListDto[]>([]);
  const [codeId, setCodeId] = useState<number>();
  const [toggle, setToggle] = useState<boolean>(false);
  const [terminalToggle, setTerminalToggle] = useState<string>("run");
  const [isInputDisabled, setIsInputDisabled] = useState<boolean>(true);
  const [selectedLanguage, setSelectedLanguage] = useState<string>("javascript");
  const [testCase, setTestCase] = useState<testApi>({});
  const dispatch = useDispatch();
  const output = useSelector((state: RootState) => state.terminal.runOutput);
  const directoryId = useSelector((state: RootState) => state.problems.directoryId);
  const questionId = useSelector((state: RootState) => state.problems.questionId);

  useEffect(() => {
    const fetchCodeFile = async () => {
      try {
        const data = await getCodeFileList();
        const filterRes = data.filter((items: CodeFileListDto) => items?.directoryId === Number(directoryId));
        setCodeFile(filterRes); // codeFile 상태 업데이트
      } catch (e) {
        console.error("Error fetching code file list:", e);
      }
    };

    // directoryId가 유효할 때만 API 호출
    if (directoryId !== undefined && directoryId !== null) {
      fetchCodeFile();
    } else {
      // directoryId가 없을 경우 codeFile을 비워서 memberCode가 기본값을 설정하도록 함
      setCodeFile([]);
    }
    setTestCase({});
  }, [directoryId]); // directoryId가 변경될 때만 재실행

  useEffect(() => {
    // codeFile이 비어있지 않을 때만 실행하도록 조건 추가
    memberCode();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedLanguage, activeMember, directoryId, codeFile]); // codeFile을 의존성 배열에 추가

  // 코드 리스트중 코드id 를 가지고 코드내용을 가져오는 api
  const fileData = async (codeFileId: number) => {
    try {
      const data = await getCodeFileContent(codeFileId);
      setCode(data);
    } catch (e) {
      console.log(e);
    }
  };

  // 코드 초기화 함수
  const memberCode = async () => {
    const languageMap: { [key: string]: string } = {
      js: "javascript",
      java: "java",
      py: "python",
    };

    const memberCode = codeFile.find((member) => {
      const parts = member.codeFileName.split(".");
      const fileName = parts[0].toLocaleLowerCase();
      const fileExtension = parts[1];

      const fullLanguageName = languageMap[fileExtension];
      return fileName === activeMember && fullLanguageName === selectedLanguage;
    });
    if (memberCode) {
      await fileData(memberCode.codeFileId);
      setCodeId(memberCode.codeFileId);
    } else {
      // 없으면 기본 코드 설정
      if (selectedLanguage === "javascript") {
        setCode(lang.javascript.value);
      } else if (selectedLanguage === "java") {
        setCode(lang.java.value.replace("Main", activeMember ? activeMember : ""));
      } else {
        setCode(lang.python.value);
      }
    }
  };

  // 선택 언어 변경 이벤트
  const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedLanguage(e.target.value);
    dispatch(setSubmitOutput(""));
    dispatch(setRunInput(""));
    dispatch(setRunOutput(""));
    setTestCase({});
  };
  // 작성 코드 변경 이벤트
  const handleCodeChange = (value: string | null | undefined) => {
    setCode(value ?? "");
  };

  // 저장과 수정 api
  const saveAPI = async () => {
    if (output !== "") {
      dispatch(setRestart());
    }
    try {
      // 로그인한 유저 id 로 파일 명 바꾸기
      const select = selectedLanguage === "javascript" ? `${activeMember}.js` : selectedLanguage === "java" ? `${activeMember}.java` : `${activeMember}.py`;
      const existCode = codeFile.find((itmes) => itmes.codeFileName === select);
      if (!existCode) {
        await createCodeFile({ directoryId: directoryId, codeFileName: select, codeContent: code });
        console.log("파일 생성");
        const data = await getCodeFileList();

        const updatedList = await getCodeFileList();
        const filterRes = data.filter((items: CodeFileListDto) => items?.directoryId === Number(directoryId));
        setCodeFile(filterRes);

        const created = updatedList.find((item: codeFileList) => item.codeFileName === select);
        if (created) {
          setCodeId(created.codeFileId);
          return created.codeFileId;
        }
      } else {
        await updateCodeFile({ codeFileId: existCode.codeFileId, codeContent: code });
        console.log("파일 업데이트");
        setCodeId(existCode.codeFileId);
        return existCode.codeFileId;
      }
    } catch (e) {
      console.log(e);
    }
    return undefined;
  };
  // 테스트 api
  const testAPI = async () => {
    const id = await saveAPI();
    console.log(id);
    if (!id) return;
    try {
      const result = await DockerTest({ memberId: activeMember, codeFileId: id, questionId: questionId });
      setTestCase(result);
    } catch (e) {
      console.error(e);
    }
  };
  const submitAPI = async () => {
    const id = await saveAPI();
    if (!id) return;
    try {
      const result = await DockerRun({ memberId: activeMember, codeFileId: id, questionId: questionId });
      dispatch(setSubmitOutput(result.isCorrect ? "성공" : "실패"));
    } catch (e) {
      console.error(e);
    }
  };

  // 버튼에 따른 클릭 이벤트
  const handleClickEvent = (type: string) => {
    setTerminalToggle(type);

    switch (type) {
      case "reset":
        setToggle((prev) => !prev);
        break;
      case "test":
        testAPI();
        break;
      case "run":
        dispatch(setRunInput(""));
        dispatch(setRunOutput(""));
        saveAPI().then(() => setIsInputDisabled((prev) => !prev));
        break;
      case "submit": {
        dispatch(setSubmitOutput(""));
        submitAPI();
        break;
      }
      default:
        console.log(`${type}은 알수없는 타입입니다.`);
    }
  };
  const handleTerminalClick = (tabName: string) => {
    setTerminalToggle(tabName);
  };
  return (
    <div className={styles.flex}>
      <div className={styles.flex_space}>
        <div>
          <button className={styles.button} onClick={() => handleClickEvent("reset")}>
            초기화
          </button>
          <button className={styles.button} onClick={() => handleClickEvent("test")}>
            테스트
          </button>
          <button className={styles.button} onClick={() => handleClickEvent("run")}>
            실행
          </button>
          <button className={`${styles.button} ${styles.submit}`} onClick={() => handleClickEvent("submit")}>
            제출
          </button>
        </div>
        <select id='language-select' value={selectedLanguage} className={styles.select} onChange={handleLanguageChange}>
          <option value='javascript'>JavaScript</option>
          <option value='java'>Java</option>
          <option value='python'>Python</option>
        </select>
      </div>
      {activeMember && <CodeEditor language={selectedLanguage} code={code} onChange={handleCodeChange} />}
      <div>
        <div style={{ display: "flex" }}>
          <div className={`${styles.terminal_case} ${terminalToggle === "run" ? styles.terminal_case_active : ""}`} onClick={() => handleTerminalClick("run")}>
            실행 결과
          </div>
          <div className={`${styles.terminal_case} ${terminalToggle === "test" ? styles.terminal_case_active : ""}`} onClick={() => handleTerminalClick("test")}>
            테스트 결과
          </div>
          <div className={`${styles.terminal_case} ${terminalToggle === "submit" ? styles.terminal_case_active : ""}`} onClick={() => handleTerminalClick("submit")}>
            제출 결과
          </div>
        </div>
      </div>
      <div style={{ minHeight: "300px" }}>
        {terminalToggle === "test" ? (
          testCase && <TestResult isCorrect={testCase.isCorrect} testcaseResults={testCase.testcaseResults} />
        ) : (
          <XtermComponent isInputDisabled={isInputDisabled} setIsInputDisabled={setIsInputDisabled} codeId={codeId} height={300} terminalToggle={terminalToggle} />
        )}
      </div>
      <div className={`${styles.resetModal} ${toggle ? styles.action : ""}`}>
        <div className={styles.resetModal_box}>
          <p>
            정말로{" "}
            <i>
              <b>초기화</b>
            </i>
            하시겠습니까 ? <span className={styles.font_color}>확인을 누르시면 모든 코드가 초기화 됩니다.</span>
          </p>
          <div>
            <button className={styles.resetModal_button} onClick={() => setToggle((prev) => !prev)}>
              취소
            </button>
            <button
              className={styles.resetModal_button}
              onClick={() => {
                memberCode();
                setToggle((prev) => !prev);
              }}
            >
              확인
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Container;
