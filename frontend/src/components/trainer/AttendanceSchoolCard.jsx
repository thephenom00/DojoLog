import React, { useState, useEffect } from "react";
import {
  Calendar,
  Clock,
  School,
  PersonStanding,
  Pen,
  Info,
} from "lucide-react";
import { ApiService } from "../../api/api.js";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Divider,
} from "@mui/material";
import { useAuth } from "../../context/AuthContext.jsx";

const AttendanceShoolCard = ({ training, setTraining }) => {
  const [editingId, setEditingId] = useState(null);
  const [tempDescription, setTempDescription] = useState("");
  const [openDialog, setOpenDialog] = useState(false);
  const { user } = useAuth();

  const getDescription = () => {
    const stored = localStorage.getItem("upcomingTrainingDescription");
    const arr = stored ? JSON.parse(stored) : [];
    return arr.find((entry) => entry.id === training.id);
  };

  useEffect(() => {
    if (!user?.demo && training?.id) {
      const found = getDescription();
      if (found) {
        setTraining((prev) => ({
          ...prev,
          description: found.description,
        }));
      }
    }
  }, [user, training?.id]);

  const handleEditClick = (id, description) => {
    setEditingId(id);
    if (!user?.demo) {
      if (description !== null) {
        setTempDescription(description);
      }
    } else {
      if (getDescription()) {
        setTempDescription(getDescription().description);
      }
    }
  };

  const handleSave = async (id) => {
    if (!user?.demo) {
      try {
        const updatedTraining = await ApiService.updateTrainingUnitDescription(
          id,
          tempDescription
        );

        setTraining({
          ...training,
          description: updatedTraining.description,
        });

        setEditingId(null);
      } catch (error) {
        console.error("Error updating description:", error);
        alert("Failed to update description.");
      }
    } else {
      const storage = localStorage.getItem("upcomingTrainingDescription");
      const arr = storage ? JSON.parse(storage) : [];

      const index = arr.findIndex((entry) => entry.id === id);

      if (index !== -1) {
        arr[index].description = tempDescription;
      } else {
        arr.push({ id, description: tempDescription });
      }

      localStorage.setItem("upcomingTrainingDescription", JSON.stringify(arr));

      setTraining({
        ...training,
        description: tempDescription,
      });

      setEditingId(null);
    }
  };

  const handleCancel = () => {
    setEditingId(null);
    setTempDescription("");
  };

  return (
    <div className="w-full max-w-xl bg-white rounded-xl shadow-md p-6 border border-slate-200">
      <div className="flex flex-col items-center mb-6 space-y-3">
        <div className="flex items-center space-x-3">
          <School className="w-8 h-8 text-judo-blue" />
          <h3 className="font-medium text-[22px] text-slate-800 text-center">
            {training.schoolName}
          </h3>
        </div>
      </div>

      <div className="flex flex-col items-center sm:flex-row sm:justify-center sm:space-x-4 space-y-3 sm:space-y-0 text-slate-600 text-sm mb-6">
        <div className="flex items-center">
          <PersonStanding className="w-5 h-5 mr-2 text-judo-blue" />
          <span className="text-[16px]">{training.name}</span>
        </div>
        <div className="flex items-center">
          <Calendar className="w-5 h-5 mr-2 text-judo-blue" />
          <span className="text-[16px]">
            {training.dayOfTheWeek}{" "}
            {new Date(training.date).toLocaleDateString()}
          </span>
        </div>
        <div className="flex items-center">
          <Clock className="w-5 h-5 mr-2 text-judo-blue" />
          <span className="text-[16px]">{training.time}</span>
        </div>
      </div>

      <div className="flex items-start text-slate-600 max-h-[90px] overflow-y-auto">
        {(training.description || getDescription()?.description) && (
          <Pen className="w-5 h-5 mr-2 mt-1 text-judo-blue shrink-0" />
        )}
        {editingId === training.id ? (
          <textarea
            className="border border-gray-300 rounded m-2 text-[14px] w-full"
            value={tempDescription}
            rows={3}
            onChange={(e) => setTempDescription(e.target.value)}
          />
        ) : (
          <div className="text-[15px] whitespace-pre-wrap break-all">
            {!user?.demo ? training.description : getDescription()?.description}
          </div>
        )}
      </div>

      <div className="flex justify-between mt-4">
        <button
          className="w-[100px] h-[50px] sm:w-auto sm:h-auto hover:cursor-pointer text-sm px-2 py-2 bg-judo-blue text-white hover:bg-blue-700 rounded transition flex justify-center items-center"
          onClick={() => setOpenDialog(true)}
        >
          ⓘ Detaily tréninku
        </button>

        {editingId === training.id ? (
          <div className="flex gap-2 justify-center items-center">
            <button
              className="hover:cursor-pointer w-[60px] h-[35px] sm:w-auto sm:h-auto text-sm px-3 py-2 text-white bg-emerald-500 hover:bg-emerald-600 rounded flex justify-center items-center "
              onClick={() => handleSave(training.id)}
            >
              Uložit
            </button>
            <button
              className="hover:cursor-pointer text-sm px-3 py-2 text-white bg-rose-500 hover:bg-rose-600 rounded"
              onClick={handleCancel}
            >
              Zrušit
            </button>
          </div>
        ) : (
          <button
            className={`w-[130px] h-[50px] sm:w-auto sm:h-auto hover:cursor-pointer text-sm px-3 py-2 rounded transition flex justify-center items-center ${
              training.description || getDescription()?.description
                ? "text-gray-500 hover:text-gray-700"
                : "bg-judo-blue text-white hover:bg-blue-600"
            }`}
            onClick={() => handleEditClick(training.id, training.description)}
          >
            {training.description || getDescription()?.description
              ? "Upravit"
              : "Vyplnit program tréninku"}
          </button>
        )}
      </div>

      <Dialog
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle
          sx={{ fontWeight: 600, fontSize: "20px", color: "#318CE7" }}
        >
          ⓘ Detaily tréninku
        </DialogTitle>
        <DialogContent dividers className="space-y-4">
          <Typography variant="body1" sx={{ fontSize: "16px" }}>
            <strong>Kontaktní osoba:</strong>{" "}
            {training.contactPerson || "Neuvedeno"}
          </Typography>

          <Typography variant="body1" sx={{ fontSize: "16px" }}>
            <strong>Telefon:</strong> {training.contactNumber || "Neuvedeno"}
          </Typography>

          <Divider sx={{ my: 2 }} />

          <Typography
            variant="body1"
            sx={{ whiteSpace: "pre-line", fontSize: "16px" }}
          >
            <strong>Instrukce:</strong>
            <br />
            {training.instructions || "Žádné instrukce nezadány."}
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)} variant="outlined">
            Zavřít
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default AttendanceShoolCard;
