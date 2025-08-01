// 컨테이너 생성 DTO
export interface CreateContainerDto {
  containerName: string;
  containerContent?: string;
  isPublic: boolean;
  invitedMemberIds?: string[];
}

// 컨테이너 응답 DTO
export interface ContainerResponseDto {
  containerId: number;
  containerName: string;
  containerContent: string;
  isPublic: boolean;
  ownerId: string;
  authority: "ROOT" | "USER";
  memberCount: number;
  createdDate: string;
}

// 컨테이너 멤버 응답 DTO
export interface GroupMemberResponseDto {
  teamUserId: number;
  memberId: string;
  memberName: string;
  memberEmail: string;
  authority: "ROOT" | "USER";
  joinedDate: string;
  lastActivityDate: string;
}

// API 응답 공통 타입
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp?: string;
}

// 컨테이너 정보 수정 DTO
export interface UpdateContainerDto {
  isPublic: boolean;
}

// 컨테이너 멤버 초대 DTO
export interface InviteMemberDto {
  memberId: string;
}
