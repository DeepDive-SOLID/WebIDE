import type {
  QuestionCreateDto,
  QuestionListDto,
  QuestionUpdDto,
  test,
} from "../types/question";
import api from "./axios";

// 전체 문제 리스트 조회
export const getQuestionList = async (): Promise<QuestionListDto[]> => {
  const response = await api.get<QuestionListDto[]>("/question/list");
  return response.data;
};

// 컨테이너 ID로 문제 리스트 조회
export const getQuestionListByContainerId = async (
  containerId: number
): Promise<QuestionListDto[]> => {
  const response = await api.post<QuestionListDto[]>("/question/list_id", containerId, {   
    headers: {
      "Content-Type": "application/json",
    }})
  return response.data;
};

// 문제 및 테스트 케이스 등록
export const createQuestion = async (
  dto: QuestionCreateDto
): Promise<string> => {
  try {
    const response = await api.post<string>("/question/create", dto);
    return response.data;
  } catch (error: any) {
    if (error.response?.data?.message) {
      throw new Error(error.response.data.message);
    }
    throw error;
  }
};

// 문제 및 테스트 케이스 수정
export const updateQuestion = async (dto: QuestionUpdDto): Promise<string> => {
  const response = await api.put<string>("/question/update", dto);
  return response.data;
};

// 문제 및 테스트 케이스 삭제
export const deleteQuestion = async (questionId: number): Promise<string> => {
  console.log("api : " + questionId)
  const response = await api.delete<string>("/question/delete", {
    params: questionId,
    headers: { "Content-Type": "application/json" },
  });
  return response.data;
};


export const TestCaseQuestion = async (questionId: number): Promise<test[]> => {
  const response = await api.post<test[]>("/question/trueList", questionId ,{
    headers: {
      "Content-Type": "application/json",
    }
  });
  return response.data;
}
