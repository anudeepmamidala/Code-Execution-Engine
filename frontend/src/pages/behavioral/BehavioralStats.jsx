import { useEffect, useState } from "react";
import { getMyBehavioralStatsApi } from "../../api/behavioralApi";

const BehavioralStats = () => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    getMyBehavioralStatsApi().then(setStats);
  }, []);

  if (!stats) return <div>Loading stats...</div>;

  return (
    <div>
      <h2>Behavioral Stats</h2>
      <p><b>Total Answers:</b> {stats.totalAnswers}</p>
      <p><b>Average Word Count:</b> {stats.averageWordCount}</p>
    </div>
  );
};

export default BehavioralStats;
