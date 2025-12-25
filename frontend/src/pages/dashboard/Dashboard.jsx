import { useEffect, useState } from "react";
import { getDashboardSummaryApi } from "../../api/dashboardApi";

const StatCard = ({ title, value }) => (
  <div
    style={{
      background: "#111",
      padding: 20,
      borderRadius: 8,
      minWidth: 180,
    }}
  >
    <p style={{ color: "#aaa", marginBottom: 6 }}>{title}</p>
    <h2 style={{ margin: 0 }}>{value}</h2>
  </div>
);

const Dashboard = () => {
  const [data, setData] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    getDashboardSummaryApi()
      .then(setData)
      .catch(() => setError("Failed to load dashboard"));
  }, []);

  if (error) return <div>{error}</div>;
  if (!data) return <div>Loading dashboard...</div>;

  return (
    <div style={{ padding: 20 }}>
      <h2>Dashboard</h2>

      {/* CODING STATS */}
      <h3 style={{ marginTop: 20 }}>Coding</h3>
      <div style={{ display: "flex", gap: 20, flexWrap: "wrap" }}>
        <StatCard
          title="Total Submissions"
          value={data.totalSubmissions}
        />
        <StatCard
          title="Successful Submissions"
          value={data.successfulSubmissions}
        />
        <StatCard
          title="Pass Rate (%)"
          value={data.passRate}
        />
      </div>

      {/* BEHAVIORAL STATS */}
      <h3 style={{ marginTop: 30 }}>Behavioral</h3>
      <div style={{ display: "flex", gap: 20, flexWrap: "wrap" }}>
        <StatCard
          title="Answers Given"
          value={data.behavioralAnswersCount}
        />
        <StatCard
          title="Avg Word Count"
          value={Math.round(data.averageBehavioralWordCount)}
        />
        <StatCard
          title="Avg STAR Score"
          value={data.averageStarScore.toFixed(2)}
        />
      </div>
    </div>
  );
};

export default Dashboard;
