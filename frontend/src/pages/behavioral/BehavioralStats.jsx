import { useEffect, useState } from "react";
import { getMyBehavioralStatsApi } from "../../api/behavioralApi";
import "./Behavioral.css";

const BehavioralStats = () => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    getMyBehavioralStatsApi().then(setStats);
  }, []);

  if (!stats) return <div className="behavioral-loader">Loading stats...</div>;

  return (
    <div className="behavioral-stats-container">
      <h2 className="behavioral-title">Behavioral Stats</h2>
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Total Answers</div>
          <div className="stat-value">{stats.totalAnswers}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Average Word Count</div>
          <div className="stat-value">{stats.averageWordCount}</div>
        </div>
      </div>
    </div>
  );
};

export default BehavioralStats;