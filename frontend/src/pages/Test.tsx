import { useParams } from "react-router-dom";
import AlgorithmNav from "../components/APP/Nav/AlgorithmNav";

const Test = () => {
  const { containerId } = useParams<{ containerId: string }>();

  return (
    <div>
      <h2>컨테이너 ID: {containerId}</h2>
      <AlgorithmNav containerId={Number(containerId)} />
    </div>
  );
};

export default Test;
