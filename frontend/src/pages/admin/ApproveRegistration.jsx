import React, { useEffect, useState } from "react";
import Sidebar from "../../components/Sidebar.jsx";
import Header from "../../components/Header.jsx";
import BackToDashBoard from "../../components/BackToDashboard.jsx";
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
  TextField,
} from "@mui/material";
import { Calendar, School, Users } from "lucide-react";
import { ApiService } from "../../api/api.js";
import Loading from "../../components/Loading.jsx";
import { getDayName, formatDate } from "../../utils/trainingUtils.js";

const ApproveRegistration = () => {
  const [requests, setRequests] = useState([]);
  const [selectedChild, setSelectedChild] = useState(null);
  const [editingChild, setEditingChild] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingAction, setLoadingAction] = useState(null);
  const [loadingEdit, setLoadingEdit] = useState(false);

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const response = await ApiService.getUnassignedChildren();
      setRequests(response);
    } catch (err) {
      console.error("Failed to fetch unassigned children", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const handleApprove = async (id) => {
    try {
      setLoadingAction(id);
      await ApiService.addChildToTraining(id);
      await fetchRequests();
    } catch (err) {
      console.error("Failed to assign child to training.", err);
    } finally {
      setLoadingAction(null);
    }
  };

  const handleDeny = async (id) => {
    try {
      setLoadingAction(id);
      await ApiService.deleteChild(id);
      await fetchRequests();
    } catch (err) {
      console.error("Failed to assign child to training.", err);
    } finally {
      setLoadingAction(null);
    }
  };

  const handleOpenDetails = (child) => {
    setSelectedChild(child);
    setEditingChild(null);
  };

  const handleCloseDialog = () => {
    setEditingChild(null);
    setSelectedChild(null);
  };

  const handleEdit = () => {
    setEditingChild({
      ...selectedChild,
      dateOfBirth: formatDateOfBirth(selectedChild.dateOfBirth),
    });
  };

  const formatDateOfBirth = (array) => {
    return `${array[0]}-${array[1].toString().padStart(2, "0")}-${array[2].toString().padStart(2, "0")}`;
  };

  const handleEditChange = (e) => {
    const { name, value } = e.target;
    setEditingChild((prev) => ({ ...prev, [name]: value }));
  };

  const handleSaveEdit = async () => {
    const childDto = {
      firstName: editingChild.firstName !== selectedChild.firstName ? editingChild.firstName : null,
      lastName: editingChild.lastName !== selectedChild.lastName ? editingChild.lastName : null,
      dateOfBirth: editingChild.dateOfBirth !== formatDateOfBirth(selectedChild.dateOfBirth) ? editingChild.dateOfBirth : null,
      street: editingChild.street !== selectedChild.street ? editingChild.street : null,
      city: editingChild.city !== selectedChild.city ? editingChild.city : null,
      zip: editingChild.zip !== selectedChild.zip ? editingChild.zip : null,
      birthNumber: editingChild.birthNumber !== selectedChild.birthNumber ? editingChild.birthNumber : null,
    };

    const parentDto = {
      firstName: editingChild.parentFirstName !== selectedChild.parentFirstName ? editingChild.parentFirstName : null,
      lastName: editingChild.parentLastName !== selectedChild.parentLastName ? editingChild.parentLastName : null,
      email: editingChild.parentEmail !== selectedChild.parentEmail ? editingChild.parentEmail : null,
      phoneNumber: editingChild.parentPhoneNumber !== selectedChild.parentPhoneNumber ? editingChild.parentPhoneNumber : null,
    };

    try {
      setLoadingEdit(true);
      await ApiService.updateChildInfo(editingChild.childId, childDto);

      if (parentDto.email !== null || parentDto.phoneNumber !== null || parentDto.parentFirstName !== null || parentDto.parentLastName !== null) {
        await ApiService.updateParentInfo(editingChild.childId, parentDto);
      }
    } catch (err) {
      console.error("Failed to edit children info.", err);
    } finally {
      setLoadingEdit(false);
    }

    setSelectedChild(null);
    setEditingChild(null);

    fetchRequests()
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashBoard />
        <main className="flex-grow p-8 bg-gray-50 flex justify-center items-start">
          {loading ? (
            <Loading />
          ) : (
            <Box className="mt-10 w-full max-w-4xl space-y-4">
              {requests.length === 0 ? (
                <Typography
                  className="flex justify-center"
                  color="text.secondary"
                  sx={{ fontSize: "17px" }}
                >
                  Žádné nové žádosti
                </Typography>
              ) : (
                // REQUEST CARD
                requests.map((request) => (
                  <Paper key={request.childId} elevation={2} sx={{ p: 3 }}>
                    <Stack spacing={1}>
                      <Typography fontSize={19}>
                        <strong>
                          {request.firstName} {request.lastName}
                        </strong>
                      </Typography>
                      <Typography className="flex items-center gap-2">
                        <School size={18} className="text-judo-blue" />
                        {request.schoolName}
                      </Typography>
                      <Typography className="flex items-center gap-2">
                        <Calendar size={18} className="text-judo-blue" />
                        {getDayName(request.trainingDayOfWeek)},{" "}
                        {request.trainingTime} ({request.trainingName})
                      </Typography>
                      <Typography className="flex items-center gap-2">
                        <Users size={18} className="text-judo-blue" />
                        {request.parentFirstName} {request.parentLastName} ({request.parentEmail})
                      </Typography>
                      <Stack direction="row" justifyContent="space-between">
                        <Button
                          size="small"
                          variant="outlined"
                          onClick={() => handleOpenDetails(request)}
                        >
                          Zobrazit detaily
                        </Button>
                        <Stack direction="row" spacing={2}>
                          <Button
                            size="small"
                            variant="contained"
                            color="success"
                            onClick={() => handleApprove(request.childId)}
                            loading={loadingAction === request.childId}
                          >
                            Schválit
                          </Button>
                          <Button
                            size="small"
                            variant="contained"
                            color="error"
                            onClick={() => handleDeny(request.childId)}
                            loading={loadingAction === request.childId}
                          >
                            Zamítnout
                          </Button>
                        </Stack>
                      </Stack>
                    </Stack>
                  </Paper>
                ))
              )}
            </Box>
          )}

          {/* Dialog */}
          <Dialog
            open={!!selectedChild}
            onClose={handleCloseDialog}
            maxWidth="sm"
            fullWidth
            disableRestoreFocus
          >
            <DialogTitle>Detail žádosti</DialogTitle>
            <DialogContent dividers>
              {selectedChild && (
                <Stack spacing={2}>
                  {editingChild ? (
                    // EDIT
                    <>
                      <TextField
                        label="Jméno"
                        name="firstName"
                        value={editingChild.firstName}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="Příjmení"
                        name="lastName"
                        value={editingChild.lastName}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="Rodné číslo"
                        name="birthNumber"
                        value={editingChild.birthNumber}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        InputLabelProps={{ shrink: true }}
                        type="date"
                        label="Datum narození"
                        name="dateOfBirth"
                        value={editingChild.dateOfBirth}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="Ulice"
                        name="street"
                        value={editingChild.street}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="Město"
                        name="city"
                        value={editingChild.city}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="PSČ"
                        name="zip"
                        value={editingChild.zip}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="Jméno rodiče"
                        name="parentFirstName"
                        value={editingChild.parentFirstName}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="Příjmení"
                        name="parentLastName"
                        value={editingChild.parentLastName}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="Email rodiče"
                        name="parentEmail"
                        value={editingChild.parentEmail}
                        onChange={handleEditChange}
                        fullWidth
                      />
                      <TextField
                        label="Telefon rodiče"
                        name="parentPhoneNumber"
                        value={editingChild.parentPhoneNumber}
                        onChange={handleEditChange}
                        fullWidth
                      />
                    </>
                  ) : (
                    // DETAIL
                    <>
                      <Typography>
                        <strong>Jméno:</strong> {selectedChild.firstName}
                      </Typography>
                      <Typography>
                        <strong>Příjmení:</strong> {selectedChild.lastName}
                      </Typography>
                      <Typography>
                        <strong>Rodné číslo:</strong>{" "}
                        {selectedChild.birthNumber}
                      </Typography>
                      <Typography>
                        <strong>Datum narození:</strong>{" "}
                        {formatDate(selectedChild.dateOfBirth)}
                      </Typography>
                      <Typography>
                        <strong>Adresa:</strong> {selectedChild.street},{" "}
                        {selectedChild.city}, {selectedChild.zip}
                      </Typography>
                      <Typography>
                        <strong>Škola:</strong> {selectedChild.schoolName}
                      </Typography>
                      <Typography>
                        <strong>Trénink:</strong>{" "}
                        {getDayName(selectedChild.trainingDayOfWeek)},{" "}
                        {selectedChild.trainingTime} (
                        {selectedChild.trainingName})
                      </Typography>
                      <Typography>
                        <strong>Rodič:</strong> {selectedChild.parentFirstName} {selectedChild.parentLastName} 
                      </Typography>
                      <Typography>
                        <strong>Kontakt:</strong> {selectedChild.parentEmail},{" "}
                        {selectedChild.parentPhoneNumber}
                      </Typography>
                    </>
                  )}
                </Stack>
              )}
            </DialogContent>
            <DialogActions>
              <Button onClick={handleCloseDialog}>Zavřít</Button>
              {editingChild ? (
                <Button
                  loading={loadingEdit}
                  variant="contained"
                  onClick={() => {
                    handleSaveEdit();
                    handleCloseDialog();
                  }}
                >
                  Uložit
                </Button>
              ) : (
                <Button variant="contained" onClick={handleEdit}>
                  Upravit údaje
                </Button>
              )}
            </DialogActions>
          </Dialog>
        </main>
      </div>
    </div>
  );
};

export default ApproveRegistration;
