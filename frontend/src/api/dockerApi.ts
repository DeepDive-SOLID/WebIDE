import axios from "axios";
import type { CustomInputDto, CustomInputResultDto, DockerRunDto, ExecutionResultDto, ExecutionTestDto } from "../types/docker";

export const DockerRun = async (dto:DockerRunDto): Promise<ExecutionResultDto> => {
    const response = await axios.post(`/docker/run`, dto);
    return response.data;
}

export const DockerTest = async (dto: DockerRunDto):Promise<ExecutionTestDto> => {
    const response = await axios.post(`/docker/test`,dto);
    return response.data; 
}

export const DockerCustom = async (dto: CustomInputDto): Promise<CustomInputResultDto> => {
    const response = await axios.post(`/docker/custom`, dto);
    return response.data;
}