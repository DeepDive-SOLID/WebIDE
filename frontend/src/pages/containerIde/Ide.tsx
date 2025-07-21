import { useEffect, useState } from "react";
import Question from "../../components/ide/question/Question";
import Header from "../../components/ide/header/Header";
import Container from "../../components/ide/container/Container";
import type { question } from "../../types/ide";

const Ide = () => {
  // 로그인한 유저의 id 가져오기
  const [loginId, setLoginId] = useState<string | null>("");
  const [activeButtonId, setActiveButtonId] = useState<string | null>("");
  const [question, setQuestion] = useState<question>({});

  // 클릭된 버튼의 ID를 인자로 받습니다.
  const handleOnClick = (id: string) => {
    setActiveButtonId(id);
  };
  useEffect(() => {
    // 컨테이너 유저 목록을 가져오는 api

    // 테스트용 더미 데이터
    const id = "1";
    setLoginId(id);
    setActiveButtonId(id);

    const fetchQuestionData = async () => {
      // 가상의 질문 데이터
      const dummyQuestion: question = {
        questionId: 1,
        questionTitle: "더하기",
        questionDescroption: "두 수를 더하는 함수를 작성하세요.",
        question: "",
        questionInput: "첫째 줄에 입력을 받은 값을 더하고 두번째 줄에 입력을 받아 더한다",
        questionOutput: "더한 값만 나오고 출력한다.",
        questionTime: 1,
        questionMem: 1,
        // 필요한 다른 필드 추가
      };
      setQuestion(dummyQuestion);
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
