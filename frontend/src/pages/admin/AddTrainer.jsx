import React, { useEffect, useState } from "react";
 import Sidebar from "../../components/Sidebar.jsx";
 import Header from "../../components/Header.jsx";
 import BackToDashBoard from "../../components/BackToDashboard.jsx";
 import {
   Box,
   Typography,
   Paper,
   Stack,
   TextField,
   MenuItem,
   Button,
   Snackbar,
   Alert,
 } from "@mui/material";
 import { ApiService } from "../../api/api.js";
 import { getDayName } from "../../utils/trainingUtils.js";
 
 const AddTrainer = () => {
   const [schools, setSchools] = useState([]);
   const [trainings, setTrainings] = useState([]);
   const [trainers, setTrainers] = useState([]);
   const [selectedSchoolId, setSelectedSchoolId] = useState("");
   const [selectedTrainingId, setSelectedTrainingId] = useState("");
   const [selectedTrainerId, setSelectedTrainerId] = useState("");
 
   const [errorMessage, setErrorMessage] = useState("");
   const [showError, setShowError] = useState(false);
 
   const [successMessage, setSuccessMessage] = useState("");
   const [showSuccess, setShowSuccess] = useState(false);
 
   const [addLoading, setAddLoading] = useState(false);
 
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
 
   const handleSchoolSelect = async (schoolId) => {
     setSelectedSchoolId(schoolId);
     setSelectedTrainingId("");
     try {
       const trainingData = await ApiService.getTrainingsBySchoolId(schoolId);
       setTrainings(trainingData);
     } catch (err) {
       console.error("Failed to fetch trainings", err);
     }
   };
 
   const handleTrainingSelect = async (trainingId) => {
     setSelectedTrainingId(trainingId);
     try {
       const trainerData = await ApiService.getAllTrainers();
       setTrainers(trainerData);
     } catch (err) {
       console.error("Failed to fetch trainers", err);
     }
   };
 
   const handleSubmit = async () => {
     if (!selectedTrainingId || !selectedTrainerId) {
       setErrorMessage("Musíš vybrat trénink i trenéra.");
       setShowError(true);
       return;
     }
 
     try {
       setAddLoading(true);
       await ApiService.addTrainerToTraining(
         selectedTrainingId,
         selectedTrainerId
       );
 
       setShowSuccess(true);
       setSuccessMessage("Trenér byl úspěšně přiřazen.");
 
       setSelectedSchoolId("");
       setSelectedTrainingId("");
       setSelectedTrainerId("");
       setTrainers([]);
       setTrainings([]);
     } catch (err) {
       console.error("Failed to assign trainer", err);
       setErrorMessage("Nepodařilo se přiřadit trenéra.");
       setShowError(true);
     } finally {
       setAddLoading(false);
     }
   };
 
   return (
     <div className="flex min-h-screen">
       <Sidebar />
       <div className="flex-grow flex flex-col">
         <Header variant="dashboard" />
         <BackToDashBoard/>
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
                 Přidat trenéra
               </Typography>
 
               <Stack spacing={2}>
                 <TextField
                   select
                   label="Škola"
                   value={selectedSchoolId}
                   onChange={(e) => handleSchoolSelect(e.target.value)}
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
                   value={selectedTrainingId}
                   onChange={(e) => handleTrainingSelect(e.target.value)}
                   fullWidth
                   required
                   disabled={!selectedSchoolId}
                 >
                   {trainings.map((training) => (
                     <MenuItem key={training.id} value={training.id}>
                       {training.name} – {getDayName(training.dayOfWeek)} (
                       {training.startTime[0]}:
                       {training.startTime[1].toString().padStart(2, "0")} -{" "}
                       {training.endTime[0]}:
                       {training.endTime[1].toString().padStart(2, "0")})
                     </MenuItem>
                   ))}
                 </TextField>
 
                 <TextField
                   select
                   label="Trenér"
                   value={selectedTrainerId}
                   onChange={(e) => setSelectedTrainerId(e.target.value)}
                   fullWidth
                   required
                   disabled={!selectedTrainingId || trainers.length === 0}
                 >
                   {trainers.map((trainer) => (
                     <MenuItem key={trainer.id} value={trainer.id}>
                       {trainer.firstName} {trainer.lastName}
                     </MenuItem>
                   ))}
                 </TextField>
 
                 <Button
                   variant="contained"
                   className="bg-judo-blue hover:bg-blue-700 text-white"
                   onClick={handleSubmit}
                   disabled={
                     !selectedTrainerId ||
                     !selectedSchoolId ||
                     !selectedTrainingId
                   }
                   loading={addLoading}
                 >
                   Přiřadit trenéra
                 </Button>
               </Stack>
             </Paper>
           </Box>
         </main>
       </div>
     </div>
   );
 };
 
 export default AddTrainer;