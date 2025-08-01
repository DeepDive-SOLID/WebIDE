import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

// 저장할 항목 타입
interface ProblemEntry {
  directoryId: number ;
  title: string;
  root:string;
  questionId: number
}
const initialState: ProblemEntry = {
  directoryId:0,
  title:"",
  root:"",
  questionId:0,
};

const problemSlice = createSlice({
  name: "problems",
  initialState,
  reducers: {

    setDirectoryId(state, action: PayloadAction<number>) {
      state.directoryId = action.payload
    },
    setTtile(state, action: PayloadAction<string>) {
      state.title = action.payload
    },
    setRoot(state, action: PayloadAction<string>) {
      state.root = action.payload
    },
    setQuestionId(state, action: PayloadAction<number>) {
      state.questionId = action.payload
    }
  },
});

export const { setDirectoryId, setTtile,setRoot,setQuestionId } = problemSlice.actions;
export default problemSlice.reducer;
