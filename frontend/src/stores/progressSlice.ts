import { createSlice } from '@reduxjs/toolkit';

interface ProgressState {
  shouldRefresh: boolean;
  lastUpdated: number;
}

const initialState: ProgressState = {
  shouldRefresh: false,
  lastUpdated: Date.now()
};

const progressSlice = createSlice({
  name: 'progress',
  initialState,
  reducers: {
    triggerProgressRefresh: (state) => {
      state.shouldRefresh = true;
      state.lastUpdated = Date.now();
    },
    resetProgressRefresh: (state) => {
      state.shouldRefresh = false;
    }
  }
});

export const { triggerProgressRefresh, resetProgressRefresh } = progressSlice.actions;
export default progressSlice.reducer;