// src/api/problemApi.js
import api from "./Axios";

/**
 * USER + ADMIN
 * GET /api/problems
 */
export const getAllProblemsApi = async () => {
  const res = await api.get("/problems");
  return res.data.data; // List<ProblemResponse>
};

/**
 * USER + ADMIN
 * GET /api/problems/{id}
 */
export const getProblemByIdApi = async (id) => {
  if (!id) throw new Error("Problem ID is required");

  const res = await api.get(`/problems/${id}`);
  return res.data.data; // ProblemResponse
};

/**
 * ADMIN ONLY
 * POST /api/problems
 */
export const createProblemApi = async (problem) => {
  if (!problem) throw new Error("Problem payload missing");

  const res = await api.post("/problems", problem);
  return res.data.data;
};

/**
 * ADMIN ONLY
 * PUT /api/problems/{id}
 */
export const updateProblemApi = async (id, problem) => {
  if (!id || !problem) {
    throw new Error("Problem ID or payload missing");
  }

  const res = await api.put(`/problems/${id}`, problem);
  return res.data.data;
};

/**
 * ADMIN ONLY
 * DELETE /api/problems/{id}
 */
export const deleteProblemApi = async (id) => {
  if (!id) throw new Error("Problem ID is required");

  const res = await api.delete(`/problems/${id}`);
  return res.data.data;
};
