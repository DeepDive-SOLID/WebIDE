import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate();

  const handleContainerClick = (id: number) => {
    navigate(`/container/${id}`);
  };

  return (
    <div style={{ padding: "2rem" }}>
      <h2>컨테이너 선택</h2>
      <button onClick={() => handleContainerClick(1)}>컨테이너 1번</button>
      <button onClick={() => handleContainerClick(2)}>컨테이너 2번</button>
      <button onClick={() => handleContainerClick(3)}>컨테이너 3번</button>
    </div>
  );
};

export default Home;
