import axios from "axios";
import type {
  DirectoryDto,
  DirectoryListDto,
  DirectoryUpdDto,
  DirectoryDelDto,
} from "../types/directory";

// 전체 디렉터리 조회
export const getDirectoryList = async (params: DirectoryListDto) => {
  const response = await axios.post<DirectoryDto[]>("/directory/list", params);
  return response.data;
};

// 디렉터리 생성
export const createDirectory = async (dto: DirectoryDto) => {
  const response = await axios.post<DirectoryDto>("/directory/create", dto);
  return response.data;
};

// 디렉터리 이름 수정
export const renameDirectory = async (dto: DirectoryUpdDto) => {
  const response = await axios.put("/directory/rename", dto);
  return response.data;
};

// 디렉터리 삭제
export const deleteDirectory = async (dto: DirectoryDelDto) => {
  const response = await axios.delete("/directory/delete", {
    data: dto,
  });
  return response.data;
};
