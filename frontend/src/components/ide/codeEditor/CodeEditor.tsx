import { Editor } from "@monaco-editor/react";
import { useRef } from "react";
import type { CodeEditorProps, MonacoEditor } from "../../../types/ide";

const CodeEditor = ({ language, code, onChange }: CodeEditorProps) => {
  const editorRef = useRef<MonacoEditor | null>(null);
  // 다크모드 클릭 시 state 받아오기
  const theme = "light";

  const handleEditorDidMount = (editor: MonacoEditor) => {
    editorRef.current = editor;
  };
  return (
    <>
      <Editor
        width='100%' // 너비
        height='70%' // 높이
        language={language}
        value={code}
        onChange={onChange}
        onMount={handleEditorDidMount}
        theme={theme} // 에디터 테마
        options={{
          wordWrap: "on", // 자동 줄 바꿈 비활성화
          automaticLayout: true, // 에디터 컨테이너 크기 변경 시 자동 조정
          minimap: {
            enabled: false, // 미니맵 활성화 여부
          },
          fontSize: 12, // 글꼴 크기
          lineNumbers: "on", // 줄 번호 표시
          readOnly: false, // 읽기 전용 아님
          tabSize: 2, // 탭 크기 설정
          insertSpaces: true, // 탭 입력 시 공백으로 처리
          cursorStyle: "line", // 커서 스타일
          quickSuggestions: true,
          parameterHints: { enabled: false },
        }}
      />
    </>
  );
};

export default CodeEditor;
