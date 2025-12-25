import { useEffect, useState } from "react";
import { getAllProblemsApi } from "../../api/problemApi";
import { useNavigate } from "react-router-dom";

const ProblemsList = () => {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProblems = async () => {
      try {
        const data = await getAllProblemsApi();
        setProblems(data);
      } catch (err) {
        alert("Failed to load problems");
      } finally {
        setLoading(false);
      }
    };

    fetchProblems();
  }, []);

  if (loading) return <div>Loading problems...</div>;

  return (
    <div>
      <h2>Problems</h2>

      <ul>
        {problems.map((p) => (
          <li
            key={p.id}
            style={{ cursor: "pointer" }}
            onClick={() => navigate(`/problems/${p.id}`)}
          >
            <b>{p.title}</b> — {p.difficulty}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ProblemsList;
