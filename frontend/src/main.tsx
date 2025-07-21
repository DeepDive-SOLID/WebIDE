import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "./styles/index.css";
import { Provider } from "react-redux";
import { store } from "./stores";
import App from "./App";
import Home from "./pages/Home";

import Ide from "./pages/containerIde/Ide";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: "/ide",
        element: <Ide />,
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <Provider store={store}>
    <RouterProvider router={router} />
  </Provider>
);
