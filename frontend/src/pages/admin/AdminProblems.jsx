import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  getAllProblemsApi,
  createProblemApi,
  updateProblemApi,
  deleteProblemApi,
} from "../../api/problemApi";

const emptyForm = {
  title: "",
  description: "",
  difficulty: "EASY",
};

const AdminProblems = () => {
  const [problems, setProblems] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  // 🔹 Load all problems
  const loadProblems = async () => {
    try {
      const data = await getAllProblemsApi();
      setProblems(data);
    } catch {
      setError("Failed to load problems");
    }
  };

  useEffect(() => {
    loadProblems();
  }, []);

  // 🔹 Handle form input
  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  // 🔹 Create / Update problem
  const handleSubmit = async () => {
    if (!form.title.trim() || !form.description.trim()) {
      alert("Title and description are required");
      return;
    }

    try {
      if (editingId) {
        await updateProblemApi(editingId, form);
      } else {
        await createProblemApi(form);
      }

      setForm(emptyForm);
      setEditingId(null);
      loadProblems();
    } catch {
      alert("Operation failed");
    }
  };

  // 🔹 Edit existing problem
  const handleEdit = (problem) => {
    setEditingId(problem.id);
    setForm({
      title: problem.title,
      description: problem.description,
      difficulty: problem.difficulty,
    });
  };

  // 🔹 Delete problem
  const handleDelete = async (id) => {
    const ok = window.confirm("Are you sure you want to delete this problem?");
    if (!ok) return;

    try {
      await deleteProblemApi(id);
      loadProblems();
    } catch {
      alert("Delete failed");
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Admin – Problems</h2>

      {/* ================= CREATE / EDIT FORM ================= */}
      <div
        style={{
          border: "1px solid #333",
          padding: 15,
          marginBottom: 30,
        }}
      >
        <h3>{editingId ? "Edit Problem" : "Create Problem"}</h3>

        <input
          type="text"
          name="title"
          placeholder="Title"
          value={form.title}
          onChange={handleChange}
          style={{ display: "block", marginBottom: 10, width: "100%" }}
        />

        <textarea
          name="description"
          placeholder="Description"
          rows={4}
          value={form.description}
          onChange={handleChange}
          style={{ display: "block", marginBottom: 10, width: "100%" }}
        />

        <select
          name="difficulty"
          value={form.difficulty}
          onChange={handleChange}
          style={{ marginBottom: 10 }}
        >
          <option value="EASY">EASY</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="HARD">HARD</option>
        </select>

        <br />

        <button onClick={handleSubmit}>
          {editingId ? "Update Problem" : "Create Problem"}
        </button>

        {editingId && (
          <button
            style={{ marginLeft: 10 }}
            onClick={() => {
              setEditingId(null);
              setForm(emptyForm);
            }}
          >
            Cancel
          </button>
        )}
      </div>

      {/* ================= PROBLEMS LIST ================= */}
      <h3>Existing Problems</h3>

      {error && <p style={{ color: "red" }}>{error}</p>}

      {problems.length === 0 && <p>No problems found</p>}

      {problems.map((p) => (
        <div
          key={p.id}
          style={{
            border: "1px solid #222",
            padding: 12,
            marginBottom: 10,
          }}
        >
          <h4>{p.title}</h4>

          <p>
            <b>Difficulty:</b> {p.difficulty}
          </p>

          <div style={{ marginTop: 10 }}>
            <button onClick={() => handleEdit(p)}>Edit</button>

            <button
              onClick={() => handleDelete(p.id)}
              style={{ marginLeft: 10 }}
            >
              Delete
            </button>

            {/* ✅ MANAGE TESTCASES */}
            <Link
              to={`/admin/problems/${p.id}/testcases`}
              style={{ marginLeft: 10 }}
            >
              <button>Manage Testcases</button>
            </Link>
          </div>
        </div>
      ))}
    </div>
  );
};

export default AdminProblems;
