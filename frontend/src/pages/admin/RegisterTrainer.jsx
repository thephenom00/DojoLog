import React, { useEffect, useState } from "react";
import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";
import BackToDashboard from "../../components/BackToDashboard";
import {
  Box,
  Typography,
  TextField,
  MenuItem,
  Paper,
  Button,
  Snackbar,
  Alert,
  Stack,
} from "@mui/material";
import { ApiService } from "../../api/api";

const RegisterTrainer = () => {
  const [schools, setSchools] = useState([]);
  const [trainings, setTrainings] = useState([]);
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    phoneNumber: "",
    email: "",
    hourlyRate: "",
    trainingId: "",
  });
  const [schoolId, setSchoolId] = useState("");

  const [notification, setNotification] = useState({
    open: false,
    message: "",
    severity: "success",
  });

  useEffect(() => {
    const fetchSchools = async () => {
      try {
        const data = await ApiService.getSchools();
        setSchools(data);
      } catch (error) {
        console.error("Failed to fetch schools", error);
      }
    };
    fetchSchools();
  }, []);

  useEffect(() => {
    const fetchTrainings = async () => {
      if (schoolId) {
        try {
          const data = await ApiService.getTrainingsBySchoolId(schoolId);
          setTrainings(data);
        } catch (error) {
          console.error("Failed to fetch trainings", error);
        }
      } else {
        setTrainings([]);
      }
    };
    fetchTrainings();
  }, [schoolId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
  
    if (name === "schoolId") {
      setSchoolId(value);
      setForm((prev) => ({ ...prev, trainingId: "" }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      console.log(form)
      await ApiService.registerTrainer(form);
      setNotification({ open: true, message: "Trenér byl úspěšně vytvořen.", severity: "success" });
      setForm({
        firstName: "",
        lastName: "",
        email: "",
        phoneNumber: "",
        hourlyRate: "",
        trainingId: "",
      });
      setSchoolId("")
    } catch (error) {
      console.error("Failed to create trainer", error);
      setNotification({ open: true, message: "Nepodařilo se vytvořit trenéra.", severity: "error" });
    }
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard />
        <main className="flex-grow p-8 bg-gray-50 flex justify-center items-start">
          <Snackbar
            open={notification.open}
            autoHideDuration={6000}
            onClose={() => setNotification({ ...notification, open: false })}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
          >
            <Alert
              onClose={() => setNotification({ ...notification, open: false })}
              severity={notification.severity}
              sx={{ width: "100%" }}
            >
              {notification.message}
            </Alert>
          </Snackbar>

          <Box className="w-[320px] sm:w-[700px]">
            <Paper elevation={3} sx={{ p: 4 }}>
              <Typography variant="h5" fontWeight={600} mb={2} color="#318CE7">
                Vytvoření nového trenéra
              </Typography>

              <form onSubmit={handleSubmit}>
                <Stack spacing={2}>
                  <TextField label="Jméno" name="firstName" value={form.firstName} onChange={handleChange} required fullWidth />
                  <TextField label="Příjmení" name="lastName" value={form.lastName} onChange={handleChange} required fullWidth />
                  <TextField label="Telefonní číslo" name="phoneNumber" value={form.phoneNumber} onChange={handleChange} required fullWidth />
                  <TextField label="E-mail" name="email" value={form.email} onChange={handleChange} type="email" required fullWidth />
                  <TextField label="Hodinová sazba (Kč)" name="hourlyRate" type="number" value={form.hourlyRate} onChange={handleChange} required fullWidth />

                  <TextField
                    select
                    label="Škola"
                    name="schoolId"
                    value={schoolId}
                    onChange={handleChange}
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
                    name="trainingId"
                    value={form.trainingId}
                    onChange={handleChange}
                    fullWidth
                  >
                    {trainings.map((training) => (
                      <MenuItem key={training.id} value={training.id}>
                        {training.name}
                      </MenuItem>
                    ))}
                  </TextField>

                  <Button type="submit" variant="contained" className="bg-judo-blue hover:bg-blue-700 text-white">
                    Vytvořit trenéra
                  </Button>
                </Stack>
              </form>
            </Paper>
          </Box>
        </main>
      </div>
    </div>
  );
};

export default RegisterTrainer;
