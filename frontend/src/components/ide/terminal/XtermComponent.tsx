import { useEffect, useRef } from "react";
import { Terminal } from "xterm";
import { FitAddon } from "xterm-addon-fit";
import "xterm/css/xterm.css";
import { setRunInput, setRunOutput } from "../../../stores/terminalSlice";
import { useDispatch, useSelector } from "react-redux";
import type { RootState } from "../../../stores";
import type { XtermProps } from "../../../types/ide";
import { CodeCustom } from "../../../api/Ide";

const XtermComponent = ({ isInputDisabled, setIsInputDisabled, codeId, height, terminalToggle }: XtermProps) => {
  const terminalRef = useRef<HTMLDivElement>(null);
  const xtermRef = useRef<Terminal | null>(null);
  const fitAddonRef = useRef<FitAddon | null>(null);
  const dispatch = useDispatch();
  const runOutput = useSelector((state: RootState) => state.terminal.runOutput);
  const runInput = useSelector((state: RootState) => state.terminal.runInput);
  const submitOutput = useSelector((state: RootState) => state.terminal.submitOutput);
  const restart = useSelector((state: RootState) => state.terminal.restart);
  const isInputDisabledRef = useRef<boolean>(isInputDisabled);
  const inputBuffer = useRef<string>(""); // 입력된 문자열을 저장할 버퍼

  useEffect(() => {
    isInputDisabledRef.current = isInputDisabled;
  }, [isInputDisabled]);

  const run = async (codeToProcess: string) => {
    setIsInputDisabled(true); // API 호출 시작 시 입력 비활성화
    try {
      const data = await CodeCustom(codeId, 1, codeToProcess);

      if (!data) {
        // HTTP 오류 응답 처리
        throw new Error(`Backend error: ${data.status}`);
      }
      console.log(data);

      dispatch(setRunOutput("\n" + data.output.toString()));
    } catch (error) {
      console.error("백엔드 코드 처리 중 오류 발생:", error);
      // 에러 메시지를 터미널에 출력 (빨간색으로)
      dispatch(setRunOutput(`\x1b[31m오류: ${error instanceof Error ? error.message : String(error)}\x1b[0m`));
    }
  };

  useEffect(() => {
    if (!terminalRef.current) {
      return;
    }

    if (xtermRef.current) {
      xtermRef.current.dispose();
    }
    inputBuffer.current = "";
    // 터미널과 애드온 인스턴스 생성
    const xterm = new Terminal();
    const fitAddon = new FitAddon();

    // 터미널을 DOM 요소에 연결
    xterm?.open(terminalRef?.current);
    fitAddon.fit();
    xterm.loadAddon(fitAddon);

    // 기본 메시지 출력
    xterm.writeln("Welcome to CODEIT");
    if (terminalToggle === "run") {
      xterm.write("> " + runInput);
    } else {
      xterm.write("> ");
    }

    xterm.onKey(({ key, domEvent }) => {
      // console.log("Key:", key, "DOM Event:", domEvent); // 디버깅용
      if (isInputDisabledRef.current) {
        domEvent.preventDefault(); // 결과값이 돌아오면 터미널 사용 x
        return;
      }

      if (domEvent.key === "Enter") {
        if (domEvent.shiftKey) {
          // Shift + Enter: 줄바꿈
          inputBuffer.current += "\n";
          xterm.write("\r\n"); // 터미널에 줄바꿈 표시
        } else {
          // Enter: 전송
          const currentInput = inputBuffer.current.trim(); // 현재 버퍼의 입력 값 가져오기
          inputBuffer.current = "";
          if (currentInput === "clear") {
            xterm.clear();
            dispatch(setRunInput(""));
            inputBuffer.current = "";
          } else if (currentInput) {
            // 백엔드에 코드를 전달하고 응답을 받아오는 함수 호출
            run(currentInput);
            // Redux input 상태는 필요하다면 업데이트 (백엔드에 보낸 최종 입력으로 설정)
            dispatch(setRunInput(currentInput));
          } else {
            xterm.write(""); // 입력 없이 엔터만 누른 경우
          }
        }
      } else if (domEvent.key === "Backspace") {
        if (inputBuffer.current.length > 0) {
          xterm.write("\b \b"); // 터미널에서 백스페이스 시각적 표현
          inputBuffer.current = inputBuffer.current.slice(0, -1);
        }
      } else if (domEvent.key.length === 1) {
        // 일반 출력 가능한 문자 (단일 문자)
        // `domEvent.key`가 단일 문자일 때만 버퍼에 추가하고 터미널에 출력
        inputBuffer.current += key;
        xterm.write(key);
      }
    });
    // 참조에 인스턴스 저장
    xtermRef.current = xterm;
    fitAddonRef.current = fitAddon;

    return () => {
      // 컴포넌트 언마운트 시 인스턴스 정리
      xterm.dispose();
    };
  }, [restart, terminalToggle, runInput, dispatch, submitOutput]);

  // output이 있으면 터미널에 출력하고 상태 초기화
  useEffect(() => {
    if (!xtermRef.current) return;

    const formattedOutput = terminalToggle === "run" ? runOutput?.replace(/\n/g, "\r\n") : submitOutput?.replace(/\n/g, "\r\n");

    xtermRef.current.write(formattedOutput);
    inputBuffer.current = "";
  }, [runOutput, submitOutput, terminalToggle]);

  return <div ref={terminalRef} style={{ width: "100%", height: height, minHeight: "300px" }} />;
};

export default XtermComponent;
