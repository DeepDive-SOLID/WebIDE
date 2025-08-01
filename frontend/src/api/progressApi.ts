import axios from "axios";
import type { ProgressDto } from "../types/progress";

// 문제 진행률 계산
export const updateProgress = async (data: ProgressDto): Promise<void> => {
  await axios.post("/progress/update", data);
};
