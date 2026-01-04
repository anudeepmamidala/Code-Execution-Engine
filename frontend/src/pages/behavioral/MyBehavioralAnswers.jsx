import { useEffect, useState } from "react";
import { getMyBehavioralAnswersApi } from "../../api/behavioralApi";
import "./Behavioral.css";

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

  if (loading) return <div className="behavioral-loader">Loading answers...</div>;
  if (error) return <div className="behavioral-error">{error}</div>;
  if (answers.length === 0)
    return <div className="behavioral-empty">No answers submitted yet.</div>;

  return (
    <div className="behavioral-answers-container">
      <h2 className="behavioral-title">My Behavioral Answers</h2>

      <div className="answers-list">
        {answers.map((a) => (
          <div key={a.answerId} className="answer-card">
            <div className="answer-header">
              <span className="answer-date">
                {new Date(a.createdAt).toLocaleString()}
              </span>
            </div>

            <div className="answer-content">
              <div className="answer-section">
                <h4 className="section-title">Question</h4>
                <p className="section-text">{a.questionText}</p>
              </div>

              <div className="answer-section">
                <h4 className="section-title">Your Answer</h4>
                <p className="section-text answer-text">{a.answerText}</p>
              </div>

              <div className="answer-metrics">
                <div className="metric">
                  <span className="metric-label">Word Count:</span>
                  <span className="metric-value">{a.wordCount}</span>
                </div>
                <div className="metric">
                  <span className="metric-label">STAR Score:</span>
                  <span className="metric-value">{a.starScore} / 4</span>
                </div>
              </div>

              <div className="answer-feedback">
                <strong>Feedback:</strong>
                <p>{a.feedback}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MyBehavioralAnswers;