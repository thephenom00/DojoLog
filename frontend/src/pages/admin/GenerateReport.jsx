import React, { useState, useEffect } from "react";
import Sidebar from "../../components/Sidebar.jsx";
import Header from "../../components/Header.jsx";
import BackToDashboard from "../../components/BackToDashboard.jsx";
import {
  Box,
  Typography,
  Paper,
  Stack,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import { ApiService } from "../../api/api.js";
import ReportTable from "../../components/trainer/TrainingReportTable.jsx";
import Loading from "../../components/Loading.jsx";
import MoneySumCard from "../../components/trainer/MoneySumCard.jsx";

const GenerateReport = () => {
  const [trainers, setTrainers] = useState([]);
  const [selectedTrainer, setSelectedTrainer] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingDetails, setLoadingDetails] = useState(null);

  const [trainingReport, setTrainingReport] = useState([]);

  const moneySum = trainingReport
    ? trainingReport.reduce((sum, training) => sum + training.money, 0)
    : 0;

  const fetchReport = async (trainer) => {
    try {
      setLoadingDetails(trainer.email);
      const trainingReports = await ApiService.getTrainerCurrentMonthReport(
        trainer.email
      );

      setTrainingReport(trainingReports);
      setSelectedTrainer(trainer);
    } catch (err) {
      console.error("Failed to fetch report", err);
    } finally {
      setLoadingDetails(null);
    }
  };

  useEffect(() => {
    const fetchTrainers = async () => {
      try {
        setLoading(true);
        const trainerData = await ApiService.getAllTrainers();
        setTrainers(trainerData);
      } catch (err) {
        console.error("Failed to fetch all trainers", err);
      } finally {
        setLoading(false);
      }
    };
    fetchTrainers();
  }, []);

  const handleGenerateReport = async (trainer) => {
    await ApiService.downloadTrainerReport();
  };

  const handleOpenDetails = (trainer) => {
    fetchReport(trainer);
  };

  const handleCloseDialog = () => {
    setSelectedTrainer(null);
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard />
        <main className="flex-grow p-8 bg-gray-50 flex flex-col items-center">
          {loading ? (
            <Loading />
          ) : (
            <Box className="w-full max-w-4xl space-y-4 mt-10">
              {trainers.length === 0 ? (
                <Typography
                  className="flex justify-center"
                  color="text.secondary"
                >
                  Žádní trenéři nenalezeni.
                </Typography>
              ) : (
                trainers.map((trainer) => (
                  <Paper key={trainer.id} elevation={3} sx={{ p: 3 }}>
                    <Stack
                      direction="row"
                      justifyContent="space-between"
                      alignItems="center"
                    >
                      <div>
                        <Typography variant="h6">
                          {trainer.firstName} {trainer.lastName}
                        </Typography>
                        <Typography color="text.secondary" fontSize={14}>
                          {trainer.email}
                        </Typography>
                      </div>
                      <Stack direction="row" spacing={2}>
                        <Button
                          variant="contained"
                          size="small"
                          loading={loadingDetails === trainer.email}
                          onClick={() => handleOpenDetails(trainer)}
                        >
                          Zobrazit výkaz
                        </Button>
                      </Stack>
                    </Stack>
                  </Paper>
                ))
              )}
            </Box>
          )}

          {/* Dialog */}
          <Dialog
            open={!!selectedTrainer}
            onClose={handleCloseDialog}
            maxWidth="md"
            fullWidth
          >
            <DialogTitle>
              {selectedTrainer?.firstName} {selectedTrainer?.lastName} - Výkaz
              práce
            </DialogTitle>

            {trainingReport.length !== 0 ? (
              <DialogContent dividers sx={{ p: 0 }}>
                {trainingReport.length !== 0 && (
                  <>
                    <div className="flex justify-center">
                      <MoneySumCard moneySum={moneySum} />
                    </div>

                    <div className="flex justify-center mb-10">
                      <ReportTable trainerReport={trainingReport} />
                    </div>
                  </>
                )}
              </DialogContent>
            ) : (
              <DialogContent>
                <p className="text-center text-slate-600 mt-[10px] text-[16px]">
                  Tento trenér zatím neodtrénoval žádné lekce ani nevedl žádné
                  akce za tento měsíc.
                </p>
              </DialogContent>
            )}

            <DialogActions>
              <Button onClick={handleCloseDialog}>Zavřít</Button>
            </DialogActions>
          </Dialog>
        </main>
      </div>
    </div>
  );
};

export default GenerateReport;
