export interface CodeFileDelDto {
  codeFileId: number;
}

export interface CodeFileListDto {
  codeFileId: number;
  directoryId: number;
  codeFilePath: string;
  codeFileName: string;
  codeFileUploadDt: string;
  codeFileCreateDt: string;
}

export interface CodeFileSaveDto {
  directoryId: number;
  codeFileName: string;
  codeContent: string;
}

export interface CodeFileUpdDto {
  codeFileId: number;
  codeContent: string;
}
