import { configureStore } from "@reduxjs/toolkit";

export const store = configureStore({
  reducer: {
    // 예: user: userReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
