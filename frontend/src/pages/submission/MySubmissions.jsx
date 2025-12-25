import { useEffect, useState } from "react";
import { getMySubmissionsApi } from "../../api/submissionApi";
import { useNavigate } from "react-router-dom";

const MySubmissions = () => {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSubmissions = async () => {
      try {
        const data = await getMySubmissionsApi();
        setSubmissions(data);
      } catch (err) {
        alert("Failed to load submissions");
      } finally {
        setLoading(false);
      }
    };

    fetchSubmissions();
  }, []);

  if (loading) return <div>Loading submissions...</div>;
  if (submissions.length === 0) return <div>No submissions yet.</div>;

  return (
    <div>
      <h2>My Submissions</h2>

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>ID</th>
            <th>Problem ID</th>
            <th>Status</th>
            <th>Output</th>
          </tr>
        </thead>
        <tbody>
          {submissions.map((s) => (
            <tr
              key={s.submissionId}
              style={{ cursor: "pointer" }}
              onClick={() => navigate(`/submissions/${s.submissionId}`)}
            >
              <td>{s.submissionId}</td>
              <td>{s.problemId}</td>
              <td>{s.status}</td>
              <td>{s.output}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default MySubmissions;
