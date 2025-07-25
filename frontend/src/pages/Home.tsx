import { Link } from "react-router-dom";

const Home = () => {
  return (
    <div>
      홈화면 입니다
      <Link to="/mypage">마이페이지로 이동</Link>
    </div>
  );
};
export default Home;
