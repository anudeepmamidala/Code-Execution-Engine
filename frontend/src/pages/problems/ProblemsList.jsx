import { useEffect, useState } from "react";
import { getAllProblemsApi } from "../../api/problemApi";
import { useNavigate } from "react-router-dom";
import "./Problems.css";

const ProblemsList = () => {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProblems = async () => {
      try {
        const data = await getAllProblemsApi();
        setProblems(data);
      } catch (err) {
        setError("Failed to load problems");
      } finally {
        setLoading(false);
      }
    };

    fetchProblems();
  }, []);

  if (loading) {
    return (
      <div className="problems-container">
        <div className="loader"></div>
        <p>Loading problems...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="problems-container">
        <div className="alert alert-error">{error}</div>
      </div>
    );
  }

  return (
    <div className="problems-page">
      <div className="container section">
        
        {/* Header */}
        <div className="page-header">
          <h1>💻 Coding Problems</h1>
          <p>Practice your coding skills with diverse problems</p>
        </div>

        {/* Stats */}
        <div className="problems-stats">
          <div className="stat">
            <span className="stat-number">{problems.length}</span>
            <span className="stat-label">Total Problems</span>
          </div>
          <div className="stat">
            <span className="stat-number">{problems.filter(p => p.difficulty === "EASY").length}</span>
            <span className="stat-label">Easy</span>
          </div>
          <div className="stat">
            <span className="stat-number">{problems.filter(p => p.difficulty === "MEDIUM").length}</span>
            <span className="stat-label">Medium</span>
          </div>
          <div className="stat">
            <span className="stat-number">{problems.filter(p => p.difficulty === "HARD").length}</span>
            <span className="stat-label">Hard</span>
          </div>
        </div>

        {/* Problems Grid */}
        {problems.length === 0 ? (
          <div className="empty-state">
            <span className="empty-icon">📭</span>
            <h3>No Problems Available</h3>
            <p>Come back soon for more coding challenges!</p>
          </div>
        ) : (
          <div className="problems-grid">
            {problems.map((problem, idx) => (
              <div
                key={problem.id}
                className="problem-card"
                onClick={() => navigate(`/problems/${problem.id}`)}
              >
                <div className="problem-number">#{idx + 1}</div>
                
                <h3 className="problem-title">{problem.title}</h3>
                
                <p className="problem-description">
                  {problem.description?.substring(0, 100)}...
                </p>

                <div className="problem-footer">
                  <span className={`difficulty-badge difficulty-${problem.difficulty.toLowerCase()}`}>
                    {problem.difficulty}
                  </span>
                  <span className="action-arrow">→</span>
                </div>
              </div>
            ))}
          </div>
        )}

      </div>
    </div>
  );
};

export default ProblemsList;