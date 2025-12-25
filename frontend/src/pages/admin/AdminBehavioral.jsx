import { useEffect, useState } from "react";
import api from "../../api/Axios";

const emptyForm = {
  questionText: "",
  category: "HR",
};

const categories = [
  "HR",
  "LEADERSHIP",
  "CONFLICT",
  "FAILURE",
  "TEAMWORK",
  "COMMUNICATION",
  "DECISION_MAKING",
];

const AdminBehavioral = () => {
  const [questions, setQuestions] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");

  const loadQuestions = async () => {
    try {
      const res = await api.get("/behavioral/questions");
      setQuestions(res.data.data);
    } catch {
      setError("Failed to load questions");
    }
  };

  useEffect(() => {
    loadQuestions();
  }, []);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async () => {
    if (!form.questionText) {
      alert("Question text required");
      return;
    }

    try {
      await api.post("/behavioral/questions", form);
      setForm(emptyForm);
      loadQuestions();
    } catch {
      alert("Create failed");
    }
  };

  const handleDelete = async (id) => {
    const ok = window.confirm("Disable this question?");
    if (!ok) return;

    try {
      await api.delete(`/behavioral/questions/${id}`);
      loadQuestions();
    } catch {
      alert("Delete failed");
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Admin – Behavioral Questions</h2>

      {/* CREATE FORM */}
      <div
        style={{
          border: "1px solid #333",
          padding: 15,
          marginBottom: 30,
        }}
      >
        <h3>Create Question</h3>

        <textarea
          name="questionText"
          placeholder="Question text"
          rows={3}
          value={form.questionText}
          onChange={handleChange}
          style={{ display: "block", width: "100%", marginBottom: 10 }}
        />

        <select
          name="category"
          value={form.category}
          onChange={handleChange}
          style={{ marginBottom: 10 }}
        >
          {categories.map((c) => (
            <option key={c} value={c}>
              {c}
            </option>
          ))}
        </select>

        <br />

        <button onClick={handleSubmit}>Create</button>
      </div>

      {/* LIST */}
      <h3>Existing Questions</h3>
      {error && <p>{error}</p>}

      {questions.map((q) => (
        <div
          key={q.id}
          style={{
            border: "1px solid #222",
            padding: 12,
            marginBottom: 10,
          }}
        >
          <p>
            <b>Category:</b> {q.category}
          </p>
          <p>{q.questionText}</p>

          <button onClick={() => handleDelete(q.id)}>
            Disable
          </button>
        </div>
      ))}
    </div>
  );
};

export default AdminBehavioral;
