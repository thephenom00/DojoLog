import React, { useState, useEffect, use } from "react";
import { useParams } from "react-router-dom";
import Sidebar from "../components/Sidebar.jsx";
import Header from "../components/Header.jsx";
import AttendanceSchoolCard from "../components/trainer/AttendanceSchoolCard.jsx";
import { ApiService } from "../api/api.js";
import { mapTrainingData } from "../utils/trainingUtils.js";
import AttendanceTable from "../components/trainer/AttendanceTable.jsx";
import Loading from "../components/Loading.jsx";
import BackToDashboard from "../components/BackToDashboard.jsx";
import { useAuth } from "../context/AuthContext.jsx";
import { mockData } from "../utils/mockData.js";

const Attendance = () => {
  const { id } = useParams();
  const [training, setTraining] = useState();
  const { user } = useAuth();

  const [trainerAttendances, setTrainerAttendances] = useState();
  const [childAttendances, setChildAttendances] = useState();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user?.demo) {
      const fetchData = async () => {
        try {
          setLoading(true);
          const training = await ApiService.getTrainingUnitById(id);
          setTraining(mapTrainingData(training));

          const trainerAtt =
          await ApiService.getTrainerAttendancesByTrainingUnitId(id);
          setTrainerAttendances(trainerAtt);

          const childAtt = await ApiService.getChildAttendancesByTrainingUnitId(
            id
          );
          setChildAttendances(childAtt);
        } catch (err) {
          console.error("Failed to fetch trainings", err);
        } finally {
          setLoading(false);
        }
      };
      fetchData();
    } else {
      {/*** DEMO ***/}
      setLoading(true)
      setInterval(() => {
        setLoading(false)
      }, "500")
      setTraining(mockData.trainerUpcomingTrainings.find((t) => t.id === Number(id)));
      setTrainerAttendances(mockData.trainerAttendances[id]);
      setChildAttendances(mockData.childAttendances[id]);
    }
  }, [id]);

  return (
    <div className="flex min-h-screen">
      <Sidebar />

      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard />
        <main className="flex-grow p-8 bg-gray-50 flex flex-col gap-6 items-center">
          {loading ? (
            <Loading />
          ) : (
            <>
              {training ? (
                <AttendanceSchoolCard
                  training={training}
                  setTraining={setTraining}
                />
              ) : (
                <p>No training data found.</p>
              )}
              {trainerAttendances && childAttendances && (
                <AttendanceTable
                  trainerAttendances={trainerAttendances}
                  childAttendances={childAttendances}
                  id={id}
                />
              )}
            </>
          )}
        </main>
      </div>
    </div>
  );
};

export default Attendance;
