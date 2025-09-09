export const getDayName = (input, cz=true) => {
  const days = [
    { en: "Monday", cz: "Pondělí" },
    { en: "Tuesday", cz: "Úterý" },
    { en: "Wednesday", cz: "Středa" },
    { en: "Thursday", cz: "Čtvrtek" },
    { en: "Friday", cz: "Pátek" },
    { en: "Saturday", cz: "Sobota" },
    { en: "Sunday", cz: "Neděle" },
  ];

  if (typeof input === "string") {
    const normalized = input.trim().toLowerCase();
    if (cz) {
      const match = days.find((day) => day.en.toLowerCase() === normalized);
      return match?.cz || "";
    } else {
      const match = days.find((day) => day.cz.toLowerCase() === normalized);
      return match?.en || "";
    }
  }
  

  return "";
};


export const mapTrainingData = (training, isTrainer=true) => {
  const date = new Date(
    training.date[0],
    training.date[1] - 1,
    training.date[2]
  );
  const start = `${training.startTime[0]
    .toString()
    .padStart(2, "0")}:${training.startTime[1].toString().padStart(2, "0")}`;
  const end = `${training.endTime[0]
    .toString()
    .padStart(2, "0")}:${training.endTime[1].toString().padStart(2, "0")}`;
  const dayInEnglish = date.getDay();

  if (isTrainer) {
    return {
      id: training.id,
      schoolName: training.schoolName,
      name: training.name,
      description: training.description,
      date: `${date}`,
      dayOfTheWeek: getDayName(dayInEnglish, true),
      time: `${start} - ${end}`,
      numberOfChildren: training.numberOfChildren,
      instructions: training.instructions,
      contactPerson: training.contactPerson,
      contactNumber: training.contactNumber
    };
  } else {
    return {
      id: training.id,
      location: training.schoolName,
      title: training.name,
      date: `${date}`,
      dayOfTheWeek: getDayName(dayInEnglish, true),
      time: `${start} - ${end}`,
      childNames: training.childNames,
      trainerNames: training.trainerNames,
      trainerPhoneNumbers: training.trainerPhoneNumbers,
    }
  }


};


export const formatDate = (dateArray) => {
  if (!Array.isArray(dateArray)) return "";
  const [year, month, day] = dateArray;
  return `${day.toString().padStart(2, "0")}.${month
    .toString()
    .padStart(2, "0")}.${year}`;
};

export const formatTime = (time) => {      
  if (!Array.isArray(time)) return "";
  const [hour, minute] = time;
  return `${hour.toString().padStart(2, "0")}:${minute
    .toString()
    .padStart(2, "0")}`;
};
