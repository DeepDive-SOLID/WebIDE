import React from "react";
import "../../styles/Box.scss";

export interface BoxProps {
  icon?: React.ReactNode;
  title: string;
  onClick?: () => void;
  cnt?: number;
  className?: string;
}

const Box = ({ icon, title, onClick, cnt, className = "" }: BoxProps) => {
  return (
    <button className={`box ${className}`} onClick={onClick}>
      <div className="box-left">
        <div className="box-icon">{icon}</div>
        <span className="box-title">{title}</span>
      </div>
      {cnt !== undefined && <span className="box-count">{cnt}</span>}
    </button>
  );
};

export default Box;
