import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "./styles/index.css";
import { Provider } from "react-redux";
import { AuthProvider } from "./contexts/AuthProvider";
import { store } from "./stores";
import App from "./App";
import Home from "./pages/Home";
import WebIde from "./pages/WebIde";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import FindId from "./pages/FindId";
import FindPw from "./pages/FindPw";
import AllContainer from "./pages/AllContainer";
import MyContainer from "./pages/MyContainer";
import SharedContainer from "./pages/SharedContainer";
import PublicContainer from "./pages/PublicContainer";
import Mypage from "./pages/Mypage";
import Info from "./pages/Info";
import KakaoLogin from "./components/UI/KakaoLogin.tsx";
import GoogleLogin from "./components/UI/GoogleLogin.tsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      {
        index: true,
        element: <Login />,
      },
      {
        path: "signup",
        element: <Signup />,
      },
      {
        path: "find-id",
        element: <FindId />,
      },
      {
        path: "find-pw",
        element: <FindPw />,
      },
      {
        path: "home",
        element: <Home />,
        children: [
          { path: "all-container", element: <AllContainer /> },
          { path: "my-container", element: <MyContainer /> },
          { path: "shared-container", element: <SharedContainer /> },
          { path: "public-container", element: <PublicContainer /> },
        ],
      },
      {
        path: "mypage",
        element: <Mypage />,
      },
      {
        path: "info",
        element: <Info />,
      },
      {
        path: "/container/:chatRoomId",
        element: <WebIde />,
      },
      {
        path: "login/kakao/callback",
        element: <KakaoLogin />,
      },
      {
        path: "login/google/callback",
        element: <GoogleLogin />,
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <Provider store={store}>
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  </Provider>
);
