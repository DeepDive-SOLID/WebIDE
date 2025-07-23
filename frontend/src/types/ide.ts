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

export interface question {
    questionId?: number;
    containerId?: number;
    teamId?: number;
    questionTitle?: string;
    questionDescroption?: string;
    question?: string;
    questionInput?:string;
    questionOutput?:string;
    questionTime?: number;
    questionMem?: number;
}
export interface QusetionProp {
    question: question | undefined;
}

export interface ContainerMemberCode {
    memberId: string;
    questionId: number;
    code: string ;
}

export interface ContainerProp {
    activeMember: string | null;
}

export interface ContainerSubmit {
    directoryId: number,
    codeFileName: string,
    codeContent: string | undefined
}

export interface XtermProps {
    isInputDisabled: boolean,
    setIsInputDisabled: (check: boolean) => void
    codeId: number | undefined,
    height: number,
}

export interface codeFileList {
    codeFileId: number;
    codeFileName: string;
    directoryId: number;
}
export type MonacoEditor = editor.IStandaloneCodeEditor
