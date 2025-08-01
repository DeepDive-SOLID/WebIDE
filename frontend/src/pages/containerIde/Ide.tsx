import { useEffect, useState } from "react";
import Question from "../../components/ide/question/Question";
import Header from "../../components/ide/header/Header";
import Container from "../../components/ide/container/Container";
import { getCurrentMemberId } from "../../utils/auth";

interface IdeProps {
  containerId: number;
}
const Ide = ({ containerId, modal }: IdeProps) => {
  // 로그인한 유저의 id 가져오기
  const [loginId, setLoginId] = useState<string>(getCurrentMemberId());
  const [activeButtonId, setActiveButtonId] = useState<string>("");
  // 클릭된 버튼의 ID를 인자로 받습니다.
  const handleOnClick = (id: string) => {
    setActiveButtonId((prev) => (prev === id ? loginId : id));
  };
  useEffect(() => {
    setLoginId(loginId);
    setActiveButtonId(loginId);
  }, []);

  return (
    <div style={{ flex: 1, minWidth: 0, marginLeft: modal ? 320 : 80 }}>
      <Header activeMember={activeButtonId} handleOnClick={handleOnClick} containerId={containerId} />
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          border: "1px solid #f5f5f5",
          flexGrow: 1,
          flexShrink: 0,
          margin: "0 20px 20px 20px",
          height: "90vh",
          overflow: "hidden",
          boxShadow: "5px 5px #f5f5f5",
        }}
      >
        <Question containerId={containerId} />
        <Container activeMember={activeButtonId} />
      </div>
    </div>
  );
};

export default Ide;
