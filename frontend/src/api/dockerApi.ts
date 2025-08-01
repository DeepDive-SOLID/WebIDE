import axios from "axios";
import type { CustomInputDto, CustomInputResultDto, DockerRunDto, ExecutionResultDto, ExecutionTestDto } from "../types/docker";

export const DockerRun = async (dto:DockerRunDto): Promise<ExecutionResultDto> => {
    const response = await axios.post<ExecutionResultDto>(`/docker/run`, dto);
    return response.data;
}

export const DockerTest = async (dto: DockerRunDto):Promise<ExecutionTestDto> => {
    const response = await axios.post<ExecutionTestDto>(`/docker/test`,dto);
    return response.data; 
}

export const DockerCustom = async (dto: CustomInputDto): Promise<CustomInputResultDto> => {
    const response = await axios.post<CustomInputResultDto>(`/docker/custom`, dto);
    return response.data;
}