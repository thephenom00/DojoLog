import React, { useEffect, useState } from "react";
import Sidebar from "../../components/Sidebar.jsx";
import Header from "../../components/Header.jsx";
import { useNavigate } from "react-router-dom";
import { ApiService } from "../../api/api.js";
import { getDayName, formatDate } from "../../utils/trainingUtils.js";
import {
  Box,
  Typography,
  Paper,
  Stack,
  TextField,
  MenuItem,
  Button,
} from "@mui/material";
import BackToDashboard from "../../components/BackToDashboard.jsx";


const AddTrainerSubstitution = () => {
  const navigate = useNavigate();

  const [selectedSchool, setSelectedSchool] = useState("");
  const [selectedTraining, setSelectedTraining] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [originalTrainer, setOriginalTrainer] = useState("");
  const [substituteTrainer, setSubstituteTrainer] = useState("");

  const [schools, setSchools] = useState([]);
  const [trainings, setTrainings] = useState([]);
  const [upcomingDates, setUpcomingDates] = useState([]);
  const [trainers, setTrainers] = useState([]);
  const [allTrainers, setAllTrainers] = useState([]);

  useEffect(() => {
    const fetchSchools = async () => {
      try {
        const data = await ApiService.getSchools();
        setSchools(data);
      } catch (err) {
        console.error("Failed to fetch schools", err);
      }
    };
    fetchSchools();
  }, []);

  const handleSchoolSelect = async (schoolId) => {
    setSelectedSchool(schoolId);
    setSelectedTraining("");
    setSelectedDate("");
    setOriginalTrainer("");
    setSubstituteTrainer("");
    setTrainings([]);
    setUpcomingDates([]);
    setTrainers([]);

    try {
      const trainingData = await ApiService.getTrainingsBySchoolId(schoolId);
      setTrainings(trainingData);
    } catch (err) {
      console.error("Failed to fetch trainings", err);
    }
  };

  const handleTrainingSelect = async (trainingId) => {
    setSelectedTraining(trainingId);
    setSelectedDate("");
    setOriginalTrainer("");
    setSubstituteTrainer("");
    setUpcomingDates([]);
    setTrainers([]);

    try {
      const [upcomingDates, trainers] = await Promise.all([
        ApiService.getTraningUpcomingDates(trainingId),
        ApiService.getTrainersByTrainingId(trainingId),
      ]);

      setUpcomingDates(upcomingDates);
      setTrainers(trainers);

      if (allTrainers.length === 0) {
        const trainers = await ApiService.getAllTrainers();
        setAllTrainers(trainers);
      }
    } catch (err) {
      console.error("Failed to fetch trainers", err);
    }
  };

  const handleDateSelect = (date) => {
    setSelectedDate(date);
    setOriginalTrainer("");
    setSubstituteTrainer("");
  };

  const handleOriginalTrainerSelect = (trainerId) => {
    setOriginalTrainer(trainerId);
    setSubstituteTrainer("");
  };

  const handleSubmitSubstitution = async () => {
    const original = allTrainers.find((t) => t.id === Number(originalTrainer));
    const substitute = allTrainers.find((t) => t.id === Number(substituteTrainer));

    const substitutionDto = {
      originalTrainerId: original ? `${original.id}` : "",
      substituteTrainerId: substitute ? `${substitute.id}` : "",
      date: selectedDate,
    };

    await ApiService.createTrainingSubstitution(
      selectedTraining,
      substitutionDto
    );
    navigate("/admin/dashboard");
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard/>
        <main className="flex-grow p-8 bg-gray-50 flex justify-center items-start">
          <Box className="w-[320px] sm:w-[700px]">
            <Paper elevation={3} sx={{ p: 4 }}>
              <Typography variant="h5" fontWeight={600} mb={2} color="#318CE7">
                Vytvořit záskok za trenéra
              </Typography>

              <Stack spacing={2}>
                <TextField
                  select
                  label="Škola"
                  value={selectedSchool}
                  onChange={(e) => handleSchoolSelect(e.target.value)}
                  fullWidth
                >
                  {schools.map((school) => (
                    <MenuItem key={school.id} value={school.id}>
                      {school.name}
                    </MenuItem>
                  ))}
                </TextField>

                <TextField
                  select
                  label="Trénink"
                  value={selectedTraining}
                  onChange={(e) => handleTrainingSelect(e.target.value)}
                  fullWidth
                  disabled={!selectedSchool}
                >
                  {trainings.length === 0 ? (
                    <MenuItem disabled value="">
                      Žádné tréninky nenalezeny
                    </MenuItem>
                  ) : (
                    trainings.map((training) => (
                      <MenuItem key={training.id} value={training.id}>
                        {training.name} – {getDayName(training.dayOfWeek)} ({training.startTime[0]}:{training.startTime[1].toString().padStart(2, "0")} - {training.endTime[0]}:{training.endTime[1].toString().padStart(2, "0")})
                      </MenuItem>
                    ))
                  )}
                </TextField>

                <TextField
                  select
                  label="Datum záskoku"
                  value={selectedDate}
                  onChange={(e) => handleDateSelect(e.target.value)}
                  fullWidth
                  disabled={!selectedTraining}
                >
                  {upcomingDates.length === 0 ? (
                    <MenuItem disabled value="">
                      Žádné budoucí tréninky nenalezeny
                    </MenuItem>
                  ) : (
                    upcomingDates.map((date, idx) => (
                      <MenuItem key={idx} value={date}>
                        {formatDate(date)}
                      </MenuItem>
                    ))
                  )}
                </TextField>

                <TextField
                  select
                  label="Původní trenér"
                  value={originalTrainer}
                  onChange={(e) => handleOriginalTrainerSelect(e.target.value)}
                  fullWidth
                  disabled={!selectedDate}
                >
                  {trainers.length === 0 ? (
                    <MenuItem disabled value="">
                      Trenéři nenalezeni
                    </MenuItem>
                  ) : (
                    [...trainers]
                      .sort((a, b) => a.lastName.localeCompare(b.lastName))
                      .map((trainer) => (
                        <MenuItem key={trainer.id} value={trainer.id}>
                          {trainer.firstName} {trainer.lastName}
                        </MenuItem>
                      ))
                  )}
                </TextField>

                <TextField
                  select
                  label="Záskok"
                  value={substituteTrainer}
                  onChange={(e) => setSubstituteTrainer(e.target.value)}
                  fullWidth
                  disabled={!originalTrainer}
                >
                  {allTrainers
                    .filter(
                      (t) =>
                        t.id !== Number(originalTrainer) &&
                        !trainers.some((assigned) => assigned.id === t.id)
                    )
                    .map((trainer) => (
                      <MenuItem key={trainer.id} value={trainer.id}>
                        {trainer.firstName} {trainer.lastName}
                      </MenuItem>
                    ))}
                </TextField>

                <Button
                  variant="contained"
                  className="bg-judo-blue hover:bg-blue-700 text-white"
                  onClick={handleSubmitSubstitution}
                  disabled={
                    !selectedSchool ||
                    !selectedTraining ||
                    !selectedDate ||
                    !originalTrainer ||
                    !substituteTrainer
                  }
                >
                  Potvrdit záskok
                </Button>
              </Stack>
            </Paper>
          </Box>
        </main>
      </div>
    </div>
  );
};

export default AddTrainerSubstitution;
