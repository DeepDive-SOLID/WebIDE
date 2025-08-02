import axios from "axios";
import type { ProgressDto, ProgressResponse, QuestionProgressResponse } from "../types/progress";

// 문제 진행률 계산
export const updateProgress = async (data: ProgressDto): Promise<void> => {
  await axios.post("/progress/update", data);
};

// 컨테이너별 진행률 조회
export const getContainerProgress = async (containerId: number): Promise<ProgressResponse> => {
  try {
    const response = await axios.get(`/progress/container/${containerId}`);
    return response.data as ProgressResponse;
  } catch (error) {
    console.error('Error fetching progress data:', error);
    throw error;
  }
};

// 디렉토리별 문제 진행률 조회
export const getDirectoryProgress = async (directoryId: number, memberId: string) => {
  try {
    const response = await axios.get(`/progress/directory/${directoryId}/member/${memberId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching directory progress:', error);
    throw error;
  }
};

// 컨테이너의 모든 문제별 진행률 조회
export const getQuestionProgressByMember = async (containerId: number, memberId: string): Promise<QuestionProgressResponse> => {
  try {
    const response = await axios.get(`/progress/container/${containerId}/member/${memberId}/questions`);
    return response.data as QuestionProgressResponse;
  } catch (error) {
    console.error('Error fetching question progress:', error);
    throw error;
  }
};
