import { useAuth } from "../../context/AuthContext";
import AdminNavbar from "./AdminNavbar";
import UserNavbar from "./UserNavbar";

const Navbar = () => {
  const { user } = useAuth();

  if (!user) return null;

  if (user.role === "ROLE_ADMIN") {
    return <AdminNavbar />;
  }

  return <UserNavbar />;
};

export default Navbar;
