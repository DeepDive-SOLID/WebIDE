import { configureStore } from "@reduxjs/toolkit";
import themeReducer from "./themeSlice";
import terminalReducer from "./terminalSlice";
import problemReducer from "./problemSlice";

export const store = configureStore({
  reducer: {
    theme: themeReducer,
    terminal: terminalReducer,
    problems: problemReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;
