import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const RoleRoute = ({ role, children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role !== role) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

export default RoleRoute;
