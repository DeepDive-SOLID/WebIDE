import axios from "./axios";
import type {
  CreateContainerDto,
  ContainerResponseDto,
  GroupMemberResponseDto,
  ApiResponse,
  UpdateContainerDto,
  InviteMemberDto,
} from "../types/home";

// 컨테이너 생성 API

export const createContainer = async (data: CreateContainerDto) => {
  const response = await axios.post("/containers", data);
  return response.data;
};

// 컨테이너 멤버 초대 API
export const inviteMember = async (
  containerId: number,
  memberId: string
): Promise<GroupMemberResponseDto> => {
  const response = await axios.post<ApiResponse<GroupMemberResponseDto>>(
    `/containers/${containerId}/members`,
    { memberId } as InviteMemberDto
  );
  return response.data.data;
};

// 컨테이너 전체 조회 API

export const getContainers = async (): Promise<ContainerResponseDto[]> => {
  const response = await axios.get<ApiResponse<ContainerResponseDto[]>>(
    "/containers"
  );
  return response.data.data;
};

// 컨테이너 상세 조회 API
export const getContainerDetail = async (
  containerId: number
): Promise<ContainerResponseDto> => {
  const response = await axios.get<ApiResponse<ContainerResponseDto>>(
    `/containers/${containerId}`
  );
  return response.data.data;
};

// 컨테이너 정보 수정 API (isPublic만 보낼 수 있음)
export const updateContainer = async (
  containerId: number,
  body: UpdateContainerDto
): Promise<ContainerResponseDto> => {
  const response = await axios.put<ApiResponse<ContainerResponseDto>>(
    `/containers/${containerId}`,
    body
  );
  return response.data.data;
};

// 컨테이너 멤버 조회 API
export const getContainerMembers = async (
  containerId: number
): Promise<GroupMemberResponseDto[]> => {
  const response = await axios.get<ApiResponse<GroupMemberResponseDto[]>>(
    `/containers/${containerId}/members`
  );
  return response.data.data;
};

// 컨테이너 참가 API
export const joinContainer = async (
  containerId: number
): Promise<{ success: boolean; message: string }> => {
  const response = await axios.post<ApiResponse<null>>(
    `/containers/${containerId}/join`
  );
  return {
    success: response.data.success,
    message: response.data.message || "",
  };
};

// 컨테이너 멤버 삭제 API
export const deleteMember = async (
  containerId: number,
  targetMemberId: string
): Promise<{ success: boolean; message: string }> => {
  const response = await axios.delete<ApiResponse<null>>(
    `/containers/${containerId}/members/${targetMemberId}`
  );
  return {
    success: response.data.success,
    message: response.data.message || "",
  };
};

// 컨테이너 탈퇴 API
export const leaveContainer = async (
  containerId: number
): Promise<{ success: boolean; message: string }> => {
  const response = await axios.delete<ApiResponse<null>>(
    `/containers/${containerId}/members/me`
  );
  return {
    success: response.data.success,
    message: response.data.message || "",
  };
};

// 내가 속한 컨테이너 목록 조회 API
export const getMyContainers = async (): Promise<ContainerResponseDto[]> => {
  const response = await axios.get<ApiResponse<ContainerResponseDto[]>>(
    "/containers/my"
  );
  return response.data.data;
};

// 내가 멤버로 참여중인(공유받은) 컨테이너 목록 조회 API
export const getSharedContainers = async (): Promise<
  ContainerResponseDto[]
> => {
  const response = await axios.get<ApiResponse<ContainerResponseDto[]>>(
    "/containers/shared"
  );
  return response.data.data;
};

// 공개된 컨테이너 목록 조회 API
export const getPublicContainers = async (): Promise<
  ContainerResponseDto[]
> => {
  const response = await axios.get<ApiResponse<ContainerResponseDto[]>>(
    "/containers/public"
  );
  return response.data.data;
};
