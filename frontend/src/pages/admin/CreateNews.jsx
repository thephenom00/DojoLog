import React, { useState } from "react";
import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";
import BackToDashboard from "../../components/BackToDashboard";
import { useNavigate } from "react-router-dom";
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

const CreateNews = () => {
  const [formData, setFormData] = useState({
    name: "",
    description: "",
  });
  const [loading, setLoading] = useState(false);
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setShowError(false);
    setErrorMessage(null);

    try {
      await ApiService.createNews(formData);
      setFormData({ name: "", description: ""});
      navigate("/dashboard")
    } catch (err) {
      setErrorMessage("Nepodařilo se vytvořit aktualitu.");
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
        <BackToDashboard />
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

          <Box className="w-[320px] sm:w-[700px]">
            <Paper elevation={3} sx={{ p: 4 }}>
              <Typography variant="h5" fontWeight={600} mb={2} color="#318CE7">
                Vytvoření nové aktuality
              </Typography>

              <form onSubmit={handleSubmit}>
                <Stack spacing={2}>
                  <TextField
                    label="Název"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Popis"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    fullWidth
                    multiline
                    rows={5}
                    placeholder="Co je nového?"
                    required
                  />
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={loading}
                    className="bg-judo-blue hover:bg-blue-700 text-white"
                  >
                    Vytvořit aktualitu
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

export default CreateNews;