import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

export interface TerminalState {
    input: string;
    output: string | null;
    restart: boolean;
  }
  
  // 초기 상태 정의
  const initialState: TerminalState = {
    input: '',
    output: null,
    restart: false,
  };

export const terminalSlice = createSlice({
    name: 'terminal',
    initialState,
    reducers: {
        setInput: (state, action: PayloadAction<string>) => {
            state.input = action.payload;
        },
        setOutput: (state, action: PayloadAction<string | null>) => {
            state.output = action.payload;
        },
        setRestart: (state) => {
            state.restart = !state.restart
        }
    }
})

export const { setInput, setOutput, setRestart } = terminalSlice.actions;
export default terminalSlice.reducer;