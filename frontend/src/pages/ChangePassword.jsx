import React, { useState } from "react";
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
import { Lock } from "lucide-react";
import { ApiService } from "../api/api";
import BackToDashboard from "../components/BackToDashboard.jsx";
import Header from "../components/Header.jsx";
import Sidebar from "../components/Sidebar.jsx";
import { useNavigate } from "react-router-dom";

const ChangePassword = () => {
  const navigate = useNavigate();

  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState({
    open: false,
    message: "",
    severity: "error",
  });

  const handleCloseNotification = () => {
    setNotification({ ...notification, open: false });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (newPassword !== confirmPassword) {
      setNotification({
        open: true,
        message: "Nová hesla se neshodují.",
        severity: "error",
      });
      return;
    }

    if (newPassword.length < 6) {
      setNotification({
        open: true,
        message: "Heslo musí mít alespoň 6 znaků.",
        severity: "error",
      });
      return;
    }

    if (currentPassword === newPassword) {
      setNotification({
        open: true,
        message: "Nové heslo nesmí být stejné jako původní.",
        severity: "error",
      });
      return;
    }

    try {
      setLoading(true);
      const response = await ApiService.changePassword(currentPassword, newPassword);
      if (response === "OK") {
        setNotification({
          open: true,
          message: "Heslo bylo úspěšně změněno.",
          severity: "success",
        });
        navigate("/dashboard")
        setCurrentPassword("");
        setNewPassword("");
        setConfirmPassword("");
      } else if (response === "UNAUTHORIZED") {
        setNotification({
          open: true,
          message: "Aktuální heslo není správné.",
          severity: "error",
        });
      }
    } catch (err) {
      setNotification({
        open: true,
        message: "Chyba při komunikaci se serverem.",
        severity: "error",
      });
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
        <main className="flex-grow p-8 bg-gray-50 flex flex-col gap-6 items-center">
          <Snackbar
            open={notification.open}
            autoHideDuration={6000}
            onClose={handleCloseNotification}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
          >
            <Alert
              onClose={handleCloseNotification}
              severity={notification.severity}
              sx={{ width: "100%" }}
            >
              {notification.message}
            </Alert>
          </Snackbar>

          <Box className="flex min-h-screen bg-gray-50 justify-center items-start py-16">
            <Paper elevation={3} sx={{ p: 5, width: "100%", maxWidth: 500 }}>
              <Stack spacing={2} alignItems="center">
                <Lock size={36} className="text-judo-blue" />
                <Typography variant="h5" fontWeight={600}>
                  Změna hesla
                </Typography>
                <Typography
                  variant="body2"
                  color="text.secondary"
                  align="center"
                >
                  Pro změnu hesla zadejte své aktuální a nové heslo.
                </Typography>
              </Stack>

              <form onSubmit={handleSubmit}>
                <Stack spacing={3} mt={4}>
                  <TextField
                    label="Aktuální heslo"
                    name="currentPassword"
                    type="password"
                    fullWidth
                    required
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                  />
                  <TextField
                    label="Nové heslo"
                    name="newPassword"
                    type="password"
                    minLength={6}
                    fullWidth
                    required
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                  />
                  <TextField
                    label="Potvrzení nového hesla"
                    name="confirmPassword"
                    type="password"
                    fullWidth
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                  />
                  <Button
                    type="submit"
                    variant="contained"
                    className="bg-judo-blue text-white hover:bg-blue-800"
                    disabled={loading}
                  >
                    Změnit heslo
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

export default ChangePassword;
