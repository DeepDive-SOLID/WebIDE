import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/directory": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path,
      },
      "/CodeFile": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path,
      },
      "/question": {
        target: "http://localhost:8080/",
        changeOrigin: true,
      },
      "/progress": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/docker": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    },
  },
});
