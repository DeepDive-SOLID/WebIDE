import { useDispatch, useSelector } from "react-redux";
import styles from "../../../styles/question.module.scss";
import type { RootState } from "../../../stores";
import { useEffect, useState } from "react";
import { getQuestionListByContainerId, TestCaseQuestion } from "../../../api/questionApi";
import type { QuestionListDto, test } from "../../../types/question";
import { setQuestionId } from "../../../stores/problemSlice";

interface QuestionProp {
  containerId: number;
}

const Question = ({ containerId }: QuestionProp) => {
  const title = useSelector((state: RootState) => state.problems.title);
  const root = useSelector((state: RootState) => state.problems.root);
  const dispatch = useDispatch();
  const [question, setQuestion] = useState<QuestionListDto[]>();
  const [testCase, setTestCase] = useState<test[]>();

  useEffect(() => {
    const questionFetch = async () => {
      try {
        const res = await getQuestionListByContainerId(containerId);
        const filterQuestion = res?.filter((item) => item.questionTitle === title);
        setQuestion(filterQuestion);
        dispatch(setQuestionId(filterQuestion[0]?.questionId));
        if (filterQuestion.length === 0) {
          setTestCase([]);
          return;
        }

        const testRes = await TestCaseQuestion(filterQuestion?.map((i) => i?.questionId)[0]);
        setTestCase(testRes);
      } catch (e) {
        console.error(e);
      }
    };
    questionFetch();
  }, [title, containerId]);

  return (
    // 중괄호에 들어간 문자열은 전부 데이터 받아와 변경
    <div className={styles.question_box}>
      {question?.map((item) => (
        <span key={item.questionId}>
          <div>
            {/* 디렉토리 명 받기 */}
            <h4>{root}</h4>
            <h2>{item.questionTitle}</h2>
          </div>
          <div>
            <h3>문제</h3>
            <p className={styles.question_length}>{item.question}</p>
          </div>
          <div>
            <h3>입력</h3>
            <p className={styles.question_length}>{item.questionInput}</p>
          </div>
          <div>
            <h3>출력</h3>
            <p className={styles.question_length}>{item.questionOutput}</p>
          </div>
        </span>
      ))}

      <div className={styles.question_overflow}>
        <h3>예시</h3>
        <div>
          <h5>입력</h5>
          <div className={styles.question_ex_box}>
            {/* 테스트 케이스가 나와야함 */}
            {testCase?.map((item) => (
              <span key={item.caseId}>{item.caseEx}</span>
            ))}
          </div>
        </div>
        <div>
          <h5>출력</h5>
          <div className={styles.question_ex_box}>
            {testCase?.map((item) => (
              <span key={item.caseId}>{item.caseAnswer}</span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Question;
