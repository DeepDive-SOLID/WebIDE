import React, { useEffect, useState } from "react";
import styles from "./test.module.scss";

const TestResult = () => {
  const [count, setCount] = useState<number>(0);

  useEffect(() => {
    a.map((item) => (item.result === "true" ? setCount((prev) => prev + 1) : ""));
  }, []);
  const a = [
    {
      time: 0.1,
      mem: 0.0,
      input: "10 20",
      output: "30",
      ac: "30",
      result: "true",
    },
    {
      time: 0.1,
      mem: 0.0,
      input: `10 20 \n 20 30`,
      output: "30",
      ac: "30",
      result: "true",
    },
    {
      time: 0.1,
      mem: 0.0,
      input: "10 20",
      output: "30",
      ac: "30",
      result: "false",
    },
  ];
  return (
    <div className={styles.test_box}>
      <p className={styles.test_p}>테스트 케이스 일치 비율 {`${count} / ${a.length}`}</p>
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
            {a.map((items, index) => (
              <tr className={styles.test_body}>
                <td className={styles.test_tr1}>{index + 1}</td>
                <td className={styles.test_tr2}>{items.input}</td>
                <td className={styles.test_tr3}>{items.output}</td>
                <td className={styles.test_tr4}>{items.ac}</td>
                <td className={styles.test_tr5}>{items.result}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TestResult;
