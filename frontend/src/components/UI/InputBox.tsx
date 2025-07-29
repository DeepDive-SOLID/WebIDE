export interface InputBoxProps {
  title: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  multiline?: boolean;
}

const InputBox = ({
  title,
  value,
  onChange,
  placeholder,
  multiline = false,
}: InputBoxProps) => {
  return (
    <div>
      <label>{title}</label>
      {multiline ? (
        <textarea
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
        />
      ) : (
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
        />
      )}
    </div>
  );
};

export default InputBox;
