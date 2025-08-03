import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

interface LanguageState {
  selectedLanguage: string;
}

const initialState: LanguageState = {
  selectedLanguage: 'javascript'
};

const languageSlice = createSlice({
  name: 'language',
  initialState,
  reducers: {
    setSelectedLanguage: (state, action: PayloadAction<string>) => {
      state.selectedLanguage = action.payload;
    }
  }
});

export const { setSelectedLanguage } = languageSlice.actions;
export default languageSlice.reducer;