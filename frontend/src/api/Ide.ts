import axios from "axios";
import type { codeFileList } from "../types/ide";

const baseURL = "http://localhost:8080"

export const ContainerExistCode = async(directoryId: number) => {
    const res = await axios.get(`${baseURL}/CodeFile/list`)
    const filterRes = res.data.filter((items: codeFileList) => items.directoryId === directoryId);
    return filterRes;
}

export const CodeContent = async (codeFileId: number) => {
    const res = await axios.post(`${baseURL}/CodeFile/content`, { codeFileId: codeFileId });
    return res.data;
}

export const CodeTest = async (activeMember:string | null, codeId: number, questionId: number) => {
    const res = await axios.post(`${baseURL}/docker/test`, { memberId: activeMember, codeFileId: codeId, questionId: questionId })

    return res.data;
}

export const CodeCreate = async (directoryId:number, select: string, code:string | undefined) => {
    const res = await axios.post(`${baseURL}/CodeFile/create`, { directoryId: directoryId, codeFileName: select, codeContent: code });

    return res.data
}

export const CodeUpdate = async (codeFileId: number, code: string | undefined ) => {
    const res  = await axios.put(`${baseURL}/CodeFile/update`, { codeFileId: codeFileId, codeContent: code });

    return res.data
}

export const CodeRun = async (activeMember:string | null, codeId:number, questionId:number) => {
    const res = await axios.post(`${baseURL}/docker/run`, { memberId: activeMember, codeFileId: codeId, questionId: questionId });

    return res.data
}

export const CodeCustom = async (codeId:number | undefined , questionId:number, codeToProcess: string) => {
    const res = await axios.post(`${baseURL}/docker/custom`, { codeFileId: codeId, questionId: questionId, input: codeToProcess });

    return res.data
}