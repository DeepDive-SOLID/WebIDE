import "../../styles/Bargraph.scss";

export interface BargraphProps {
  name: string;
  language: string;
  success: number;
  total: number;
}

const Bargraph = ({ name, language, success, total }: BargraphProps) => {
  const safeSuccess = Math.min(success, total);
  const percentage = total > 0 ? Math.round((safeSuccess / total) * 100) : 0;
  return (
    <div className="bargraph-wrapper">
      <div className="bargraph-user-info">
        <div className="bargraph-name">{name || <>&nbsp;</>}</div>
        <div className="bargraph-language">
          {" "}
          {language ? `(${language})` : <>&nbsp;</>}
        </div>
      </div>
      <div className="bargraph-bar-wrapper">
        <div className="bargraph-bar">
          <div
            className="bargraph-bar-fill"
            style={{ width: `${percentage}%` }}
          />
        </div>
      </div>
      <span className="bargraph-percent">{percentage}%</span>
    </div>
  );
};

export default Bargraph;
