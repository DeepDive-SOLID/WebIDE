import { useEffect, useState } from "react";
import Question from "../../components/ide/question/Question";
import Header from "../../components/ide/header/Header";
import Container from "../../components/ide/container/Container";
import type { question } from "../../types/ide";
import axios from "axios";

const Ide = () => {
  // 로그인한 유저의 id 가져오기
  const [loginId, setLoginId] = useState<string | null>("");
  const [activeButtonId, setActiveButtonId] = useState<string | null>("");
  const [question, setQuestion] = useState<question>();

  // 클릭된 버튼의 ID를 인자로 받습니다.
  const handleOnClick = (id: string) => {
    setActiveButtonId(id);
  };
  useEffect(() => {
    // 컨테이너 유저 목록을 가져오는 api

    // 테스트용 더미 데이터
    const id = "test";
    setLoginId(id);
    setActiveButtonId(id);

    const fetchQuestionData = async () => {
      try {
        const res = await axios.post("http://localhost:8080/question/list_id", { containerId: 1 });
        console.log(res.data);
        setQuestion(res.data);
      } catch (e) {
        console.error("에러: " + e);
      }
    };
    fetchQuestionData();
  }, []);

  return (
    <>
      <Header activeMember={activeButtonId} handleOnClick={handleOnClick} />
      <div style={{ display: "flex", flexDirection: "row", border: "1px solid black", margin: "0 20px 20px 20px", height: "90vh", overflow: "hidden" }}>
        <Question question={question} />
        <Container activeMember={activeButtonId} />
      </div>
    </>
  );
};

export default Ide;
