// src/api/submissionApi.js
import api from "./Axios";

/**
 * POST /api/submissions
 */
export const submitCodeApi = async ({ problemId, code }) => {
  if (!problemId) throw new Error("problemId is required");
  if (!code) throw new Error("code is required");

  const res = await api.post("/submissions", {
    problemId,
    code,
  });

  // ✅ ApiResponse<SubmissionResponse>
  return res.data.data;
};

/**
 * GET /api/submissions/{submissionId}
 */
export const getSubmissionDetailApi = async (submissionId) => {
  if (!submissionId) throw new Error("submissionId is required");

  const res = await api.get(`/submissions/${submissionId}`);

  // ✅ ApiResponse<SubmissionDetailResponse>
  return res.data.data;
};

/**
 * GET /api/submissions/my
 */
export const getMySubmissionsApi = async () => {
  const res = await api.get("/submissions/my");

  // ✅ ApiResponse<List<SubmissionResponse>>
  return res.data.data;
};
