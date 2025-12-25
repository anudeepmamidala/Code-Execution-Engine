import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import RoleRoute from "./routes/RoleRoute";

import Login from "./auth/Login";
import Register from "./auth/Register";

import Navbar from "./components/navbar/Navbar";

import ProblemsList from "./pages/problems/ProblemsList";
import ProblemDetail from "./pages/problems/ProblemDetail";

import MySubmissions from "./pages/submission/MySubmissions";
import SubmissionDetail from "./pages/submission/SubmissionDetail";

import BehavioralQuestions from "./pages/behavioral/BehavioralQuestions";
import MyBehavioralAnswers from "./pages/behavioral/MyBehavioralAnswers";
import BehavioralStats from "./pages/behavioral/BehavioralStats";

import Dashboard from "./pages/dashboard/Dashboard";

import AdminProblems from "./pages/admin/AdminProblems";
import AdminTestcases from "./pages/admin/AdminTestcases";
import AdminBehavioral from "./pages/admin/AdminBehavioral";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>

          {/* PUBLIC */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* DASHBOARD */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <Dashboard />
                </>
              </ProtectedRoute>
            }
          />

          {/* PROBLEMS */}
          <Route
            path="/problems"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <ProblemsList />
                </>
              </ProtectedRoute>
            }
          />

          <Route
            path="/problems/:id"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <ProblemDetail />
                </>
              </ProtectedRoute>
            }
          />

          {/* SUBMISSIONS */}
          <Route
            path="/submissions"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <MySubmissions />
                </>
              </ProtectedRoute>
            }
          />

          <Route
            path="/submissions/:id"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <SubmissionDetail />
                </>
              </ProtectedRoute>
            }
          />

          {/* BEHAVIORAL */}
          <Route
            path="/behavioral"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <BehavioralQuestions />
                </>
              </ProtectedRoute>
            }
          />

          <Route
            path="/behavioral/my-answers"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <MyBehavioralAnswers />
                </>
              </ProtectedRoute>
            }
          />

          <Route
            path="/behavioral/stats"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <BehavioralStats />
                </>
              </ProtectedRoute>
            }
          />

          {/* ADMIN (ROLE BASED) */}
          <Route
            path="/admin/problems"
            element={
              <RoleRoute role="ROLE_ADMIN">
                <>
                  <Navbar />
                  <AdminProblems />
                </>
              </RoleRoute>
            }
          />

          <Route
            path="/admin/testcases/:problemId"
            element={
              <RoleRoute role="ROLE_ADMIN">
                <>
                  <Navbar />
                  <AdminTestcases />
                </>
              </RoleRoute>
            }
          />

          <Route
            path="/admin/behavioral"
            element={
              <RoleRoute role="ROLE_ADMIN">
                <>
                  <Navbar />
                  <AdminBehavioral />
                </>
              </RoleRoute>
            }
          />

          {/* DEFAULT */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <Dashboard />
                </>
              </ProtectedRoute>
            }
          />

            <Route
  path="/admin/problems/:problemId/testcases"
  element={
    <ProtectedRoute>
      <>
        <Navbar />
        <AdminTestcases />
      </>
    </ProtectedRoute>
  }
/>


        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
