import React, { useEffect, useState } from "react";
import lang from "../../../assets/defaultLang.json";
import CodeEditor from "../codeEditor/CodeEditor";
import styles from "./container.module.scss";
import type { ContainerProp } from "../../../types/ide";

// api 요청에 따른 변경 후 모듈화 시전
interface a {
  id: string;
  code: string;
  language: string;
}

const Container = ({ activeMember }: ContainerProp) => {
  const [code, setCode] = useState<string | undefined>("");
  const [resultMember, setResultMember] = useState<a[]>([]);
  const [toggle, setToggle] = useState<boolean>(false);
  const [selectedLanguage, setSelectedLanguage] = useState<string>("javascript");

  useEffect(() => {
    const res = async () => {
      //api 요청
      // 더미
      setResultMember([
        { id: "1", code: "asdasdasda", language: "javascript" },
        { id: "2", code: "hi", language: "java" },
        { id: "2", code: "javascript", language: "javascript" },
        { id: "2", code: "python", language: "python" },
        { id: "3", code: "function test() { \n  console.log('hello') \n}\n\ntest(); ", language: "javascript" },
      ]);
    };
    res();
  }, []);

  useEffect(() => {
    // 해당 문제에 대한 풀이가 없으면 firstCode 로 기본 코드 초기화
    // 만일 문제 푼 게 있으면 기본 코드가 아닌 푼 코드로 초기화
    memberCode();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedLanguage, activeMember]);

  const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedLanguage(e.target.value);
  };

  const handleCodeChange = (value: string | null | undefined) => {
    setCode(value ?? "");
  };

  const testAPI = async () => {
    console.log("testAPI 입니다.");
  };
  const runAPI = async () => {
    console.log("runAPI 입니다.");
  };
  const submitAPI = async () => {
    console.log("submitAPI 입니다.");
  };

  const handleClickEvent = (type: string) => {
    switch (type) {
      case "reset":
        setToggle((prev) => !prev);
        break;
      case "test":
        // api 함수
        testAPI();
        break;
      case "run":
        // api 함수
        runAPI();
        break;
      case "submit":
        // api 함수
        submitAPI();
        break;
      default:
        console.log(`${type}은 알수없는 타입입니다.`);
    }
  };

  const memberCode = () => {
    const memberCode = resultMember.find((member) => member.id === activeMember && member.language === selectedLanguage);

    if (memberCode) {
      // 해당 멤버의 해당 언어 코드가 있으면 설정
      setCode(memberCode.code);
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
