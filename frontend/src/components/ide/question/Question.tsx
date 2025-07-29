import styles from "../../../styles/question.module.scss";
import type { QuestionListDto } from "../../../types/question";

const Question = ({ question }: QuestionListDto) => {
  return (
    // 중괄호에 들어간 문자열은 전부 데이터 받아와 변경
    <div className={styles.question_box}>
      <div>
        {/* 디렉토리 명 받기 */}
        <h4>{"01. 기초 수학"}</h4>
        <h2>{question?.questionTitle}</h2>
      </div>
      <div>
        <h3>문제</h3>
        <p className={styles.question_length}>{question?.questionDescription}</p>
      </div>
      <div>
        <h3>입력</h3>
        <p className={styles.question_length}>{question?.questionInput}</p>
      </div>
      <div>
        <h3>출력</h3>
        <p className={styles.question_length}>{question?.questionOutput}</p>
      </div>
      <div className={styles.question_overflow}>
        <h3>예시</h3>
        <div>
          <h5>입력</h5>
          <div className={styles.question_ex_box}>
            {/* 테스트 케이스가 나와야함 */}
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
