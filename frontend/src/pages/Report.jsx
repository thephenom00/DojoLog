import React, { useState, useEffect } from "react";
import Sidebar from "../components/Sidebar.jsx";
import Header from "../components/Header.jsx";
import { ApiService } from "../api/api.js";
import ReportTable from "../components/trainer/TrainingReportTable.jsx";
import MoneySumCard from "../components/trainer/MoneySumCard.jsx";
import { useAuth } from "../context/AuthContext.jsx";
import Loading from "../components/Loading.jsx";
import BackToDashboard from "../components/BackToDashboard.jsx";
import { mockData } from "../utils/mockData.js";

const Report = () => {
  const [report, setReport] = useState([]);
  const { user } = useAuth();
  const email = user.email;
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user || !email) return;

    if (!user?.demo) {
      const fetchReport = async () => {
      try {
        setLoading(true);
        const reports = await ApiService.getTrainerCurrentMonthReport(
          email
        );
        setReport(reports);
      } catch (err) {
        console.error("Failed to fetch report", err);
      } finally {
        setLoading(false);
      }
    };
    fetchReport();
    } else {
      setLoading(true)
      setTimeout(() => {
        setLoading(false)
      }, "500")
      setReport(mockData.reports)
    }

  }, [user]);

  const moneySum = report
    ? report.reduce((sum, training) => sum + training.money, 0)
    : 0;

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard/>
        <main className="flex-grow p-8 bg-gray-50 flex flex-col gap-6 items-center">
          {loading ? (
            <Loading />
          ) : (
            <>
              <MoneySumCard moneySum={moneySum} />
              <ReportTable trainerReport={report} />
            </>
          )}
        </main>
      </div>
    </div>
  );
};

export default Report;
