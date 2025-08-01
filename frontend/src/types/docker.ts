
export interface DockerRunDto {
    memberId: string,
    codeFileId: number,
    questionId: number   
}

export interface CustomInputDto {
    codeFileId: number,
    questionId: number,
    input: string
}

export interface ExecutionResultDto {
    language: string,
    time: number,
    mem: string,
    isCorrect: boolean,
    testcaseResults: TestcaseResultDto[]
}

export interface ExecutionTestDto {
    language: string,
    isCorrect: boolean,
    testcaseResults: TestcaseResultDto[]
}

export interface CustomInputResultDto {
    output: string,
}

export interface TestcaseResultDto {
    time: number,
    mem: string,
    input: string,
    output: string,
    actual: string,
    pass: boolean
}
