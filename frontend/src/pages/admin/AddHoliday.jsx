import React, { useState, useEffect } from "react";
import {
  Box,
  Paper,
  Typography,
  TextField,
  MenuItem,
  Button,
  Checkbox,
  FormControlLabel,
  Stack,
  Snackbar,
  Alert,
  List,
  ListItem,
  ListItemText,
  Divider
} from "@mui/material";
import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";
import {ApiService} from "../../api/api"
import { formatDate } from "../../utils/trainingUtils";

const AddHoliday = () => {
  const [schools, setSchools] = useState([]);
  const [selectedSchoolId, setSelectedSchoolId] = useState("");
  const [applyToAll, setApplyToAll] = useState(false);

  const [name, setName] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [existingHolidays, setExistingHolidays] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState({ open: false, message: "", severity: "success" });

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

  useEffect(() => {
    if (!applyToAll && selectedSchoolId) {
      const fetchHolidays = async () => {
        try {
          const data = await ApiService.getHolidaysBySchoolId(selectedSchoolId);
          setExistingHolidays(data);
        } catch (err) {
          console.error("Failed to fetch holidays", err);
          setExistingHolidays([]);
        }
      };
  
      fetchHolidays();
    } else {
      setExistingHolidays([]);
    }
  }, [selectedSchoolId, applyToAll]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    try {
      const holidayDto = {
        name: name,
        startDate: startDate,
        endDate: endDate || null,
      }

      var response = "";
      if (applyToAll) {
        response = await ApiService.addHolidayToAllSchools(holidayDto);
      } else {
        response = await ApiService.addHolidayToSchool(selectedSchoolId, holidayDto);
      }

      if (response.status === "409") {
        setNotification({ open: true, message: "Tyto prázdniny již byly přidány.", severity: "error" });
      } else {
        setNotification({ open: true, message: "Volno bylo úspěšně přidáno.", severity: "success" });
      }
      setStartDate("");
      setEndDate("");
      setSelectedSchoolId("");
      setApplyToAll(false);
      setName("");
    } catch (err) {
      console.error("Failed to add holiday", err);
      setNotification({ open: true, message: "Chyba při přidávání volna.", severity: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <main className="flex-grow p-8 bg-gray-50 flex flex-col items-center">
          <Snackbar
            open={notification.open}
            autoHideDuration={6000}
            onClose={() => setNotification({ ...notification, open: false })}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
          >
            <Alert severity={notification.severity}>{notification.message}</Alert>
          </Snackbar>

          <Paper sx={{ p: 4, width: "100%", maxWidth: 600 }} elevation={3}>
            <Typography variant="h5" fontWeight={600} mb={3}>
              Přidat prázdniny
            </Typography>
            <form onSubmit={handleSubmit}>
              <Stack spacing={3}>
                <FormControlLabel
                  control={<Checkbox checked={applyToAll} onChange={(e) => setApplyToAll(e.target.checked)} />}
                  label="Přidat všem školám"
                />
                {!applyToAll && (
                  <TextField
                    select
                    label="Vyberte školu"
                    value={selectedSchoolId}
                    onChange={(e) => setSelectedSchoolId(e.target.value)}
                    required
                    fullWidth
                  >
                    {schools.map((school) => (
                      <MenuItem key={school.id} value={school.id}>
                        {school.name}
                      </MenuItem>
                    ))}
                  </TextField>
                )}

                {existingHolidays.length > 0 && (
                  <Box>
                    <Typography variant="subtitle1" fontWeight={500}>Nadcházející prázdniny:</Typography>
                    <List dense>
                      {existingHolidays.map((h, idx) => (
                        <React.Fragment key={idx}>
                          <ListItem>
                            <ListItemText
                              primary={h.name}
                              secondary={`${formatDate(h.startDate)} ${h.endDate ? `- ${formatDate(h.endDate)}` : ""}`}
                            />
                          </ListItem>
                          <Divider />
                        </React.Fragment>
                      ))}
                    </List>
                  </Box>
                )}

                <TextField
                  label="Název volna"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                  fullWidth
                />

                <TextField
                  label="Datum začátku"
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  InputLabelProps={{ shrink: true }}
                  required
                  fullWidth
                />

                <TextField
                  label="Datum konce (nepovinné)"
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  InputLabelProps={{ shrink: true }}
                  fullWidth
                />

                <Button type="submit" variant="contained" disabled={loading}>
                  Přidat prázdniny
                </Button>
              </Stack>
            </form>
          </Paper>
        </main>
      </div>
    </div>
  );
};

export default AddHoliday;