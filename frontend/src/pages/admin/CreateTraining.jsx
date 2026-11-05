import React, { useEffect, useState } from "react";
import Sidebar from "../../components/Sidebar.jsx";
import Header from "../../components/Header.jsx";
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
import { ApiService } from "../../api/api.js";
import { useNavigate } from "react-router-dom";
import { getDayName } from "../../utils/trainingUtils.js";
import BackToDashboard from "../../components/BackToDashboard.jsx";

const CreateTraining = () => {
  const [schools, setSchools] = useState([]);
  const [selectedSchoolId, setSelectedSchoolId] = useState("");
  const [form, setForm] = useState({
    name: "",
    capacity: "",
    price: "",
    dayOfWeek: "",
    startTime: "",
    endTime: "",
  });

  const [createLoading, setCreateLoading] = useState(false);
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const [showSuccess, setShowSuccess] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    const fetchSchools = async () => {
      try {
        const response = await ApiService.getSchools();
        setSchools(response);
      } catch (err) {
        console.error("Failed to fetch schools", err);
      }
    };

    fetchSchools();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!selectedSchoolId) {
      setErrorMessage("Musíte vybrat školu.");
      setShowError(true);
      return;
    }

    try {
      setCreateLoading(true);
      const updatedForm = {
        ...form,
        dayOfWeek: getDayName(form.dayOfWeek, false).toUpperCase(),
      };

      await ApiService.createTraining(selectedSchoolId, updatedForm);

      setShowSuccess(true);
      setSuccessMessage("Trénink byl úspěšně vytvořen.");

      setForm({
        name: "",
        capacity: "",
        price: "",
        dayOfWeek: "",
        startTime: "",
        endTime: "",
      });
      setSelectedSchoolId("");
    } catch (err) {
      console.error("Failed to create training", err);
      setErrorMessage("Nepodařilo se vytvořit trénink.");
      setShowError(true);
    } finally {
      setCreateLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard/>
        <main className="flex-grow p-8 bg-gray-50 flex justify-center items-start">
          <Snackbar
            open={showError || showSuccess}
            autoHideDuration={6000}
            onClose={() => {
              setShowError(false);
              setShowSuccess(false);
            }}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
          >
            <Alert
              onClose={() => {
                setShowError(false);
                setShowSuccess(false);
              }}
              severity={showSuccess ? "success" : "error"}
              sx={{ width: "100%" }}
            >
              {showSuccess ? successMessage : errorMessage}
            </Alert>
          </Snackbar>
          <Box className="w-[320px] sm:w-[700px]">
            <Paper elevation={3} sx={{ p: 4 }}>
              <Typography
                variant="h5"
                fontWeight={600}
                mb={2}
                color="#318CE7"
              >
                Vytvoření nového tréninku
              </Typography>

              <form onSubmit={handleSubmit}>
                <Stack spacing={2}>
                  <TextField
                    select
                    label="Škola"
                    value={selectedSchoolId}
                    onChange={(e) => setSelectedSchoolId(e.target.value)}
                    fullWidth
                    required
                  >
                    {schools.map((school) => (
                      <MenuItem key={school.id} value={school.id}>
                        {school.name}
                      </MenuItem>
                    ))}
                  </TextField>

                  <TextField
                    label="Název tréninku"
                    name="name"
                    value={form.name}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Kapacita"
                    name="capacity"
                    type="number"
                    value={form.capacity}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Cena (Kč/rok)"
                    name="price"
                    type="number"
                    value={form.price}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    select
                    label="Den v týdnu"
                    name="dayOfWeek"
                    value={form.dayOfWeek}
                    onChange={handleChange}
                    fullWidth
                    required
                  >
                    {[
                      "Pondělí",
                      "Úterý",
                      "Středa",
                      "Čtvrtek",
                      "Pátek",
                    ].map((day, index) => (
                      <MenuItem key={index} value={day}>
                        {day}
                      </MenuItem>
                    ))}
                  </TextField>
                  <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                    <TextField
                      label="Začátek"
                      name="startTime"
                      type="time"
                      value={form.startTime}
                      onChange={handleChange}
                      fullWidth
                      required
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 300 }}
                    />
                    <TextField
                      label="Konec"
                      name="endTime"
                      type="time"
                      value={form.endTime}
                      onChange={handleChange}
                      fullWidth
                      required
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 300 }}
                    />
                  </Stack>

                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={createLoading}
                    className="bg-judo-blue hover:bg-blue-700 text-white"
                  >
                    Vytvořit trénink
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

export default CreateTraining;
