import styles from "./question.module.scss";
import type { QusetionProp } from "../../../types/ide";

const Question = ({ question }: QusetionProp) => {
  return (
    // 중괄호에 들어간 문자열은 전부 데이터 받아와 변경
    <div className={styles.question_box}>
      <div>
        <h4>{"01. 기초 수학"}</h4>
        <h2>{question?.questionTitle}</h2>
      </div>
      <div>
        <h3>문제</h3>
        <p className={styles.question_length}>{question?.questionDescroption}</p>
      </div>
      <div>
        <h3>입력</h3>
        <p className={styles.question_length}>{question?.question}</p>
      </div>
      <div>
        <h3>출력</h3>
        <p className={styles.question_length}>{"더한 값만 나오고 출력한다."}</p>
      </div>
      <div className={styles.question_overflow}>
        <h3>예시</h3>
        <div>
          <h5>입력</h5>
          <div className={styles.question_ex_box}>
            <span>{"1_2"}</span>
          </div>
        </div>
        <div>
          <h5>출력</h5>
          <div className={styles.question_ex_box}>
            <span>{"3"}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Question;
