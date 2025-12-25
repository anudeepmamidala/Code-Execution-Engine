import api from "./Axios";

export const getBehavioralQuestionsApi = async () => {
  const res = await api.get("/behavioral/questions");
  return res.data.data;
};

export const getBehavioralQuestionsByCategoryApi = async (category) => {
  const res = await api.get(`/behavioral/questions/category/${category}`);
  return res.data.data;
};

export const submitBehavioralAnswerApi = async (payload) => {
  const res = await api.post("/behavioral/answer", payload);
  return res.data.data; // includes starScore + feedback
};

export const getMyBehavioralAnswersApi = async () => {
  const res = await api.get("/behavioral/my-answers");
  return res.data.data;
};

export const getMyBehavioralStatsApi = async () => {
  const res = await api.get("/behavioral/my-stats");
  return res.data.data;
};
