import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getSubmissionDetailApi } from "../../api/submissionApi";
import Editor from "@monaco-editor/react";

const SubmissionDetail = () => {
  const { id } = useParams(); // ✅ MUST match route param :id

  const [submission, setSubmission] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const data = await getSubmissionDetailApi(id);
        setSubmission(data);
      } catch (err) {
        console.error(err);
        setError("Failed to load submission");
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchDetail();
  }, [id]);

  if (loading) return <div>Loading submission...</div>;
  if (error) return <div>{error}</div>;
  if (!submission) return <div>No submission found</div>;

  return (
    <div style={{ maxWidth: 900, margin: "auto" }}>
      <h2>Submission #{submission.submissionId}</h2>

      <p>
        <b>Status:</b> {submission.status}
      </p>

      <p>
        <b>Result:</b> {submission.output}
      </p>

      <p>
        <b>Submitted At:</b>{" "}
        {new Date(submission.createdAt).toLocaleString()}
      </p>

      {/* ================= SUBMITTED CODE ================= */}
      {submission.code && (
        <div style={{ marginTop: 20 }}>
          <h3>Submitted Code</h3>
          <Editor
            height="350px"
            language="python"
            theme="vs-dark"
            value={submission.code}
            options={{
              readOnly: true,
              fontSize: 14,
              minimap: { enabled: false },
              scrollBeyondLastLine: false,
            }}
          />
        </div>
      )}

      {/* ================= TESTCASE RESULTS ================= */}
      {submission.testcaseResults && submission.testcaseResults.length > 0 && (
        <div style={{ marginTop: 20 }}>
          <h3>Testcase Results</h3>
          <ul>
            {submission.testcaseResults.map((r) => (
              <li key={r.testcaseId}>
                Testcase {r.testcaseId} —{" "}
                {r.passed ? "PASSED" : "FAILED"}
                {r.executionTime !== null && (
                  <> ({r.executionTime} ms)</>
                )}
                {r.error && r.error.trim() !== "" && (
                  <div style={{ color: "red" }}>
                    Error: {r.error}
                  </div>
                )}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default SubmissionDetail;
