import { useForm, useFieldArray } from "react-hook-form";
import Modal from "../UI/Modal";
import styles from "../../styles/AddFileModal.module.scss";
import { spacebar, enter, tab } from "../../assets";
import { createQuestion } from "../../api/questionApi";
import { useState } from "react";
import { createDirectory } from "../../api/directoryApi";
import { useDispatch, useSelector } from "react-redux";
import type { RootState } from "../../stores";
// import { setProblemEntries } from "../../stores/problemSlice";

interface AddFileModalProps {
  onClose: () => void;
  directoryId: number;
  onCreateComplete?: (newFile: { title: string; directoryId: number }) => void;
  selectedId: string;
  boxList: {
    id: string;
    directoryId: number;
    title: string;
    isProblem?: boolean;
    teamId?: number;
    directoryRoot: string;
  }[];
  create: (...args: any[]) => void;
  normalizePath: (path: string) => string;
  containerId: number;
}

interface FormValues {
  questionTitle: string;
  problem: string;
  inputDesc: string;
  outputDesc: string;
  timeLimit: string;
  memoryLimit: string;
  testcases: {
    input: string;
    output: string;
    checked: boolean;
  }[];
}

const AddFileModal = ({ onClose, directoryId, onCreateComplete, selectedId, boxList, create, normalizePath, containerId }: AddFileModalProps) => {
  const dispatch = useDispatch();
  const selectTeamId = useSelector((state: RootState) => state.problems.teamId);

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
    watch,
  } = useForm<FormValues>({
    defaultValues: {
      questionTitle: "",
      problem: "",
      inputDesc: "",
      outputDesc: "",
      timeLimit: "",
      memoryLimit: "",
      testcases: [
        { input: "", output: "", checked: true },
        { input: "", output: "", checked: true },
        { input: "", output: "", checked: true },
      ],
    },
  });

  const [select, setSelet] = useState(selectedId);

  const parent = boxList?.find((b) => b?.id === select);
  const directoryRoot = parent ? normalizePath(`${parent?.directoryRoot}/${parent?.title}`) : "/";
  const teamId = parent?.teamId ?? boxList[0]?.teamId ?? selectTeamId;

  const { fields, append } = useFieldArray({
    control,
    name: "testcases",
  });

  const onSubmit = async (data: FormValues) => {
    const validChecked = data.testcases.filter((tc) => tc.checked && tc.input.trim() && tc.output.trim());

    if (validChecked.length < 3) {
      alert("체크된 테스트 케이스가 3개 이상 필요합니다.");
      return;
    }

    try {
      // 문제 생성 API 호출
      await createQuestion({
        containerId: containerId, // 실제 값으로 교체
        teamId: teamId,
        questionTitle: data.questionTitle,
        questionDescription: "",
        question: data.problem,
        questionInput: data.inputDesc,
        questionOutput: data.outputDesc,
        questionTime: parseFloat(data.timeLimit),
        questionMem: parseInt(data.memoryLimit),
        testcases: data.testcases.map((tc) => ({
          caseEx: tc.input,
          caseAnswer: tc.output,
          caseCheck: tc.checked,
        })),
      });

      alert("문제 생성 성공!");

      const res = await createDirectory({
        containerId: containerId,
        teamId: teamId,
        directoryName: data.questionTitle,
        directoryRoot,
        directoryId: 0,
      });
      console.log(res);
      create(data.questionTitle, res?.directoryId, select, true, directoryRoot);

      onClose();
    } catch (e) {
      console.error(e);
      alert("문제 생성 실패");
    }
  };

  const watchTestcases = watch("testcases");

  return (
    <Modal onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className={styles.inputGroup}>
          <div className={styles.formControl}>
            <label htmlFor='questionTitle' className={styles.title}>
              문제 제목
            </label>
            <input id='questionTitle' {...register("questionTitle", { required: true })} placeholder='예: A + B 문제' />
            {errors.problem && <p className={styles.warning}>⚠ 문제 제목을 작성해주세요.</p>}
          </div>

          <div className={styles.formControl}>
            <label htmlFor='problem' className={styles.title}>
              문제
            </label>
            <textarea id='problem' {...register("problem", { required: true })} placeholder='문제를 작성하세요' />
            {errors.problem && <p className={styles.warning}>⚠ 문제를 작성해주세요.</p>}
          </div>

          <div className={styles.formControl}>
            <label htmlFor='inputDesc' className={styles.title}>
              입력
            </label>
            <textarea id='inputDesc' {...register("inputDesc", { required: true })} placeholder='입력 설명' />
            {errors.inputDesc && <p className={styles.warning}>⚠ 입력 설명이 필요합니다.</p>}
          </div>

          <div className={styles.formControl}>
            <label htmlFor='outputDesc' className={styles.title}>
              출력
            </label>
            <textarea id='outputDesc' {...register("outputDesc", { required: true })} placeholder='출력 설명' />
            {errors.outputDesc && <p className={styles.warning}>⚠ 출력 설명이 필요합니다.</p>}
          </div>

          <div className={styles.labeledInput}>
            <label htmlFor='timeLimit' className={styles.title}>
              시간 제한
            </label>
            <div className={styles.inlineInput}>
              <input
                id='timeLimit'
                type='number'
                {...register("timeLimit", {
                  required: true,
                  min: { value: 1, message: "1초 이상 입력해주세요." },
                })}
                placeholder='예: 2'
              />
              <span className={styles.unit}>초</span>
            </div>
            {errors.timeLimit && <p className={styles.warning}>⚠ 제한 시간을 입력해주세요.</p>}
          </div>

          <div className={styles.labeledInput}>
            <label htmlFor='memoryLimit' className={styles.title}>
              메모리 제한
            </label>
            <div className={styles.inlineInput}>
              <input
                id='memoryLimit'
                type='number'
                {...register("memoryLimit", {
                  required: true,
                  min: { value: 1, message: "1MB 이상 입력해주세요." },
                })}
                placeholder='예: 512'
              />
              <span className={styles.unit}>MB</span>
            </div>
            {errors.memoryLimit && <p className={styles.warning}>⚠ 메모리 제한을 입력해주세요.</p>}
          </div>
        </div>

        <div className={styles.testcaseSection}>
          <p className={styles.testcaseLabel}>테스트 케이스</p>
          <div className={styles.testcaseInfo}>
            <img src={spacebar} alt='공백' className={styles.icon} /> 공백
            <img src={enter} alt='줄바꿈' className={styles.icon} /> 줄바꿈
            <img src={tab} alt='탭' className={styles.icon} /> 탭
          </div>

          {fields.map((field, index) => (
            <div key={field.id} className={styles.testcaseRow}>
              <input type='checkbox' {...register(`testcases.${index}.checked` as const)} />
              <input className={styles.testcaseInput} placeholder='입력 값 예시' {...register(`testcases.${index}.input` as const)} />
              <input className={styles.testcaseInput} placeholder='출력 값 예시' {...register(`testcases.${index}.output` as const)} />
            </div>
          ))}

          {watchTestcases.filter((tc) => tc.checked && tc.input.trim() && tc.output.trim()).length < 3 && <p className={styles.testcaseWarning}>⚠ 체크된 테스트 케이스가 3개 이상 필요합니다.</p>}

          <button type='button' className={styles.addBtn} onClick={() => append({ input: "", output: "", checked: false })} disabled={fields.length >= 10}>
            테스트케이스 추가 ({fields.length}/10)
          </button>
        </div>

        <button className={styles.submitBtn} type='submit'>
          파일 추가
        </button>
      </form>
    </Modal>
  );
};

export default AddFileModal;
