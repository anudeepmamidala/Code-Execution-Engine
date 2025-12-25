import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const UserNavbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (!user) return null;

  return (
    <nav style={{ padding: "10px", borderBottom: "1px solid #ccc" }}>
      <strong style={{ marginRight: 20 }}>CodeForge</strong>

      <Link to="/dashboard" style={{ marginRight: 10 }}>
        Dashboard
      </Link>

      <Link to="/problems" style={{ marginRight: 10 }}>
        Problems
      </Link>

      <Link to="/submissions" style={{ marginRight: 10 }}>
        My Submissions
      </Link>

      <Link to="/behavioral" style={{ marginRight: 10 }}>
        Behavioral Questions
      </Link>

      <Link to="/behavioral/my-answers" style={{ marginRight: 10 }}>
        My Answers
      </Link>

      <Link to="/behavioral/stats" style={{ marginRight: 10 }}>
        Behavioral Stats
      </Link>

      <span style={{ marginLeft: 20, marginRight: 10 }}>
        {user.username}
      </span>

      <button onClick={handleLogout}>Logout</button>
    </nav>
  );
};

export default UserNavbar;
