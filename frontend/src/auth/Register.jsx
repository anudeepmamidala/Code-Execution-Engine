import { useState } from "react";
import { registerApi } from "../api/authApi";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!username || !email || !password) {
      setError("All fields are required");
      return;
    }

    try {
      setLoading(true);

      await registerApi({
        username,
        email,
        password,
      });

      // Backend only registers user, does not auto-login
      navigate("/login");
    } catch (err) {
      setError("Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Register</h2>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <form onSubmit={handleSubmit}>
        <input
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button type="submit" disabled={loading}>
          {loading ? "Registering..." : "Register"}
        </button>
      </form>
    </div>
  );
};

export default Register;
