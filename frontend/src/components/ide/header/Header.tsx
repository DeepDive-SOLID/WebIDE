import { useEffect, useState } from "react";
import styles from "../../../styles/ideHeader.module.scss";
import type { HeaderProps } from "../../../types/ide";
import { getContainerMembers } from "../../../api/homeApi";
import type { GroupMemberResponseDto } from "../../../types/home";

const Header = ({ activeMember, handleOnClick, containerId }: HeaderProps) => {
  const [member, setMember] = useState<GroupMemberResponseDto[]>([]);

  useEffect(() => {
    const fetchContainerMember = async () => {
      try {
        const res = await getContainerMembers(Number(containerId));
        setMember(res);
      } catch (e) {
        console.error(e);
      }
    };
    fetchContainerMember();
  }, []);
  return (
    <header className={styles.header}>
      {member.map((user) => (
        <div key={user.memberId}>
          <button onClick={() => handleOnClick(user.memberId)} className={`${styles.button} ${activeMember === user.memberId ? styles.action : ""}`}>
            {user.memberName}
          </button>
        </div>
      ))}
    </header>
  );
};

export default Header;
