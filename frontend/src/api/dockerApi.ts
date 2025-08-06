
import type { CustomInputDto, CustomInputResultDto, DockerRunDto, ExecutionResultDto, ExecutionTestDto } from "../types/docker";
import api from "./axios";

export const DockerRun = async (dto:DockerRunDto): Promise<ExecutionResultDto> => {
    const response = await api.post<ExecutionResultDto>(`/docker/run`, dto);
    return response.data;
}

export const DockerTest = async (dto: DockerRunDto):Promise<ExecutionTestDto> => {
    const response = await api.post<ExecutionTestDto>(`/docker/test`,dto);
    return response.data; 
}

export const DockerCustom = async (dto: CustomInputDto): Promise<CustomInputResultDto> => {
    const response = await api.post<CustomInputResultDto>(`/docker/custom`, dto);
    return response.data;
}