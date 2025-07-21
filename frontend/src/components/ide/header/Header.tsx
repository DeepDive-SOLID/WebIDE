import { useEffect, useState } from "react";
import styles from "./header.module.scss";
import type { HeaderProps, member } from "../../../types/ide";

const Header = ({ activeMember, handleOnClick }: HeaderProps) => {
  const [member, setMember] = useState<member[]>([]);

  useEffect(() => {
    // 해당 컨테이너 유저를 가져오는 api
    // 더미 데이터
    setMember([
      { id: "1", name: "l" },
      { id: "2", name: "k" },
      { id: "3", name: "p" },
    ]);
  }, []);
  return (
    <header className={styles.header}>
      {member.map((user) => (
        <div key={user.id}>
          <button onClick={() => handleOnClick(user.id)} className={`${styles.button} ${activeMember === user.id ? styles.action : ""}`}>
            {user.name}
          </button>
        </div>
      ))}
    </header>
  );
};

export default Header;
