import api from "./Axios";

// ✅ USER – get public testcases
export const getPublicTestcasesApi = async (problemId) => {
  if (!problemId) throw new Error("problemId is required");

  const res = await api.get(`/testcases/problem/${problemId}/public`);
  return res.data.data;
};

// ✅ ADMIN – get all testcases
export const getAllTestcasesApi = async (problemId) => {
  const res = await api.get(`/testcases/problem/${problemId}/all`);
  return res.data.data;
};

export const createTestcaseApi = async (payload) => {
  const res = await api.post("/testcases", payload);
  return res.data.data;
};

export const updateTestcaseApi = async (id, payload) => {
  const res = await api.put(`/testcases/${id}`, payload);
  return res.data.data;
};

export const deleteTestcaseApi = async (id) => {
  const res = await api.delete(`/testcases/${id}`);
  return res.data.data;
};
