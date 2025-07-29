export interface DirectoryDelDto {
  directoryId: number;
  containerId: number;
  directoryRoot: string;
  directoryName: string;
}

export interface DirectoryDto {
  directoryId: number;
  containerId: number;
  teamId: number;
  directoryName: string;
  directoryRoot: string;
}

export interface DirectoryListDto {
  containerId: number;
}

export interface DirectoryUpdDto {
  directoryId: number;
  oldDirectoryName: string;
  directoryName: string;
}
