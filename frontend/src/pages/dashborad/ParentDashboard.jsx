import React from "react";
import { useNavigate } from "react-router-dom";
import { Typography, Paper, Button, Alert } from "@mui/material";
import ParentUpcomingTrainingCard from "../../components/parent/ParentUpcomingTrainingCard";
import NewsCard from "../../components/NewsCard";
import Loading from "../../components/Loading";

const ParentDashboard = ({ upcomingTrainings, news, children, isLoading }) => {
  const navigate = useNavigate();

  if (isLoading) {
    return <Loading />;
  }

  return (
    <>
      {children?.map((child) => {
        if (child.requestedTrainingId) {
          return (
            <Alert
              key={child.id}
              severity="info"
              sx={{
                mb: 2,
                borderRadius: 2,
                backgroundColor: "#E3F2FD",
                border: "1px solid #90CAF9",
                color: "#0D47A1",
                fontWeight: 500,
                fontSize: "15px",
              }}
            >
              Registrace Vašeho dítěte{" "}
              <strong>
                {child.firstName} {child.lastName}
              </strong>{" "}
              je v procesu schválení administrátorem.
            </Alert>
          );
        } else if (
          !child.requestedTrainingId &&
          upcomingTrainings.length === 0
        ) {
          return (
            <React.Fragment key={child.id}>
              <Alert
                severity="info"
                sx={{
                  mb: 2,
                  borderRadius: 2,
                  backgroundColor: "#FFF8E1",
                  border: "1px solid #FFECB3",
                  color: "#795548",
                  fontWeight: 500,
                  fontSize: "15px",
                  "& .MuiAlert-icon": {
                    color: "#795548",
                  },
                }}
              >
                Dítě{" "}
                <strong>
                  {child.firstName} {child.lastName}
                </strong>{" "}
                bylo přiřazeno k tréninku. V případě, že chcete zaregistrovat
                další dítě, můžete tak učinit pomocí tlačítka níže nebo v
                postranním menu.
              </Alert>
            </React.Fragment>
          );
        }
        return null;
      })}

      {children?.some((child) => !child.requestedTrainingId && upcomingTrainings.length === 0) && (
        <div className="flex justify-center">
          <Button
            variant="contained"
            color="primary"
            size="md"
            onClick={() => navigate("/register-child")}
            sx={{ ml: 2, whiteSpace: "nowrap", mb: 2 }}
          >
            Přidat další dítě
          </Button>
        </div>
      )}

      {upcomingTrainings && upcomingTrainings.length > 0 ? (
        <ParentUpcomingTrainingCard news={news} trainings={upcomingTrainings} />
      ) : children.length === 0 ? (
        <Paper sx={{ padding: 4, textAlign: "center" }}>
          <Typography
            variant="body1"
            sx={{ color: "text.secondary", mb: 2, fontSize: "17px" }}
          >
            Nezaregistroval(a) jste dítě na žádný trénink. Použijte tlačítko
            níže nebo v postranním menu pro registraci.
          </Typography>
          <Button
            variant="contained"
            color="primary"
            onClick={() => navigate("/register-child")}
            sx={{
              padding: {
                xs: "6px 10px",
                md: "10px 18px",
              },
            }}
          >
            Zaregistrovat dítě na trénink
          </Button>
        </Paper>
      ) : null}

      <NewsCard news={news} />
    </>
  );
};

export default ParentDashboard;
