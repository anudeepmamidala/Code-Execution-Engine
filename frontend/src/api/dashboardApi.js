import api from "./Axios";

export const getDashboardSummaryApi = async () => {
  const res = await api.get("/dashboard/summary");
  return res.data.data;
};
