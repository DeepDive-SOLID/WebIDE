import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

// 저장할 항목 타입
interface ProblemEntry {
  directoryId: number;
  title: string;
}

interface ProblemState {
  entries: ProblemEntry[];
}

const initialState: ProblemState = {
  entries: [],
};

const problemSlice = createSlice({
  name: "problems",
  initialState,
  reducers: {
    // 문제 디렉터리 목록 갱신
    setProblemEntries(state, action: PayloadAction<ProblemEntry[]>) {
      state.entries = action.payload;
    },
    // 필요시 초기화
    clearProblemEntries(state) {
      state.entries = [];
    },
  },
});

export const { setProblemEntries, clearProblemEntries } = problemSlice.actions;
export default problemSlice.reducer;
