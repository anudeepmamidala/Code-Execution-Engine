import { createContext, useContext, useEffect, useState } from "react";
import { loginApi, getMeApi } from "../api/authApi";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // { id, username, role }
  const [loading, setLoading] = useState(true);

  const loadUser = async () => {
    try {
      const me = await getMeApi();

      // 🔐 NORMALIZE ROLE (CRITICAL)
      const normalizedUser = {
        ...me,
        role: me.role?.startsWith("ROLE_")
          ? me.role
          : `ROLE_${me.role}`,
      };

      setUser(normalizedUser);
    } catch (err) {
      localStorage.removeItem("token");
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      loadUser();
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (username, password) => {
    const authData = await loginApi(username, password);
    localStorage.setItem("token", authData.token);
    await loadUser();
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        logout,
        isAuthenticated: !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
