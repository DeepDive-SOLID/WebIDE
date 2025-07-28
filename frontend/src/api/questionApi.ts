import axios from "axios";
import type {
  QuestionCreateDto,
  QuestionListDto,
  QuestionUpdDto,
} from "../types/question";

// 전체 문제 리스트 조회
export const getQuestionList = async (): Promise<QuestionListDto[]> => {
  const response = await axios.get("/question/list");
  return response.data;
};

// 컨테이너 ID로 문제 리스트 조회
export const getQuestionListByContainerId = async (
  containerId: number
): Promise<QuestionListDto[]> => {
  const response = await axios.post("/question/list_id", containerId, {
    headers: { "Content-Type": "text/plain" },
  });
  return response.data;
};

// 문제 및 테스트 케이스 등록
export const createQuestion = async (
  dto: QuestionCreateDto
): Promise<string> => {
  const response = await axios.post("/question/create", dto);
  return response.data;
};

// 문제 및 테스트 케이스 수정
export const updateQuestion = async (dto: QuestionUpdDto): Promise<string> => {
  const response = await axios.put("/question/update", dto);
  return response.data;
};

// 문제 및 테스트 케이스 삭제
export const deleteQuestion = async (questionId: number): Promise<string> => {
  const response = await axios.delete("/question/delete", {
    data: questionId,
    headers: { "Content-Type": "text/plain" },
  });
  return response.data;
};
