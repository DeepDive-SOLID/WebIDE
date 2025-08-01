import { useEffect, useState } from "react";
import styles from "../../../styles/test.module.scss";
import type { testApi } from "../../../types/ide";

const TestResult = ({ isCorrect, testcaseResults }: testApi) => {
  const [count, setCount] = useState<number>(0);
  // 테스트 버튼 클릭 전 보여줄 컴포넌트 구성

  // 테스트 클릭을 계속했을 시 생길 오류 처리
  useEffect(() => {
    if (count === testcaseResults?.length) {
      return;
    } else {
      setCount(0);
      testcaseResults?.map((item) => (item.pass ? setCount((prev) => prev + 1) : ""));
    }
  }, [count, testcaseResults]);

  return (
    <div className={styles.test_box}>
      <p className={styles.test_p}>테스트 케이스 일치 비율 {`${count} / ${testcaseResults?.length}`}</p>
      <div className={styles.test_table}>
        <table>
          <thead>
            <tr className={styles.test_head}>
              <th className={styles.test_tr1}>번호</th>
              <th className={styles.test_tr2}>입력값</th>
              <th className={styles.test_tr3}>출력값</th>
              <th className={styles.test_tr4}>예상 출력값</th>
              <th className={styles.test_tr5}>실행 결과</th>
            </tr>
          </thead>
          <tbody>
            {testcaseResults?.map((items, index) => (
              <tr key={index} className={styles.test_body}>
                <td className={styles.test_tr1}>{index + 1}</td>
                <td className={styles.test_tr2}>{items.input}</td>
                <td className={styles.test_tr3}>{items.output}</td>
                <td className={styles.test_tr4}>{items.actual}</td>
                <td className={styles.test_tr5}>{`${items.pass.toString().toLocaleUpperCase()}`}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TestResult;
