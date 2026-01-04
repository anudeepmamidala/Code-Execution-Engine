import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useState } from "react";
import "./Navbar.css";

const UserNavbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login");
    setMenuOpen(false);
  };

  if (!user) return null;

  return (
    <nav className="navbar navbar-user">
      <div className="navbar-container">
        
        {/* Logo */}
        <Link to="/dashboard" className="navbar-logo">
          <span>🚀</span>
          <span>CodeForge</span>
        </Link>

        {/* Desktop Menu */}
        <div className="navbar-menu">
          <Link to="/dashboard" className="navbar-link">Dashboard</Link>
          <Link to="/problems" className="navbar-link">Problems</Link>
          <Link to="/submissions" className="navbar-link">Submissions</Link>
          
          <div className="navbar-divider"></div>
          
          <Link to="/behavioral" className="navbar-link">Behavioral</Link>
          <Link to="/behavioral/my-answers" className="navbar-link">My Answers</Link>
          <Link to="/behavioral/stats" className="navbar-link">Stats</Link>
        </div>

        {/* User Menu */}
        <div className="navbar-user-menu">
          <div className="navbar-user-info" onClick={() => setMenuOpen(!menuOpen)}>
            <span className="user-icon">👤</span>
            <span className="username">{user.username}</span>
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

export default UserNavbar;