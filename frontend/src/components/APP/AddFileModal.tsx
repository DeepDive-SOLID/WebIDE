import { useState } from "react";
import Modal from "../UI/Modal";
import InputBox from "../UI/InputBox";
import styles from "../../styles/AddFileModal.module.scss";
import { spacebar, enter, tab } from "../../assets";

interface AddFileModalProps {
  onClose: () => void;
}

interface Testcase {
  input: string;
  output: string;
  checked: boolean;
}

const AddFileModal = ({ onClose }: AddFileModalProps) => {
  const [filename, setFilename] = useState("");
  const [problem, setProblem] = useState("");
  const [inputDesc, setInputDesc] = useState("");
  const [outputDesc, setOutputDesc] = useState("");
  const [timeLimit, setTimeLimit] = useState("");
  const [memoryLimit, setMemoryLimit] = useState("");

  const [testcases, setTestcases] = useState<Testcase[]>([
    { input: "", output: "", checked: true },
    { input: "", output: "", checked: true },
    { input: "", output: "", checked: true },
  ]);

  const addTestcase = () => {
    if (testcases.length < 10) {
      setTestcases([...testcases, { input: "", output: "", checked: false }]);
    }
  };

  const updateTestcase = (
    index: number,
    field: "input" | "output" | "checked",
    value: string | boolean
  ) => {
    const updated = [...testcases];
    if (field === "checked") {
      updated[index].checked = value as boolean;
    } else {
      updated[index][field] = value as string;
    }
    setTestcases(updated);
  };

  const isFormValid = () => {
    const requiredFilled =
      filename &&
      problem &&
      inputDesc &&
      outputDesc &&
      timeLimit &&
      memoryLimit;
    const hasValidTestcase = testcases.some(
      (tc) => tc.checked && tc.input.trim() !== "" && tc.output.trim() !== ""
    );
    return requiredFilled && hasValidTestcase;
  };

  return (
    <Modal onClose={onClose}>
      <div className={styles.inputGroup}>
        <InputBox
          title="파일명"
          value={filename}
          onChange={setFilename}
          placeholder="파일명을 입력하세요"
        />
        <InputBox
          title="문제"
          value={problem}
          onChange={setProblem}
          placeholder="문제를 작성하세요"
          multiline
        />
        <InputBox
          title="입력"
          value={inputDesc}
          onChange={setInputDesc}
          placeholder="입력에 대한 설명을 작성하세요"
          multiline
        />
        <InputBox
          title="출력"
          value={outputDesc}
          onChange={setOutputDesc}
          placeholder="출력에 대한 설명을 작성하세요"
          multiline
        />
        <InputBox
          title="시간 제한"
          value={timeLimit}
          onChange={setTimeLimit}
          placeholder="제한 시간을 입력하세요"
        />
        <InputBox
          title="메모리 제한"
          value={memoryLimit}
          onChange={setMemoryLimit}
          placeholder="제한 메모리양을 입력하세요"
        />
      </div>

      <div className={styles.testcaseSection}>
        <p className={styles.testcaseLabel}>테스트 케이스</p>
        <div className={styles.testcaseInfo}>
          <img src={spacebar} alt="공백" className={styles.icon} /> 공백
          <img src={enter} alt="줄바꿈" className={styles.icon} /> 줄바꿈
          <img src={tab} alt="탭" className={styles.icon} /> 탭
        </div>

        <div className={styles.testcaseList}>
          {testcases.map((tc, idx) => (
            <div className={styles.testcaseRow} key={idx}>
              <input
                type="checkbox"
                checked={tc.checked}
                onChange={(e) =>
                  updateTestcase(idx, "checked", e.target.checked)
                }
              />
              <input
                className={styles.testcaseInput}
                value={tc.input}
                onChange={(e) => updateTestcase(idx, "input", e.target.value)}
                placeholder="입력 값 예시"
              />
              <input
                className={styles.testcaseInput}
                value={tc.output}
                onChange={(e) => updateTestcase(idx, "output", e.target.value)}
                placeholder="출력 값 예시"
              />
            </div>
          ))}
        </div>

        <button
          onClick={addTestcase}
          className={styles.addBtn}
          disabled={testcases.length >= 10}
        >
          테스트케이스 추가 ({testcases.length}/10)
        </button>
      </div>

      <button
        className={styles.submitBtn}
        disabled={!isFormValid()}
        onClick={() => {
          // 여기에 파일 추가 로직 추가 예정
        }}
      >
        파일 추가
      </button>
    </Modal>
  );
};

export default AddFileModal;
