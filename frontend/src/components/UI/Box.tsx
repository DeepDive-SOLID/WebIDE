import React from "react";
import styles from "../../styles/Box.module.scss";

export interface BoxProps {
  icon?: React.ReactNode;
  title: string;
  onClick?: () => void;
  cnt?: number;
  className?: string;
  onContextMenu?: (e: React.MouseEvent<HTMLButtonElement>) => void;
}

const Box = ({
  icon,
  title,
  onClick,
  onContextMenu,
  cnt,
  className = "",
}: BoxProps) => {
  return (
    <button
      className={`${styles.box} ${className}`}
      onClick={onClick}
      onContextMenu={onContextMenu}
    >
      <div className={styles.boxLeft}>
        <div className={styles.boxIcon}>{icon}</div>
        <span className={styles.boxTitle}>{title}</span>
      </div>
      {cnt !== undefined && <span className={styles.boxCount}>{cnt}</span>}
    </button>
  );
};

export default Box;
