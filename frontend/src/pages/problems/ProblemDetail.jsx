import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getProblemByIdApi } from "../../api/problemApi";
import { submitCodeApi } from "../../api/submissionApi";
import { getPublicTestcasesApi } from "../../api/testcaseApi";
import Editor from "@monaco-editor/react";

const ProblemDetail = () => {
  const { id } = useParams(); // ✅ SINGLE source of truth

  const [problem, setProblem] = useState(null);
  const [code, setCode] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [testcases, setTestcases] = useState([]);

  // 🔹 Fetch problem
  useEffect(() => {
    const fetchProblem = async () => {
      try {
        const data = await getProblemByIdApi(id);
        setProblem(data);
      } catch {
        setError("Failed to load problem");
      }
    };

    fetchProblem();
  }, [id]);

  // 🔹 Fetch public testcases
  useEffect(() => {
    const fetchTestcases = async () => {
      try {
        const data = await getPublicTestcasesApi(id);
        setTestcases(data);
      } catch (e) {
        console.error("Failed to load testcases", e);
      }
    };

    fetchTestcases();
  }, [id]);

  const handleSubmit = async () => {
    if (!code.trim()) {
      alert("Code cannot be empty");
      return;
    }

    setSubmitting(true);
    setError("");
    setResult(null);

    try {
      const submission = await submitCodeApi({
        problemId: id, // ✅ correct
        code,
      });

      setResult(submission);
    } catch {
      setError("Submission failed");
    } finally {
      setSubmitting(false);
    }
  };

  if (error) return <div>{error}</div>;
  if (!problem) return <div>Loading problem...</div>;

  return (
  <div>
    <h2>{problem.title}</h2>
    <p><b>Difficulty:</b> {problem.difficulty}</p>

    {/* TWO COLUMN LAYOUT */}
    <div style={{ display: "flex", gap: "20px", marginTop: 20 }}>

      {/* LEFT: PROBLEM DETAILS */}
      <div style={{ flex: 1, maxHeight: "70vh", overflowY: "auto" }}>
        <h3>Description</h3>
        <p>{problem.description}</p>

        {testcases.length > 0 && (
          <div style={{ marginTop: 20 }}>
            <h3>Sample Testcases</h3>
            {testcases.map((tc, idx) => (
              <div
                key={idx}
                style={{
                  background: "#111",
                  padding: 12,
                  borderRadius: 6,
                  marginBottom: 12,
                }}
              >
                <p><b>Input</b></p>
                <pre>{tc.input}</pre>

                <p><b>Expected Output</b></p>
                <pre>{tc.expectedOutput}</pre>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* RIGHT: CODE EDITOR */}
      <div style={{ flex: 1 }}>
        <h3>Your Code</h3>
        <Editor
          height="400px"
          language="python"
          theme="vs-dark"
          value={code}
          onChange={(value) => setCode(value || "")}
          options={{
            fontSize: 14,
            minimap: { enabled: false },
            scrollBeyondLastLine: false,
            readOnly: submitting,
          }}
        />
      </div>
    </div>

    {/* SUBMIT + RESULT */}
    <div style={{ marginTop: 20 }}>
      <button onClick={handleSubmit} disabled={submitting}>
        {submitting ? "Submitting..." : "Submit"}
      </button>

      {result && (
        <div style={{ marginTop: 20 }}>
          <h3>Result</h3>
          <p><b>Status:</b> {result.status}</p>
          <p><b>Output:</b> {result.output}</p>
        </div>
      )}
    </div>
  </div>
);

};

export default ProblemDetail;
