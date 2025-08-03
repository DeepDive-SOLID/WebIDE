export interface QuestionCreateDto {
  containerId: number;
  teamId: number;
  directoryId?: number; // 추가
  questionTitle: string;
  questionDescription: string;
  question: string;
  questionInput: string;
  questionOutput: string;
  questionTime: number;
  questionMem: number;
  testcases: TestCaseDto[];
}

export interface QuestionListDto {
  questionId: number;
  containerId: number;
  teamId: number;
  directoryId?: number;
  questionTitle: string;
  questionDescription: string;
  question: string;
  questionInput: string;
  questionOutput: string;
  questionTime: number;
  questionMem: number;
}

export interface QuestionUpdDto {
  questionId: number;
  questionTitle: string;
  questionDescription: string;
  question: string;
  questionInput: string;
  questionOutput: string;
  questionTime: number;
  questionMem: number;
  testcases: TestCaseUpdDto[];
}

export interface test {
  caseId: number;
  caseEx: string;
  caseAnswer: string;
}
export interface TestCaseDto {
  caseEx: string;
  caseAnswer: string;
  caseCheck: boolean;
}

export interface TestCaseUpdDto {
  caseId: number;
  caseEx: string;
  caseAnswer: string;
  caseCheck: boolean;
}
