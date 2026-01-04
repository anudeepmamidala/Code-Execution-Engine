import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getProblemByIdApi } from "../../api/problemApi";
import { submitCodeApi } from "../../api/submissionApi";
import { getPublicTestcasesApi } from "../../api/testcaseApi";
import Editor from "@monaco-editor/react";
import "./Problems.css";

const ProblemDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [problem, setProblem] = useState(null);
  const [code, setCode] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [testcases, setTestcases] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProblem = async () => {
      try {
        const data = await getProblemByIdApi(id);
        setProblem(data);
      } catch {
        setError("Failed to load problem");
      } finally {
        setLoading(false);
      }
    };

    fetchProblem();
  }, [id]);

  useEffect(() => {
    const fetchTestcases = async () => {
      try {
        const data = await getPublicTestcasesApi(id);
        setTestcases(data);
      } catch (e) {
        console.error("Failed to load testcases", e);
      }
    };

    if (id) fetchTestcases();
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
        problemId: id,
        code,
      });

      setResult(submission);
    } catch (err) {
      setError("Submission failed. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  if (error && loading) {
    return (
      <div className="problem-detail-page">
        <div className="container section">
          <div className="alert alert-error">{error}</div>
          <button className="btn btn-primary" onClick={() => navigate("/problems")}>
            ← Back to Problems
          </button>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="problem-detail-page">
        <div className="container section flex-center" style={{ minHeight: "500px" }}>
          <div className="loader"></div>
        </div>
      </div>
    );
  }

  if (!problem) {
    return (
      <div className="problem-detail-page">
        <div className="container section">
          <div className="alert alert-error">Problem not found</div>
        </div>
      </div>
    );
  }

  return (
    <div className="problem-detail-page">
      <div className="container section">
        
        {/* Header */}
        <div className="problem-header">
          <button className="btn btn-outline btn-small" onClick={() => navigate("/problems")}>
            ← Back
          </button>
          <div>
            <h1>{problem.title}</h1>
            <div className="problem-meta">
              <span className={`difficulty-badge difficulty-${problem.difficulty.toLowerCase()}`}>
                {problem.difficulty}
              </span>
              <span className="problem-id">Problem #{problem.id}</span>
            </div>
          </div>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        {/* Main Layout */}
        <div className="problem-layout">
          
          {/* LEFT PANEL - Description & Testcases */}
          <div className="problem-panel description-panel">
            <div className="panel-content">
              
              <div className="section-block">
                <h2 className="section-title">📝 Description</h2>
                <p className="description-text">{problem.description}</p>
              </div>

              {testcases.length > 0 && (
                <div className="section-block">
                  <h2 className="section-title">📋 Sample Testcases</h2>
                  <div className="testcases-list">
                    {testcases.map((tc, idx) => (
                      <div key={idx} className="testcase-card">
                        <div className="testcase-header">
                          <h4>Test Case {idx + 1}</h4>
                        </div>
                        
                        <div className="testcase-content">
                          <div className="testcase-input">
                            <label>Input:</label>
                            <pre><code>{tc.input}</code></pre>
                          </div>

                          <div className="testcase-output">
                            <label>Expected Output:</label>
                            <pre><code>{tc.expectedOutput}</code></pre>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* RIGHT PANEL - Editor */}
          <div className="problem-panel editor-panel">
            <div className="panel-header">
              <h2 className="section-title">💾 Solution</h2>
              <span className="language-badge">Python</span>
            </div>

            <div className="editor-container">
              <Editor
                height="500px"
                language="python"
                theme="vs-dark"
                value={code}
                onChange={(value) => setCode(value || "")}
                options={{
                  fontSize: 14,
                  minimap: { enabled: false },
                  scrollBeyondLastLine: false,
                  readOnly: submitting,
                  wordWrap: "on",
                }}
              />
            </div>

            {/* Submit Button */}
            <div className="submit-section">
              <button
                className="btn btn-primary submit-btn"
                onClick={handleSubmit}
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="spinner"></span>
                    Submitting...
                  </>
                ) : (
                  "✓ Submit Solution"
                )}
              </button>
            </div>
          </div>
        </div>

        {/* Result */}
        {result && (
          <div className="result-section">
            <h2 className="section-title">📊 Submission Result</h2>
            
            <div className={`result-card result-${result.status.toLowerCase()}`}>
              <div className="result-header">
                <span className="result-status">{result.status}</span>
                <span className={`result-badge result-badge-${result.status.toLowerCase()}`}>
                  {result.status === "PASSED" && "✓ All Tests Passed"}
                  {result.status === "FAILED" && "✗ Some Tests Failed"}
                  {result.status === "ERROR" && "⚠ Runtime Error"}
                </span>
              </div>

              <div className="result-details">
                <div className="result-output">
                  <strong>Output:</strong>
                  <p>{result.output}</p>
                </div>
              </div>

              <div className="result-actions">
                <button 
                  className="btn btn-secondary btn-small"
                  onClick={() => navigate(`/submissions`)}
                >
                  View All Submissions
                </button>
              </div>
            </div>
          </div>
        )}

      </div>
    </div>
  );
};

export default ProblemDetail;