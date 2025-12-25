import { useEffect, useState } from "react";
import {
  getBehavioralQuestionsApi,
  submitBehavioralAnswerApi,
} from "../../api/behavioralApi";

const BehavioralQuestions = () => {
  const [questions, setQuestions] = useState([]);
  const [selected, setSelected] = useState(null);
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState(null);

  useEffect(() => {
    getBehavioralQuestionsApi().then(setQuestions);
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

  return (
    <div>
      <h2>Behavioral Questions</h2>

      <ul>
        {questions.map((q) => (
          <li key={q.id}>
            <button onClick={() => {
              setSelected(q);
              setResult(null);
            }}>
              {q.questionText}
            </button>
          </li>
        ))}
      </ul>

      {selected && (
        <div style={{ marginTop: 20 }}>
          <h3>{selected.questionText}</h3>

          <textarea
            rows={6}
            cols={70}
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
          />

          <br />
          <button onClick={submit}>Submit</button>
        </div>
      )}

      {result && (
        <div style={{ marginTop: 20 }}>
          <h3>Feedback</h3>
          <p><b>Word Count:</b> {result.wordCount}</p>
          <p><b>STAR Score:</b> {result.starScore} / 4</p>
          <p><b>Feedback:</b> {result.feedback}</p>
        </div>
      )}
    </div>
  );
};

export default BehavioralQuestions;
