// 진행률 업데이트 DTO
export interface ProgressDto {
  directoryId: number;
  teamUserId: number;
  progressComplete: number;
  language?: string;
}

// 진행률 데이터
export interface ProgressData {
  progressId?: number;
  directoryId?: number;
  teamUserId?: number;
  progressComplete?: number;
  directoryCount?: number;
  averageProgress?: number;
  memberId?: string;
  memberName?: string;
  language?: string;
}

// 진행률 응답 타입
export type ProgressResponse = ProgressData[];

// 문제별 진행률 DTO
export interface QuestionProgressDto {
  questionId: number;
  questionTitle: string;
  passedTestCases: number;
  totalTestCases: number;
  progressPercentage: number;
}

// 문제별 진행률 응답 타입
export type QuestionProgressResponse = QuestionProgressDto[];