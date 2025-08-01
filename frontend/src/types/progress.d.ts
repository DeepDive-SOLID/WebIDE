// 진행률 업데이트 DTO
export interface ProgressDto {
  directoryId: number;
  teamUserId: number;
  progressComplete: number;
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
}

// 진행률 응답 타입
export type ProgressResponse = ProgressData[];