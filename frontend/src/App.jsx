import "./App.css";
import LandingPage from "./pages/LandingPage.jsx";
import AuthPages from "./pages/authentication/AuthPages.jsx";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "./pages/dashborad/Dashboard.jsx";
import Attendance from "./pages/Attendance.jsx";
import Report from "./pages/Report.jsx";
import Event from "./pages/Event.jsx";
import EventDetail from "./pages/EventDetail.jsx";
import News from "./pages/News.jsx";
import RegisterChild from "./pages/RegisterChild.jsx";
import MyChild from "./pages/MyChild.jsx";
import ScrollToTop from "./components/ScrollToTop.jsx"
import ProtectedRoute from "./utils/ProtectedRoute.jsx";
import CreateSchool from "./pages/admin/CreateSchool.jsx";
import CreateTraining from "./pages/admin/CreateTraining.jsx"
import AddTrainerSubstitution from "./pages/admin/AddTrainerSubstitution.jsx";
import ApproveRegistration from "./pages/admin/ApproveRegistration.jsx";
import CreateNews from "./pages/admin/CreateNews.jsx"
import GenerateReport from "./pages/admin/GenerateReport.jsx"
import ChangePassword from "./pages/ChangePassword.jsx"
import AddHoliday from "./pages/admin/AddHoliday.jsx"
import RegisterTrainer from "./pages/admin/RegisterTrainer.jsx";
import AddTrainer from "./pages/admin/AddTrainer.jsx";
import DemoSelection from "./pages/DemoSelection.jsx"

function App() {
  return (
    <Router>
      <ScrollToTop />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<AuthPages currentPage="login" />} />
        <Route path="/register" element={<AuthPages currentPage="register" />} />

        <Route path="/demo-selection" element={<DemoSelection />} />

        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/attendance/:id" element={<ProtectedRoute role={["ROLE_TRAINER"]}><Attendance /></ProtectedRoute>} />
        <Route path="/report" element={<ProtectedRoute role={["ROLE_TRAINER"]}><Report /></ProtectedRoute>} />
        <Route path="/events" element={<ProtectedRoute><Event /></ProtectedRoute>} />
        <Route path="/events/detail/:id" element={<ProtectedRoute ><EventDetail /></ProtectedRoute>} />
        <Route path="/news" element={<ProtectedRoute role={["ROLE_TRAINER"]}><News /></ProtectedRoute>} />
        <Route path="/register-child" element={<ProtectedRoute role={["ROLE_PARENT"]}><RegisterChild /></ProtectedRoute>} />
        <Route path="/my-child" element={<ProtectedRoute role={["ROLE_PARENT"]}><MyChild /></ProtectedRoute>} />

        <Route path="/admin/create-school" element={<ProtectedRoute role={["ROLE_ADMIN"]}><CreateSchool /></ProtectedRoute>} />
        <Route path="/admin/create-training" element={<ProtectedRoute role={["ROLE_ADMIN"]}><CreateTraining /></ProtectedRoute>} />
        <Route path="/admin/add-trainer-substitution" element={<ProtectedRoute role={["ROLE_ADMIN"]}><AddTrainerSubstitution /></ProtectedRoute>} />
        <Route path="/admin/approve-registration" element={<ProtectedRoute role={["ROLE_ADMIN"]}><ApproveRegistration /></ProtectedRoute>} />
        <Route path="/admin/create-news" element={<ProtectedRoute role={["ROLE_ADMIN"]}><CreateNews /></ProtectedRoute>} />
        <Route path="/admin/generate-report" element={<ProtectedRoute role={["ROLE_ADMIN"]}><GenerateReport /></ProtectedRoute>} />
        <Route path="/admin/add-holiday" element={<ProtectedRoute role={["ROLE_ADMIN"]}><AddHoliday /></ProtectedRoute>} />
        <Route path="/admin/register-trainer" element={<ProtectedRoute role={["ROLE_ADMIN"]}><RegisterTrainer /></ProtectedRoute>} />
        <Route path="/admin/add-trainer" element={<ProtectedRoute role={["ROLE_ADMIN"]}><AddTrainer /></ProtectedRoute>} />

        <Route path="/change-password" element={<ProtectedRoute><ChangePassword /></ProtectedRoute>} />

        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
