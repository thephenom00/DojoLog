import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { ApiService } from "../api/api.js";
import Loading from "../components/Loading.jsx";
import { getDayName, formatTime } from "../utils/trainingUtils.js";
import {
  Box,
  Typography,
  TextField,
  MenuItem,
  Paper,
  Divider,
  Button,
  FormControlLabel,
  Checkbox,
  Stack,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Snackbar,
  Alert,
} from "@mui/material";
import Sidebar from "../components/Sidebar.jsx";
import Header from "../components/Header.jsx";
import BackToDashboard from "../components/BackToDashboard.jsx";
import { mockData } from "../utils/mockData.js";

const RegisterChild = () => {
  const [selectedSchoolId, setSelectedSchoolId] = useState("");
  const [selectedTrainingId, setSelectedTrainingId] = useState("");
  const [schools, setSchools] = useState([]);
  const [trainings, setTrainings] = useState([]);
  const [children, setChildren] = useState([]);
  const [schoolLoading, setSchoolLoading] = useState(false);
  const [trainingLoading, setTrainingLoading] = useState(false);
  const [registerLoading, setRegisterLoading] = useState(false);

  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    dateOfBirth: "",
    street: "",
    city: "",
    zip: "",
    birthNumber: "",
    requestedTrainingId: "",
  });

  const [acceptedTerms, setAcceptedTerms] = useState(false);

  const [openDialog, setOpenDialog] = useState(false);

  const { user } = useAuth();
  const email = user.email;
  const navigate = useNavigate();

  // GET CHILDREN, SCHOOL
  useEffect(() => {
    if (!email) {
      return;
    }

    if (!user?.demo) {
      const fetchData = async () => {
        try {
          setSchoolLoading(true);
          const [children, schools] = await Promise.all([
            ApiService.getParentChildren(email),
            ApiService.getSchools(),
          ]);

          setChildren(children);
          setSchools(schools);

          // SET STREET ZIP AND CITY FROM REGISTERED CHILD
          if (children.length > 0) {
            const child = children[0];

            setForm((prevForm) => ({
              ...prevForm,
              street: child.street || "",
              city: child.city || "",
              zip: child.zip || "",
            }));
          }
        } catch (err) {
          console.error("Failed to fetch children", err);
        } finally {
          setSchoolLoading(false);
        }
      };
      fetchData();
    } else {
      setSchoolLoading(true);
      setTimeout(() => {
        setSchoolLoading(false)
      }, "500")
      setSchools(mockData.schools);
    }
  }, [email]);

  // GET ALL TRAININGS
  useEffect(() => {
    if (!selectedSchoolId) return;

    if (!user?.demo) {
      const fetchTrainings = async () => {
        try {
          setTrainingLoading(true);
          const trainings = await ApiService.getTrainingsBySchoolId(
            selectedSchoolId
          );
          setTrainings(trainings);
        } catch (err) {
          console.error("Failed to fetch trainings", err);
        } finally {
          setTrainingLoading(false);
        }
      };

      fetchTrainings();
    } else {
      if (selectedSchoolId === 1) {
        setTrainings(mockData.trainings[0]);
      } else {
        setTrainings(mockData.trainings[1]);
      }
    }
  }, [selectedSchoolId]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    let newValue = value;

    if (name === "firstName" || name === "lastName" || name === "city") {
      newValue = value.replace(/[^a-zA-ZáčďéěíňóřšťúůýžÁČĎÉĚÍŇÓŘŠŤÚŮÝŽ ]/g, "");
    }

    if (name === "birthNumber") {
      newValue = value
        .replace(/[^0-9/]/g, "")
        .replace(/^([^/]*\/[^/]*)\/.*$/, "$1");

      if (newValue.length > 11) {
        newValue = newValue.slice(0, 11);
      }
    }

    if (name === "zip") {
      if (newValue.length > 5) {
        newValue = newValue.slice(0, 5);
      }
    }

    setForm((prev) => ({
      ...prev,
      [name]: newValue,
    }));
  };

  const handleSchoolChange = (e) => {
    const schoolId = e.target.value;
    setSelectedSchoolId(schoolId);

    setSelectedTrainingId("");

    setForm((prev) => ({
      ...prev,
      requestedTrainingId: "",
    }));
  };

  const handleTrainingChange = (e) => {
    const trainingId = e.target.value;
    setSelectedTrainingId(trainingId);
    setForm((prev) => ({ ...prev, requestedTrainingId: trainingId }));
  };

  // SUBMIT
  const handleSubmit = async (e) => {
    e.preventDefault();
    var message;
    if (form.birthNumber.length < 10) {
      message = "Zadané rodné číslo je neplatné. Zkontrolujte ho prosím.";
      setShowError(true);
      setErrorMessage(message);
      return;
    }

    if (!user?.demo) {
      try {
        setRegisterLoading(true);
        const response = await ApiService.createChild(email, form);

        if (response.message) {
          switch (response.message) {
            case "CHILD_ALREADY_EXISTS_BY_BIRTH_NUMBER":
              message = "Dítě s takovým rodným číslem je již zaregistrované.";
              break;
            case "MAX_CHILDREN_REACHED":
              message =
                "Dosáhli jste maximálního počtu registrovaných dětí. Pokud potřebujete registrovat další dítě, kontaktujte prosím administrátora klubu.";
              break;
            case "CHILD_ALREADY_EXISTS_BY_NAME":
              message =
                "Již jste zaregistroval(a) dítě s takovým jménem i příjmením.";
              break;
            default:
              message = "Nastala neznámá chyba při registraci dítěte.";
          }

          setShowError(true);
          setErrorMessage(message);
        } else {
          navigate("/dashboard");
        }
      } catch (err) {
        console.error("Failed to fetch trainings", err);
      } finally {
        setRegisterLoading(false);
      }
    } else {
      const children = JSON.parse(localStorage.getItem("children") || "[]");

      if (children.length === 5) {
        setShowError(true);
        setErrorMessage("Dosáhli jste maximálního počtu registrovaných dětí. Pokud potřebujete registrovat další dítě, kontaktujte prosím administrátora klubu.");
      } else {
        const newChild = {
          id: children.length + 1,
          firstName: form.firstName,
          lastName: form.lastName,
          requestedTrainingId: form.requestedTrainingId,
        };

        children.push(newChild);
        localStorage.setItem("children", JSON.stringify(children));

        navigate("/dashboard");
      }
    }
  };

  const getCapacityText = (capacity) => {
    if (capacity === 1) {
      return `${capacity} volné místo`;
    } else if (capacity > 1 && capacity < 5) {
      return `${capacity} volná místa`;
    } else {
      return `${capacity} volných míst`;
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
            open={showError}
            autoHideDuration={6000}
            onClose={() => setShowError(false)}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
          >
            <Alert
              onClose={() => setShowError(false)}
              severity="error"
              sx={{ width: "100%" }}
            >
              {errorMessage}
            </Alert>
          </Snackbar>

          {schoolLoading ? (
            <Loading />
          ) : (
            <Box maxWidth={800} mx="auto" my={4}>
              <Paper elevation={3} sx={{ p: 4 }}>
                <Typography
                  variant="h5"
                  fontWeight={600}
                  mb={2}
                  color="#318CE7"
                >
                  Registrace dítěte na trénink
                </Typography>

                <form onSubmit={handleSubmit}>
                  <Stack spacing={2}>
                    <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                      <TextField
                        label="Jméno dítěte"
                        name="firstName"
                        value={form.firstName}
                        onChange={handleChange}
                        fullWidth
                        required
                      />
                      <TextField
                        label="Příjmení dítěte"
                        name="lastName"
                        value={form.lastName}
                        onChange={handleChange}
                        fullWidth
                        required
                      />
                    </Stack>

                    <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                      <TextField
                        label="Datum narození dítěte"
                        name="dateOfBirth"
                        type="date"
                        InputLabelProps={{ shrink: true }}
                        value={form.dateOfBirth}
                        onChange={handleChange}
                        fullWidth
                        required
                        inputProps={{
                          max: new Date().toISOString().split("T")[0],
                        }}
                      />
                      <TextField
                        label="Rodné číslo dítěte"
                        name="birthNumber"
                        value={form.birthNumber}
                        onChange={handleChange}
                        fullWidth
                        required
                        placeholder="000000/0000"
                      />
                    </Stack>

                    <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                      <TextField
                        label="Ulice"
                        name="street"
                        value={form.street}
                        onChange={handleChange}
                        fullWidth
                        required
                      />
                      <TextField
                        label="Město"
                        name="city"
                        value={form.city}
                        onChange={handleChange}
                        fullWidth
                        required
                      />
                      <TextField
                        label="PSČ"
                        name="zip"
                        value={form.zip}
                        onChange={handleChange}
                        type="number"
                        fullWidth
                        required
                      />
                    </Stack>

                    <TextField
                      select
                      label="Škola"
                      value={selectedSchoolId || ""}
                      onChange={handleSchoolChange}
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
                      select
                      label="Trénink"
                      value={selectedTrainingId || ""}
                      onChange={handleTrainingChange}
                      disabled={!selectedSchoolId}
                      fullWidth
                      required
                    >
                      {!selectedSchoolId ? (
                        <MenuItem disabled value="">
                          Nejprve vyberte školu
                        </MenuItem>
                      ) : trainings.length > 0 ? (
                        trainings.map((t) => (
                          <MenuItem
                            key={t.id}
                            value={t.id}
                            sx={{ alignItems: "flex-start" }}
                            disabled={t.capacity < 1}
                          >
                            <Box>
                              <Typography>
                                {getDayName(t.dayOfWeek, true)}{" "}
                                {formatTime(t.startTime)} -{" "}
                                {formatTime(t.endTime)} ({t.name})
                              </Typography>
                              <Typography
                                variant="body2"
                                color="text.secondary"
                              >
                                Cena: {t.price} Kč / rok | Kapacita:{" "}
                                {getCapacityText(t.capacity)}
                              </Typography>
                            </Box>
                          </MenuItem>
                        ))
                      ) : schoolLoading ? (
                        <MenuItem disabled value="">
                          Načítání tréninků...
                        </MenuItem>
                      ) : (
                        <MenuItem disabled value="">
                          Žádné dostupné tréninky
                        </MenuItem>
                      )}
                    </TextField>
                    <Box display="flex" flexDirection="column">
                      <FormControlLabel
                        control={
                          <Checkbox
                            name="terms"
                            checked={acceptedTerms}
                            onChange={(e) => setAcceptedTerms(e.target.checked)}
                          />
                        }
                        label={
                          <span>
                            Souhlasím s{" "}
                            <Button
                              variant="text"
                              onClick={() => setOpenDialog(true)}
                              style={{
                                textTransform: "none",
                                fontSize: "15px",
                                padding: 0,
                                minWidth: 0,
                                verticalAlign: "middle",
                              }}
                            >
                              podmínkami klubu
                            </Button>
                            .
                          </span>
                        }
                      />

                      <Dialog
                        open={openDialog}
                        onClose={() => setOpenDialog(false)}
                        maxWidth="sm"
                        disableRestoreFocus
                      >
                        <DialogTitle>
                          Podmínky klubu a souhlas se zpracováním údajů
                        </DialogTitle>
                        <DialogContent
                          dividers
                          style={{ whiteSpace: "pre-line", fontSize: "14px" }}
                        >
                          <>
                            <strong>
                              Vzhledem k tomu, že každý klub juda je povinen
                              evidovat údaje o svých členech v centrální
                              evidenci Českého svazu juda, je nutné vyplnit i
                              elektronickou přihlášku do klubu.
                            </strong>{" "}
                            Odkaz na tento formulář najdete na stránkách{" "}
                            <a
                              href="https://www.judosgplzen.cz"
                              target="_blank"
                              rel="noopener noreferrer"
                              className="text-blue-600 underline"
                            >
                              www.judosgplzen.cz
                            </a>{" "}
                            nebo přímo{" "}
                            <a
                              href="http://www.czechjudo.org/prihlaska-do-klubu"
                              target="_blank"
                              rel="noopener noreferrer"
                              className="text-blue-600 underline"
                            >
                              www.czechjudo.org/prihlaska-do-klubu
                            </a>
                            .
                            <br />
                            <br />
                            <strong>SOUHLAS</strong> se zpracováním a evidencí
                            osobních údajů podle zák. č. 101/2000 Sb., v platném
                            znění a souhlas s využitím rodného čísla podle
                            zákona č.133/2000Sb. v platném znění.
                            <br />
                            <br />
                            <strong>
                              Souhlasím se zpracováním a evidencí obrazových
                              materiálů: Souhlasím s možností publikovat
                            </strong>{" "}
                            v tisku nebo v elektronických médiích mé fotografie
                            a videonahrávky, které mě zachycují v souvislosti s
                            činností klubu nebo ČSJu. Souhlasím se zpracováním
                            osobních údajů uvedených na adrese{" "}
                            <a
                              href="https://www.judosgplzen.cz/prihlaseni-do-judo-sg-plzen"
                              target="_blank"
                              rel="noopener noreferrer"
                              className="text-blue-600 underline"
                            >
                              www.judosgplzen.cz/prihlaseni-do-judo-sg-plzen
                            </a>{" "}
                            do klubu/souhlas se zpracováním osobních údajů.
                            <br />
                            <br />
                            <strong>
                              Klub a ČSJu se zavazuje s těmito údaji nakládat v
                              souladu s platnými právními předpisy
                            </strong>{" "}
                            (zák. č. 101/2000 Sb., o ochraně osobních údajů) a
                            užívat je pouze v rozsahu nezbytném pro naplnění
                            účelu, pro který jsou osobní údaje získávány a vždy
                            tak, aby zamezil jakékoliv újmě osoby, které se
                            údaje týkají.
                            <br />
                            <br />
                            <strong>
                              Beru na vědomí, že je mojí povinností se seznámit
                              se svým zdravotním stavem a podle platné
                              legislativy pro sportovní kluby je mojí povinností
                              absolvovat lékařskou prohlídku pro sport jedenkrát
                              za rok.
                            </strong>
                          </>
                        </DialogContent>
                        <DialogActions>
                          <Button onClick={() => setOpenDialog(false)}>
                            Zavřít
                          </Button>
                        </DialogActions>
                      </Dialog>
                    </Box>

                    <Divider />

                    <Button
                      type="submit"
                      variant="contained"
                      color="primary"
                      loading={trainingLoading || registerLoading}
                      disabled={!acceptedTerms}
                    >
                      Odeslat registraci
                    </Button>
                  </Stack>
                </form>
              </Paper>
            </Box>
          )}
        </main>
      </div>
    </div>
  );
};

export default RegisterChild;
