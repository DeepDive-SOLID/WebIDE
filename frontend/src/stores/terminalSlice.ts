import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

export interface TerminalState {
    runInput: string;
    runOutput: string;
    submitOutput: string;
    restart: boolean;
  }
  
  // 초기 상태 정의
  const initialState: TerminalState = {
    runInput: '',
    runOutput: '',
    submitOutput: '',
    restart: false,
  };

export const terminalSlice = createSlice({
    name: 'terminal',
    initialState,
    reducers: {
        setRunInput: (state, action: PayloadAction<string>) => {
            state.runInput = action.payload;
        },
        setRunOutput: (state, action: PayloadAction<string>) => {
            state.runOutput = action.payload;
        },
        setSubmitOutput: (state, action: PayloadAction<string>) => {
            state.submitOutput = action.payload;
        },
        setRestart: (state) => {
            state.restart = !state.restart
        }
    }
})

export const { setRunInput, setRunOutput, setRestart, setSubmitOutput } = terminalSlice.actions;
export default terminalSlice.reducer;