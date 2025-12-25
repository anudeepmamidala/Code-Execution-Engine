import api from "./Axios"; // keep casing consistent with your choice

export const loginApi = async (username, password) => {
  const res = await api.post("/auth/login", {
    username,
    password,
  });
  return res.data.data; // AuthResponse
};

export const registerApi = async ({ username, email, password }) => {
  const res = await api.post("/auth/register", {
    username,
    email,
    password,
  });
  return res.data.data;
};

export const getMeApi = async () => {
  const res = await api.get("/auth/me");
  return res.data.data; // UserMeResponse
};
