import React, { useEffect, useState } from "react";
import axios from "axios";
import lang from "../../../assets/defaultLang.json";
import CodeEditor from "../codeEditor/CodeEditor";
import XtermComponent from "../terminal/XtermComponent";
import styles from "./container.module.scss";
import { useDispatch, useSelector } from "react-redux";
import { setRestart } from "../../../stores/terminalSlice";
import type { codeFileList, ContainerProp } from "../../../types/ide";
import type { RootState } from "../../../stores";
import TestResult from "../terminal/TestResult";

const Container = ({ activeMember }: ContainerProp) => {
  const [code, setCode] = useState<string | undefined>("");
  const [codeFile, setCodeFile] = useState<codeFileList[]>([]);
  const [codeId, setCodeId] = useState<number>();
  const [toggle, setToggle] = useState<boolean>(false);
  const [terminalToggle, setTerminalToggle] = useState<string>("run");
  const [isInputDisabled, setIsInputDisabled] = useState<boolean>(true);
  const [selectedLanguage, setSelectedLanguage] = useState<string>("javascript");
  const dispatch = useDispatch();
  const output = useSelector((state: RootState) => state.terminal.output);

  // 임시 더미 데이터
  const directoryId = 1;
  useEffect(() => {
    // 디렉토리 id를 받는다는 가정
    const fetchCodeFile = async () => {
      try {
        // 디렉토리에 존재하는 제출 코드 가져오는 api
        const res = await axios.get("http://localhost:8080/CodeFile/list");
        const filterRes = res.data.filter((items: codeFileList) => items.directoryId === directoryId);
        setCodeFile(filterRes);
      } catch (e) {
        console.log(e);
      }
    };
    fetchCodeFile();
  }, []);

  useEffect(() => {
    // codeFile이 비어있지 않을 때만 실행하도록 조건 추가
    if (codeFile.length > 0) {
      memberCode();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedLanguage, activeMember, codeFile]); // codeFile을 의존성 배열에 추가

  // 코드 리스트중 코드id 를 가지고 코드내용을 가져오는 api
  const fileData = async (codeFileId: number) => {
    try {
      const res = await axios.post("http://localhost:8080/CodeFile/content", { codeFileId: codeFileId });
      setCode(res.data);
    } catch (e) {
      console.log(e);
    }
  };

  // 코드 초기화 함수
  const memberCode = () => {
    const languageMap: { [key: string]: string } = {
      js: "javascript",
      java: "java",
      py: "python",
    };

    const memberCode = codeFile.find((member) => {
      const parts = member.codeFileName.split(".");
      const fileName = parts[0];
      const fileExtension = parts[1];

      const fullLanguageName = languageMap[fileExtension];
      return fileName === activeMember && fullLanguageName === selectedLanguage;
    });
    if (memberCode) {
      fileData(memberCode.codeFileId);
      setCodeId(memberCode.codeFileId);
    } else {
      // 없으면 기본 코드 설정
      if (selectedLanguage === "javascript") {
        setCode(lang.javascript.value);
      } else if (selectedLanguage === "java") {
        setCode(lang.java.value);
      } else {
        setCode(lang.python.value);
      }
    }
  };

  // 선택 언어 변경 이벤트
  const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedLanguage(e.target.value);
  };
  // 작성 코드 변경 이벤트
  const handleCodeChange = (value: string | null | undefined) => {
    setCode(value ?? "");
  };

  // 테스트 api
  const testAPI = async () => {
    try {
      const res = axios.post("http://localhost:8080/docker/test", { codeId: codeId, questionId: 1 });
      console.log(res);
    } catch (e) {
      console.log(e);
    }
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
        const data = await axios.post("http://localhost:8080/CodeFile/create", { directoryId: 1, codeFileName: select, codeContent: code });
        console.log(data);
      } else {
        const data = await axios.put("http://localhost:8080/CodeFile/update", { codeFileId: existCode.codeFileId, codeContent: code });
        console.log(data);
      }
    } catch (e) {
      console.log(e);
    }
  };

  // 버튼에 따른 클릭 이벤트
  const handleClickEvent = (type: string) => {
    switch (type) {
      case "reset":
        setToggle((prev) => !prev);
        break;
      case "test":
        setTerminalToggle(type);
        saveAPI();
        testAPI();
        break;
      case "run":
        setTerminalToggle(type);
        saveAPI();
        setIsInputDisabled((prev) => !prev);
        break;
      case "submit": {
        setTerminalToggle(type);
        saveAPI();
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
        {terminalToggle === "test" ? <TestResult /> : <XtermComponent isInputDisabled={isInputDisabled} setIsInputDisabled={setIsInputDisabled} codeId={codeId} height={300} />}
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
