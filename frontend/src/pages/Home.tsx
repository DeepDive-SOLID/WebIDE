import AppNav from "../components/APP/Nav/AppNav";
import { Outlet, useLocation } from "react-router-dom";
import { useState } from "react";
import { Link } from "react-router-dom";

const Home = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [sidebarType, setSidebarType] = useState<
    "container" | "algorithm" | null
  >(null);
  const location = useLocation();
  const isContainerSidebarOpen = sidebarOpen && sidebarType === "container";
  const needDynamicMargin = [
    "/home/all-container",
    "/home/my-container",
    "/home/shared-container",
    "/home/public-container",
  ].includes(location.pathname);

  return (
    <div style={{ display: "flex" }}>
      <AppNav
        sidebarOpen={sidebarOpen}
        setSidebarOpen={setSidebarOpen}
        sidebarType={sidebarType}
        setSidebarType={setSidebarType}
      />
      <div
        style={{
          flex: 1,
          marginLeft: needDynamicMargin
            ? isContainerSidebarOpen
              ? 330
              : 80
            : 80,
        }}
      >
        <Outlet
          context={{
            sidebarOpen,
            setSidebarOpen,
            sidebarType,
            setSidebarType,
            isContainerSidebarOpen,
          }}
        />
      </div>
    </div>
  );
};

export default Home;
