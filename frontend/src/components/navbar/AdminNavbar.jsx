import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const AdminNavbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (!user || user.role !== "ROLE_ADMIN") return null;

  return (
    <nav
      style={{
        padding: "10px",
        borderBottom: "2px solid #ff4d4f",
        background: "#111",
        color: "#fff",
      }}
    >
      <strong style={{ marginRight: 20, color: "#ff4d4f" }}>
        CodeForge Admin
      </strong>

      <Link to="/admin/problems" style={{ marginRight: 10, color: "#fff" }}>
        Problems
      </Link>

      <Link to="/admin/behavioral" style={{ marginRight: 10, color: "#fff" }}>
        Behavioral
      </Link>

      <span style={{ marginLeft: 20, marginRight: 10 }}>
        {user.username}
      </span>

      <button onClick={handleLogout}>Logout</button>
    </nav>
  );
};

export default AdminNavbar;
