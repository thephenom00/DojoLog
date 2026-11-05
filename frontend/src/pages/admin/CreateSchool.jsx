import React, { useState } from "react";
import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";
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
} from "@mui/material";
import BackToDashboard from "../../components/BackToDashboard";

const CreateSchool = () => {
  const [formData, setFormData] = useState({
    name: "",
    address: "",
    city: "",
    contactPerson: "",
    contactNumber: "",
    instructions:"",
  });
  const [loading, setLoading] = useState(false);
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const [showSuccess, setShowSuccess] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setShowError(false);
    setErrorMessage(null);

    try {
      await ApiService.createSchool(formData);
      setShowSuccess(true);
      setSuccessMessage("Škola byla úspěšně vytvořena.");
      setFormData({
        name: "",
        address: "",
        city: "",
        contactPerson: "",
        contactNumber: "",
        instructions: "",
      });
    } catch (err) {
      setErrorMessage("Nepodařilo se vytvořit školu.");
      setShowError(true);
    } finally {
      setLoading(false);
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
              <Typography variant="h5" fontWeight={600} mb={2} color="#318CE7">
                Vytvoření nové školy
              </Typography>

              <form onSubmit={handleSubmit}>
                <Stack spacing={2}>
                  <TextField
                    label="Název školy"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Adresa"
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Město"
                    name="city"
                    value={formData.city}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Kontaktní osoba"
                    name="contactPerson"
                    value={formData.contactPerson}
                    onChange={handleChange}
                    fullWidth
                  />
                  <TextField
                    label="Telefonní číslo"
                    name="contactNumber"
                    value={formData.contactNumber}
                    onChange={handleChange}
                    fullWidth
                  />
                  <TextField
                    label="Instrukce"
                    name="instructions"
                    value={formData.instructions}
                    onChange={handleChange}
                    fullWidth
                    multiline
                    rows={4}
                    placeholder="Zde můžete uvést např. na koho se obrátit, kde jsou žíněnky atd."
                  />
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={loading}
                    className="bg-judo-blue hover:bg-blue-700 text-white"
                  >
                    Vytvořit školu
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

export default CreateSchool;
