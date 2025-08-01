import axios from "axios";
import type { ProgressDto, ProgressResponse } from "../types/progress";

// 문제 진행률 계산
export const updateProgress = async (data: ProgressDto): Promise<void> => {
  await axios.post("/api/progress/update", data);
};

// 컨테이너별 진행률 조회
export const getContainerProgress = async (containerId: number): Promise<ProgressResponse> => {
  try {
    const response = await axios.get(`/api/progress/container/${containerId}`);
    return response.data as ProgressResponse;
  } catch (error) {
    console.error('Error fetching progress data:', error);
    throw error;
  }
};
