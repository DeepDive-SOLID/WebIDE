import axios from "axios";
import type {
  CodeFileListDto,
  CodeFileSaveDto,
  CodeFileUpdDto,
  CodeFileDelDto,
} from "../types/codefile";

// 전체 코드 파일 목록 조회
export const getCodeFileList = async (): Promise<CodeFileListDto[]> => {
  const response = await axios.get("/CodeFile/list");
  return response.data;
};

// 특정 코드 파일 내용 조회
export const getCodeFileContent = async (
  codeFileId: number
): Promise<string> => {
  const response = await axios.post("/CodeFile/content", codeFileId, {
    headers: { "Content-Type": "application/json" },
  });
  return response.data;
};

// 코드 파일 생성
export const createCodeFile = async (data: CodeFileSaveDto): Promise<void> => {
  await axios.post("/CodeFile/create", data);
};

// 코드 파일 수정
export const updateCodeFile = async (data: CodeFileUpdDto): Promise<void> => {
  await axios.put("/CodeFile/update", data);
};

// 코드 파일 삭제
export const deleteCodeFile = async (data: CodeFileDelDto): Promise<void> => {
  await axios.delete("/CodeFile/delete", { data });
};
