import React from "react";

export interface ButtonProps {
  onClick: () => void;
  icon: React.ReactNode;
  className: string;
}

const Button: React.FC<ButtonProps> = ({ onClick, icon, className }) => {
  return (
    <button className={className} onClick={onClick}>
      {icon}
    </button>
  );
};
export default Button;
