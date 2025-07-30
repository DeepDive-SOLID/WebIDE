import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  define: {
    'process.env': {}, // 일부 라이브러리에서 필요
    global: "window",
  },
  server: {
    proxy: {
      "/sign": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/mypage": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
