import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="dashboard">
      <div className="container section">
        
        {/* Header */}
        <div className="dashboard-header">
          <h1>Welcome, {user?.username}! 👋</h1>
          <p>Continue your coding interview preparation</p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-3">
          <div className="stat-card primary">
            <div>
              <div className="stat-label">Coding Problems</div>
              <div className="stat-value">12</div>
            </div>
            <div className="stat-icon">💻</div>
          </div>

          <div className="stat-card success">
            <div>
              <div className="stat-label">Submissions</div>
              <div className="stat-value">24</div>
            </div>
            <div className="stat-icon">✅</div>
          </div>

          <div className="stat-card danger">
            <div>
              <div className="stat-label">Behavioral</div>
              <div className="stat-value">8</div>
            </div>
            <div className="stat-icon">🎤</div>
          </div>
        </div>

        {/* Content Grid */}
        <div className="grid grid-2" style={{ marginTop: "2rem" }}>
          
          {/* Quick Actions */}
          <div className="card">
            <h3>🚀 Quick Actions</h3>
            <div className="action-buttons">
              <button 
                className="btn btn-primary"
                onClick={() => navigate("/problems")}
              >
                Start Coding Problem
              </button>
              <button 
                className="btn btn-primary"
                onClick={() => navigate("/behavioral")}
              >
                Practice Behavioral
              </button>
              <button 
                className="btn btn-primary"
                onClick={() => navigate("/submissions")}
              >
                Review Submissions
              </button>
            </div>
          </div>

          {/* Progress */}
          <div className="card">
            <h3>📊 Your Progress</h3>
            <div className="progress-section">
              <div className="progress-item">
                <div className="flex-between" style={{ marginBottom: "0.5rem" }}>
                  <span>Completion</span>
                  <span className="badge badge-primary">65%</span>
                </div>
                <div className="progress-bar">
                  <div className="progress-fill" style={{ width: "65%" }}></div>
                </div>
              </div>

              <div className="progress-item">
                <div className="flex-between" style={{ marginBottom: "0.5rem" }}>
                  <span>Success Rate</span>
                  <span className="badge badge-success">78%</span>
                </div>
                <div className="progress-bar">
                  <div className="progress-fill" style={{ width: "78%", backgroundColor: "#10b981" }}></div>
                </div>
              </div>
            </div>
          </div>

        </div>

        {/* Recent Activity */}
        <div className="card" style={{ marginTop: "2rem" }}>
          <h3>📝 Recent Activity</h3>
          <table>
            <thead>
              <tr>
                <th>Problem</th>
                <th>Status</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Two Sum</td>
                <td><span className="badge badge-success">Solved</span></td>
                <td>2 days ago</td>
              </tr>
              <tr>
                <td>Longest Substring</td>
                <td><span className="badge badge-success">Solved</span></td>
                <td>3 days ago</td>
              </tr>
              <tr>
                <td>Binary Tree Traversal</td>
                <td><span className="badge badge-warning">Attempting</span></td>
                <td>Today</td>
              </tr>
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;