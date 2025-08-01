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
import Chat from "./components/UI/Chat";

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

          // { path: "all-container/:chatRoomId", element: <Chat /> },
          // { path: "my-container/:chatRoomId", element: <Chat /> },
          // { path: "shared-container/:chatRoomId", element: <Chat /> },
          // { path: "public-container/:chatRoomId", element: <Chat /> },
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
