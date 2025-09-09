import React from "react";
import TrainerUpcomingTrainingsCard from "../../components/trainer/TrainerUpcomingTrainingsCard";
import TrainerPastTrainingsCard from "../../components/trainer/TrainerPastTrainingsCard";
import Loading from "../../components/Loading";

const TrainerDashboard = ({ upcomingTrainings, pastTrainings, setPastTrainings, isLoading }) => {
  if (isLoading) {
    return <Loading />;
  }

  return (
    <>
      <TrainerUpcomingTrainingsCard trainings={upcomingTrainings} />
      <TrainerPastTrainingsCard trainings={pastTrainings} setTrainings={setPastTrainings} />
    </>
  );
};

export default TrainerDashboard;
