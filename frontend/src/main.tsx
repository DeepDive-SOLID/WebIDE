import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "./styles/index.css";
import { Provider } from "react-redux";
import { store } from "./stores";
import App from "./App";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import FindId from "./pages/FindId";
import FindPw from "./pages/FindPw";

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
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <Provider store={store}>
    <RouterProvider router={router} />
  </Provider>
);
