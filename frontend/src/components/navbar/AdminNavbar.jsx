import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useState } from "react";
import "./Navbar.css";

const AdminNavbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login");
    setMenuOpen(false);
  };

  if (!user || user.role !== "ROLE_ADMIN") return null;

  return (
    <nav className="navbar navbar-admin">
      <div className="navbar-container">
        
        {/* Logo */}
        <Link to="/dashboard" className="navbar-logo admin-logo">
          <span>⚙️</span>
          <span>CodeForge Admin</span>
        </Link>

        {/* Desktop Menu */}
        <div className="navbar-menu">
          <Link to="/admin/problems" className="navbar-link">Problems</Link>
          <Link to="/admin/behavioral" className="navbar-link">Behavioral</Link>
          <div className="navbar-divider"></div>
          <Link to="/dashboard" className="navbar-link back-link">← Back to User</Link>
        </div>

        {/* User Menu */}
        <div className="navbar-user-menu">
          <div className="navbar-user-info admin" onClick={() => setMenuOpen(!menuOpen)}>
            <span className="user-icon">🔐</span>
            <span className="username">{user.username}</span>
            <span className="admin-badge">ADMIN</span>
            <span className="dropdown-arrow">▼</span>
          </div>

          {menuOpen && (
            <div className="navbar-dropdown">
              <button onClick={handleLogout} className="dropdown-item logout-btn">
                🚪 Logout
              </button>
            </div>
          )}
        </div>

        {/* Mobile Menu Button */}
        <button className="navbar-toggle">☰</button>
      </div>
    </nav>
  );
};

export default AdminNavbar;