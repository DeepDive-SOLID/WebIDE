import { editor } from "monaco-editor";

// api 동기화하면 변수명 변경 !
export interface member {
    id: string;
    name: string
}

export interface HeaderProps {
    activeMember: string | null;
    handleOnClick: (id: string) => void;
}

export interface CodeEditorProps {
    language: string;
    code: string  | undefined;
    onChange: (value?: string | null | undefined, ev?: editor.IModelContentChangedEvent | undefined) => void;
}


export interface ContainerProp {
    activeMember: string;
}

export interface XtermProps {
    isInputDisabled: boolean,
    setIsInputDisabled: (check: boolean) => void
    codeId: number,
    height: number,
    terminalToggle: string;
}

export interface codeFileList {
    codeFileId: number;
    codeFileName: string;
    directoryId: number;
}

export interface testResult {
    actual: string,
    input: string,
    mem: string,
    output: string,
    pass: boolean,
    time: number,
}

export interface testApi {
    isCorrect?: boolean,
    language?: string,
    testcaseResults?: testResult[];
}
export type MonacoEditor = editor.IStandaloneCodeEditor
