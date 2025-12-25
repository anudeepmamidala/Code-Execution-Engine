import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  getAllTestcasesApi,
  createTestcaseApi,
  updateTestcaseApi,
  deleteTestcaseApi,
} from "../../api/testcaseApi";

const AdminTestcases = () => {
  const { problemId } = useParams();

  const [testcases, setTestcases] = useState([]);
  const [loading, setLoading] = useState(true);

  const [input, setInput] = useState("");
  const [expectedOutput, setExpectedOutput] = useState("");
  const [hidden, setHidden] = useState(true);

  const loadTestcases = async () => {
    setLoading(true);
    try {
      const data = await getAllTestcasesApi(problemId);
      setTestcases(data);
    } catch (err) {
      console.error("Failed to load testcases", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (problemId) loadTestcases();
  }, [problemId]);

  const handleCreate = async () => {
    if (!input.trim() || !expectedOutput.trim()) {
      alert("Input and expected output required");
      return;
    }

    await createTestcaseApi({
      problemId,
      input,
      expectedOutput,
      hidden,
    });

    setInput("");
    setExpectedOutput("");
    setHidden(true);

    loadTestcases();
  };

  const toggleHidden = async (tc) => {
    await updateTestcaseApi(tc.id, {
      input: tc.input,
      expectedOutput: tc.expectedOutput,
      hidden: !tc.hidden,
    });

    loadTestcases();
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete testcase?")) return;
    await deleteTestcaseApi(id);
    loadTestcases();
  };

  if (loading) return <div>Loading testcases...</div>;

  return (
    <div style={{ padding: 20 }}>
      <h2>Admin – Testcases</h2>

      {/* ADD TESTCASE */}
      <div style={{ marginBottom: 30 }}>
        <h3>Add Testcase</h3>

        <textarea
          placeholder="Input"
          rows={3}
          value={input}
          onChange={(e) => setInput(e.target.value)}
          style={{ width: "100%", marginBottom: 10 }}
        />

        <textarea
          placeholder="Expected Output"
          rows={2}
          value={expectedOutput}
          onChange={(e) => setExpectedOutput(e.target.value)}
          style={{ width: "100%", marginBottom: 10 }}
        />

        <label>
          <input
            type="checkbox"
            checked={hidden}
            onChange={(e) => setHidden(e.target.checked)}
          />{" "}
          Hidden
        </label>

        <br />
        <button onClick={handleCreate} style={{ marginTop: 10 }}>
          Add Testcase
        </button>
      </div>

      {/* LIST TESTCASES */}
      <h3>All Testcases</h3>

      {testcases.length === 0 ? (
        <p>No testcases</p>
      ) : (
        <table border="1" cellPadding="8" width="100%">
          <thead>
            <tr>
              <th>ID</th>
              <th>Input</th>
              <th>Expected Output</th>
              <th>Hidden</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {testcases.map((tc) => (
              <tr key={tc.id}>
                <td>{tc.id}</td>
                <td>
                  <pre>{tc.input}</pre>
                </td>
                <td>
                  <pre>{tc.expectedOutput}</pre>
                </td>
                <td>{tc.hidden ? "YES" : "NO"}</td>
                <td>
                  <button onClick={() => toggleHidden(tc)}>
                    Toggle Hidden
                  </button>{" "}
                  <button onClick={() => handleDelete(tc.id)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default AdminTestcases;
