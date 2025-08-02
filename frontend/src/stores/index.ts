import { configureStore } from "@reduxjs/toolkit";
import themeReducer from "./themeSlice";
import terminalReducer from "./terminalSlice";
import problemReducer from "./problemSlice";
import progressReducer from "./progressSlice";
import languageReducer from "./languageSlice";

export const store = configureStore({
  reducer: {
    theme: themeReducer,
    terminal: terminalReducer,
    problems: problemReducer,
    progress: progressReducer,
    language: languageReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;
