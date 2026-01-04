import { useEffect, useState } from "react";
import {
  getBehavioralQuestionsApi,
  submitBehavioralAnswerApi,
} from "../../api/behavioralApi";
import "./Behavioral.css";

const BehavioralQuestions = () => {
  const [questions, setQuestions] = useState([]);
  const [selected, setSelected] = useState(null);
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getBehavioralQuestionsApi().then((data) => {
      setQuestions(data);
      setLoading(false);
    });
  }, []);

  const submit = async () => {
    if (!answer.trim()) {
      alert("Answer cannot be empty");
      return;
    }

    const res = await submitBehavioralAnswerApi({
      questionId: selected.id,
      answerText: answer,
    });

    setResult(res);
    setAnswer("");
  };

  if (loading) return <div className="behavioral-loader">Loading questions...</div>;

  return (
    <div className="behavioral-questions-container">
      <h2 className="behavioral-title">Behavioral Questions</h2>

      <div className="behavioral-questions-list">
        {questions.map((q) => (
          <button
            key={q.id}
            className={`behavioral-question-btn ${selected?.id === q.id ? "active" : ""}`}
            onClick={() => {
              setSelected(q);
              setResult(null);
            }}
          >
            <span className="question-text">{q.questionText}</span>
            <span className="question-arrow">→</span>
          </button>
        ))}
      </div>

      {selected && (
        <div className="behavioral-answer-section">
          <h3>{selected.questionText}</h3>

          <textarea
            className="behavioral-textarea"
            rows={8}
            placeholder="Type your answer here..."
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
          />

          <button className="behavioral-submit-btn" onClick={submit}>
            Submit Answer
          </button>
        </div>
      )}

      {result && (
        <div className="behavioral-feedback-section">
          <h3>Feedback</h3>
          <div className="feedback-grid">
            <div className="feedback-item">
              <span className="feedback-label">Word Count:</span>
              <span className="feedback-value">{result.wordCount}</span>
            </div>
            <div className="feedback-item">
              <span className="feedback-label">STAR Score:</span>
              <span className="feedback-value">{result.starScore} / 4</span>
            </div>
          </div>
          <div className="feedback-text">
            <strong>Feedback:</strong>
            <p>{result.feedback}</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default BehavioralQuestions;