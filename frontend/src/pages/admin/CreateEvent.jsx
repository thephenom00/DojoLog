import React, { useState } from "react";
import { ApiService } from "../../api/api";
import {
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  Snackbar,
  Alert,
  Stack,
  MenuItem,
} from "@mui/material";
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';

const CreateEvent = ({ onEventCreated }) => {
  const [formData, setFormData] = useState({
    name: "",
    startDate: "",
    endDate: "",
    location: "",
    startTime: "",
    endTime: "",
    places: "",
    price: "",
    description: "",
    trainerSalary: "",
    eventType: "",
  });

  const [loading, setLoading] = useState(false);
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setShowError(false);
    setErrorMessage(null);

    try {
      const eventDto = {
        name: formData.name,
        startDate: formData.startDate,
        endDate: formData.endDate || null,
        location: formData.location,
        startTime: formData.startTime || null,
        endTime: formData.endTime || null,
        places: parseInt(formData.places),
        price: parseInt(formData.price),
        description: formData.description,
        trainerSalary: parseInt(formData.trainerSalary),
        eventType: formData.eventType,
      };

      await ApiService.createEvent(eventDto);
      onEventCreated();
      setFormData({
        name: "",
        startDate: "",
        endDate: "",
        location: "",
        startTime: "",
        endTime: "",
        places: "",
        price: "",
        description: "",
        trainerSalary: "",
        eventType: "",
      });
    } catch (err) {
      setErrorMessage("Nepodařilo se vytvořit akci.");
      setShowError(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen">
      <div className="flex-grow flex flex-col">
        <main className="flex-grow p-8 bg-gray-50 flex justify-center items-start">
          <Snackbar
            open={showError}
            autoHideDuration={6000}
            onClose={() => {
              setShowError(false);
            }}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
          >
            <Alert
              onClose={() => {
                setShowError(false);
              }}
              severity={"error"}
              sx={{ width: "100%" }}
            >
              {errorMessage}
            </Alert>
          </Snackbar>

          <Box className="w-[260px] sm:w-[700px]">
            <Paper elevation={3} sx={{ p: 4 }}>
              <Typography variant="h5" fontWeight={600} mb={2} color="#318CE7">
                Vytvoření nové akce
              </Typography>

              <form onSubmit={handleSubmit}>
                <Stack spacing={2}>
                  <TextField
                    label="Název akce"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Datum začátku"
                    type="date"
                    name="startDate"
                    value={formData.startDate}
                    onChange={handleChange}
                    fullWidth
                    required
                    InputLabelProps={{ shrink: true }}
                  />

                  <TextField
                    label="Datum konce (nepovinné)"
                    type="date"
                    name="endDate"
                    value={formData.endDate}
                    onChange={handleChange}
                    fullWidth
                    InputLabelProps={{ shrink: true }}
                  />
                  <TextField
                    label="Místo konání"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Čas začátku (nepovinné)"
                    type="time"
                    name="startTime"
                    value={formData.startTime}
                    onChange={handleChange}
                    fullWidth
                    InputLabelProps={{ shrink: true }}
                  />
                  <TextField
                    label="Čas konce (nepovinné)"
                    type="time"
                    name="endTime"
                    value={formData.endTime}
                    onChange={handleChange}
                    fullWidth
                    InputLabelProps={{ shrink: true }}
                  />
                  <TextField
                    label="Počet míst"
                    name="places"
                    type="number"
                    value={formData.places}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Cena (Kč)"
                    name="price"
                    type="number"
                    value={formData.price}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Plat pro trenéra (Kč)"
                    name="trainerSalary"
                    type="number"
                    value={formData.trainerSalary}
                    onChange={handleChange}
                    fullWidth
                    required
                  />

                  <TextField
                    select
                    label="Typ akce"
                    name="eventType"
                    value={formData.eventType}
                    onChange={handleChange}
                    fullWidth
                    required
                  >
                    <MenuItem value="">Vyber typ akce</MenuItem>
                    <MenuItem value="Závody">Závody</MenuItem>
                    <MenuItem value="Soustředění">Soustředění</MenuItem>
                    <MenuItem value="Celodenní akce">Celodenní akce</MenuItem>
                  </TextField>

                  <TextField
                    label="Popis"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    fullWidth
                    multiline
                    rows={5}
                    placeholder="Popište událost..."
                    required
                  />
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={loading}
                    className="bg-judo-blue hover:bg-blue-700 text-white"
                  >
                    Vytvořit akci
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

export default CreateEvent;
