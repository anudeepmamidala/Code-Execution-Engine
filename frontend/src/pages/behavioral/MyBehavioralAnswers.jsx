import { useEffect, useState } from "react";
import { getMyBehavioralAnswersApi } from "../../api/behavioralApi";

const MyBehavioralAnswers = () => {
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const data = await getMyBehavioralAnswersApi();
        setAnswers(data);
      } catch {
        setError("Failed to load behavioral answers");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <div>Loading behavioral answers...</div>;
  if (error) return <div>{error}</div>;
  if (answers.length === 0) return <div>No answers submitted yet.</div>;

  return (
    <div style={{ maxWidth: 900, margin: "auto" }}>
      <h2>My Behavioral Answers</h2>

      {answers.map((a) => (
        <div
          key={a.answerId}
          style={{
            border: "1px solid #333",
            borderRadius: 8,
            padding: 16,
            marginBottom: 20,
            background: "#111",
          }}
        >
          <p style={{ opacity: 0.7 }}>
            Submitted on {new Date(a.createdAt).toLocaleString()}
          </p>

          <h4>Question</h4>
          <p>{a.questionText}</p>

          <h4>Your Answer</h4>
          <p style={{ whiteSpace: "pre-wrap" }}>{a.answerText}</p>

          <p><b>Word Count:</b> {a.wordCount}</p>
          <p><b>STAR Score:</b> {a.starScore} / 4</p>
          <p><b>Feedback:</b> {a.feedback}</p>
        </div>
      ))}
    </div>
  );
};

export default MyBehavioralAnswers;
