import axios from "axios";
import type {
  QuestionCreateDto,
  QuestionListDto,
  QuestionUpdDto,
  test,
} from "../types/question";

// 전체 문제 리스트 조회
export const getQuestionList = async (): Promise<QuestionListDto[]> => {
  const response = await axios.get<QuestionListDto[]>("/question/list");
  return response.data;
};

// 컨테이너 ID로 문제 리스트 조회
export const getQuestionListByContainerId = async (
  containerId: number
): Promise<QuestionListDto[]> => {
  const response = await axios.post<QuestionListDto[]>("/question/list_id", containerId, {   
    headers: {
      "Content-Type": "application/json",
    }})
  return response.data;
};

// 문제 및 테스트 케이스 등록
export const createQuestion = async (
  dto: QuestionCreateDto
): Promise<string> => {
  const response = await axios.post<string>("/question/create", dto);
  return response.data;
};

// 문제 및 테스트 케이스 수정
export const updateQuestion = async (dto: QuestionUpdDto): Promise<string> => {
  const response = await axios.put<string>("/question/update", dto);
  return response.data;
};

// 문제 및 테스트 케이스 삭제
export const deleteQuestion = async (questionId: number): Promise<string> => {
  const response = await axios.delete<string>("/question/delete", {
    data: questionId,
    headers: { "Content-Type": "application/json" },
  });
  return response.data;
};


export const TestCaseQuestion = async (questionId: number): Promise<test[]> => {
  const response = await axios.post<test[]>("/question/trueList", questionId ,{
    headers: {
      "Content-Type": "application/json",
    }
  });
  return response.data;
}
