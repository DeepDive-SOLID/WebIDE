import React from "react";

export interface ButtonProps {
  onClick: () => void;
  icon: React.ReactNode;
  className: string;
}

const Button = ({ onClick, icon, className }: ButtonProps) => {
  return (
    <button className={className} onClick={onClick}>
      {icon}
    </button>
  );
};
export default Button;
