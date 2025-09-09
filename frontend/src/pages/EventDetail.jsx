import React, { useState, useEffect } from "react";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar.jsx";
import Header from "../components/Header.jsx";
import { ApiService } from "../api/api.js";
import { useAuth } from "../context/AuthContext.jsx";
import Loading from "../components/Loading.jsx";
import {
  Check,
  Calendar,
  Clock,
  MapPin,
  Users,
  Phone,
  AtSign,
  Banknote,
  Baby,
  ArrowLeft,
  NotebookPen,
  X,
} from "lucide-react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  IconButton,
} from "@mui/material";
import { mockData } from "../utils/mockData.js";

const EventDetail = () => {
  const { id } = useParams();
  const { state } = useLocation();
  const event = state?.event;
  const { user } = useAuth();
  const role = user.role;
  const email = user.email;
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [removeLoading, setRemoveLoading] = useState(null);
  const [addLoading, setAddLoading] = useState(false);

  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const [childrenStatus, setChildrenStatus] = useState([]);
  const [selectedChild, setSelectedChild] = useState(null);
  const [isChildRegistered, setIsChildRegistered] = useState(false);
  const [note, setNote] = useState("");
  const [registeredChildren, setRegisteredChildren] = useState([]);

  const [registeredTrainers, setRegisteredTrainers] = useState([]);
  const [allTrainers, setAllTrainers] = useState([]);
  const [selectedTrainerId, setSelectedTrainerId] = useState("");

  const fetchChildrenEventStatus = async () => {
    try {
      setLoading(true);
      const data = await ApiService.getChildrenEventStatus(email, id);
      setChildrenStatus(data);
    } catch (err) {
      console.error("Failed to fetch children event status", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchRegisteredChildren = async () => {
    try {
      setLoading(true);
      const data = await ApiService.getEventRegisteredChildren(id);
      setRegisteredChildren(data);
    } catch (err) {
      console.error("Failed to fetch children", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchRegisteredTrainers = async () => {
    try {
      setLoading(true);
      const data = await ApiService.getEventRegisteredTrainers(id);
      setRegisteredTrainers(data);
    } catch (err) {
      console.error("Failed to fetch children", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!role) return;

    if (role === "ROLE_TRAINER") {
      if (!user?.demo) {
        fetchRegisteredChildren();
      } else {
        setLoading(true);
        setTimeout(() => {
          setLoading(false);
        }, "500");
        setRegisteredChildren(mockData.childrenInEvent);
      }
    } else if (role === "ROLE_PARENT") {
      if (!user?.demo) {
        fetchChildrenEventStatus();
      } else {
        const children = JSON.parse(localStorage.getItem("children"));
        if (children) {
          for (const child of children) {
            if (child.isRegistered === undefined) {
              child.isRegistered = false;
            }
          }
          localStorage.setItem("children", JSON.stringify(children));
          setChildrenStatus(children);
        }
      }
    } else if (role === "ROLE_ADMIN") {
      fetchRegisteredChildren();
      fetchRegisteredTrainers();
    }
  }, [id, role, isChildRegistered]);

  const handleRegister = (childId) => {
    if (!user?.demo) {
      const registerChildToEvent = async () => {
        try {
          await ApiService.registerChildToEvent(childId, id, note);
          fetchChildrenEventStatus();
          setSelectedChild(null);
          setNote(null);
        } catch (err) {
          console.error("Failed register child to event", err);
        }
      };
      registerChildToEvent();
    } else {
      let children = JSON.parse(localStorage.getItem("children"))
      let child = children.find((c) => c.id === childId)
      child.isRegistered = true
      localStorage.setItem("children", JSON.stringify(children));
      
      setChildrenStatus(children);
      setSelectedChild(null);
      setNote(null);
    }
  };

  const handleChildPresence = async (childAttendanceId) => {
    try {
      await ApiService.toggleEventAttendance(childAttendanceId);
      setRegisteredChildren((prev) =>
        prev.map((child) =>
          child.id === childAttendanceId
            ? { ...child, present: !child.present }
            : child
        )
      );
    } catch (err) {
      console.error("Failed to toggle child attendance", err);
    }
  };

  const handleChildPayment = async (childAttendanceId) => {
    try {
      await ApiService.toggleChildPayment(childAttendanceId);
      setRegisteredChildren((prev) =>
        prev.map((child) =>
          child.id === childAttendanceId
            ? { ...child, paymentReceived: !child.paymentReceived }
            : child
        )
      );
    } catch (err) {
      console.error("Failed to toggle child attendance", err);
    }
  };

  const handleAddTrainer = async () => {
    setAddLoading(true);
    const trainerToAdd = allTrainers.find(
      (t) => t.id === Number(selectedTrainerId)
    );

    if (trainerToAdd) {
      try {
        await ApiService.addTrainerToEvent(id, trainerToAdd.id);
        setRegisteredTrainers((prev) => [...prev, trainerToAdd]);
        setSelectedTrainerId("");
      } catch (err) {
        console.error(err);
      } finally {
        setAddLoading(false);
      }
    }
    setAddLoading(false);
  };

  const handleRemoveTrainer = async (trainerId) => {
    try {
      setRemoveLoading(trainerId);
      await ApiService.removeTrainerFromEvent(id, trainerId);
      setRegisteredTrainers((prev) =>
        prev.filter((trainer) => trainer.id !== trainerId)
      );
    } catch (err) {
      console.error(err);
    } finally {
      setRemoveLoading(null);
    }
  };

  const fetchAllTrainers = async () => {
    try {
      setAddLoading(true);
      const data = await ApiService.getAllTrainers();
      setAllTrainers(data);
    } catch (err) {
      console.error("Failed to fetch all trainers", err);
    } finally {
      setAddLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <div className="top-17 sm:top-25 px-5 absolute">
          <button
            onClick={() => navigate("/events")}
            className="flex items-center text-judo-blue hover:underline text-[15px] hover:cursor-pointer"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Zpět na eventy
          </button>
        </div>
        <main className="flex-grow p-8 bg-gray-50 flex flex-col gap-6 items-center">
          {loading ? (
            <Loading />
          ) : (
            <>
              <Dialog
                open={isDialogOpen}
                onClose={() => setIsDialogOpen(false)}
                disableRestoreFocus
              >
                <DialogTitle>Potvrzení přihlášení na akci</DialogTitle>
                <DialogContent>
                  {selectedChild && (
                    <div className="space-y-4">
                      <p className="text-slate-700">
                        Chcete opravdu přihlásit dítě{" "}
                        <strong>
                          {selectedChild.firstName} {selectedChild.lastName}
                        </strong>{" "}
                        na akci <strong>{event.name}</strong>? Přihlášku nebude
                        možné dodatečně zrušit.
                      </p>

                      <div className="flex flex-col mt-6">
                        <label
                          htmlFor="note"
                          className="text-sm font-semibold text-slate-700 mb-1"
                        >
                          Poznámka k přihlášení (např. alergie, dřívější odchod
                          domů):
                        </label>
                        <textarea
                          id="note"
                          name="note"
                          rows="4"
                          placeholder="Zde můžete uvést dodatečné informace..."
                          className="border border-slate-300 rounded-lg p-3 text-sm focus:outline-none focus:ring-2 focus:ring-judo-blue"
                          value={note}
                          onChange={(e) => setNote(e.target.value)}
                        />
                      </div>
                    </div>
                  )}
                </DialogContent>

                <DialogActions>
                  <Button onClick={() => setIsDialogOpen(false)}>Zrušit</Button>
                  <Button
                    onClick={() => {
                      setIsDialogOpen(false);
                      handleRegister(selectedChild.id);
                    }}
                    variant="contained"
                    color="primary"
                  >
                    Přihlásit
                  </Button>
                </DialogActions>
              </Dialog>

              <div className="bg-white rounded-lg shadow-md w-full max-w-3xl p-6">
                <h1 className="text-2xl font-semibold text-slate-800 mb-4">
                  {event.name}
                </h1>

                <div className="text-slate-600 space-y-3 text-[16px]">
                  <div className="flex items-center">
                    <Calendar className="w-5 h-5 mr-2 text-judo-blue" />
                    Datum:{" "}
                    {event.endDate
                      ? `${event.startDate} - ${event.endDate}`
                      : event.startDate}
                  </div>
                  {event.startTime && event.endTime && (
                    <div className="flex items-center">
                      <Clock className="w-5 h-5 mr-2 text-judo-blue" />
                      Čas: {event.startTime} - {event.endTime}
                    </div>
                  )}
                  <div className="flex items-center">
                    <MapPin className="w-5 h-5 mr-2 text-judo-blue" />
                    Místo konání: {event.location}
                  </div>
                  <div className="flex items-center">
                    <Users className="w-5 h-5 mr-2 text-judo-blue" />
                    {event.places - event.takenPlaces} volných míst
                  </div>
                  <div className="flex items-center">
                    <Banknote className="w-5 h-5 mr-2 text-judo-blue" />
                    {event.price !== "0" ? `Cena: ${event.price} Kč` : "Zdarma"}
                  </div>
                </div>

                <div className="mt-6">
                  <h2 className="text-xl font-semibold text-slate-800 mb-2">
                    Popis akce
                  </h2>
                  <p className="text-slate-700 leading-relaxed whitespace-pre-line">
                    {event.description}
                  </p>
                </div>

                {role === "ROLE_ADMIN" && (
                  <div className="mt-8 mb-6 w-full">
                    <h2 className="text-xl font-semibold text-slate-800 mb-4">
                      Přidat trenéra na akci
                    </h2>
                    <div className="flex flex-col sm:flex-row gap-4">
                      <select
                        className="border border-slate-300 rounded-md p-2"
                        value={selectedTrainerId}
                        onChange={(e) => setSelectedTrainerId(e.target.value)}
                        onClick={() => {
                          if (allTrainers.length === 0) {
                            fetchAllTrainers();
                          }
                        }}
                      >
                        <option value="">Vyberte trenéra</option>
                        {allTrainers
                          .filter(
                            (trainer) =>
                              !registeredTrainers.some(
                                (t) => t.id === trainer.id
                              )
                          )
                          .map((trainer) => (
                            <option key={trainer.id} value={trainer.id}>
                              {trainer.firstName} {trainer.lastName}
                            </option>
                          ))}
                      </select>
                      <Button
                        variant="contained"
                        onClick={handleAddTrainer}
                        loading={addLoading}
                        disabled={!selectedTrainerId}
                      >
                        Přidat trenéra
                      </Button>
                    </div>

                    {registeredTrainers.length !== 0 && (
                      <div className="mt-8 w-full">
                        <h2 className="text-xl font-semibold text-slate-800 mb-4">
                          Trenéři na této akci
                        </h2>
                        <div className="grid gap-2">
                          {registeredTrainers.map((trainer) => (
                            <div
                              key={trainer.id}
                              className="flex flex-row items-center justify-between border border-slate-100 rounded-lg p-3 bg-slate-50"
                            >
                              <span className="font-medium text-slate-800">
                                {trainer.firstName} {trainer.lastName}
                              </span>
                              <Button
                                variant="outlined"
                                color="error"
                                loading={trainer.id === removeLoading}
                                onClick={() => handleRemoveTrainer(trainer.id)}
                              >
                                Odebrat
                              </Button>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                )}

                {childrenStatus.length !== 0 && role === "ROLE_PARENT" && (
                  <div className="mt-6">
                    <h2 className="text-xl font-semibold text-slate-800 mb-4">
                      Přihlásit dítě na akci
                    </h2>
                    <div className="grid gap-2">
                      {[...childrenStatus]
                        .sort((a, b) => a.lastName.localeCompare(b.lastName))
                        .map((child) => (
                          <div
                            key={child.id}
                            className="flex flex-row items-center justify-between border border-slate-100 rounded-lg p-3 gap-2"
                            style={{
                              backgroundColor: child.isRegistered
                                ? "#e6f4ea"
                                : "#f8fafc",
                            }}
                          >
                            <div className="flex items-center gap-2 text-sm sm:text-base">
                              <Baby className="w-6 h-6 text-judo-blue flex-shrink-0" />
                              <span className="font-medium text-slate-800">
                                {child.firstName} {child.lastName}
                              </span>
                            </div>

                            {child.isRegistered ? (
                              <div className="ml-auto flex items-center gap-2 text-green-700 font-medium text-[10px] sm:text-sm">
                                <Check className="w-3 h-3 sm:w-5 sm:h-5 text-green-600" />
                                <span>Dítě je přihlášeno na akci</span>
                              </div>
                            ) : (
                              <button
                                onClick={() => {
                                  setSelectedChild(child);
                                  setIsDialogOpen(true);
                                }}
                                className="ml-auto text-judo-blue border border-judo-blue px-2 py-1 sm:px-3 sm:py-1 rounded-md text-xs sm:text-sm font-medium transition duration-200 ease-in-out cursor-pointer hover:bg-blue-50 hover:shadow-sm hover:border-blue-800 hover:text-blue-800"
                              >
                                Přihlásit dítě na akci
                              </button>
                            )}
                          </div>
                        ))}
                    </div>
                  </div>
                )}

                {registeredChildren.length !== 0 &&
                  (role === "ROLE_TRAINER" || role === "ROLE_ADMIN") && (
                    <div className="mt-6 w-full">
                      <h2 className="text-xl font-semibold text-slate-800 mb-4">
                        Registrované děti
                      </h2>
                      <div className="grid gap-3">
                        {registeredChildren
                          .sort((a, b) => {
                            const lastNameA = a.name.split(" ").slice(-1)[0];
                            const lastNameB = b.name.split(" ").slice(-1)[0];
                            return lastNameA.localeCompare(lastNameB);
                          })
                          .map((child, index) => (
                            <div
                              key={index}
                              className="flex flex-col sm:flex-row sm:items-start sm:justify-between border border-slate-100 rounded-lg p-4 bg-slate-50 gap-4"
                            >
                              <div className="flex flex-col gap-2 flex-1">
                                <div className="flex items-center justify-between">
                                  <div className="flex items-center gap-2 text-sm sm:text-base ">
                                    {!user?.demo && (
                                      <Banknote
                                        onClick={() =>
                                          handleChildPayment(child.id)
                                        }
                                        className={`hover:cursor-pointer w-6 h-6 flex-shrink-0`}
                                        style={{
                                          color: child.paymentReceived
                                            ? "#4caf50"
                                            : "#f44336",
                                        }}
                                      />
                                    )}
                                    <span className="font-medium text-[17px] text-slate-800">
                                      {child.name}
                                    </span>
                                  </div>
                                  {!user?.demo && (
                                    <IconButton
                                      size="small"
                                      sx={{
                                        width: 30,
                                        height: 30,
                                        backgroundColor: child.present
                                          ? "#4caf50"
                                          : "#f44336",
                                        borderRadius: "8px",
                                        border: "1px solid #d1d5db",
                                        color: "white",
                                        "&:hover": {
                                          backgroundColor: child.present
                                            ? "#43a047"
                                            : "#e53935",
                                        },
                                        marginRight: "15px",
                                      }}
                                      onClick={() =>
                                        handleChildPresence(child.id)
                                      }
                                    >
                                      {child.present ? (
                                        <Check fontSize="small" />
                                      ) : (
                                        <X fontSize="small" />
                                      )}
                                    </IconButton>
                                  )}
                                </div>

                                <div className="flex flex-col gap-2 text-sm sm:text-base ">
                                  {child.note !== "null" && (
                                    <div className="flex items-center gap-1">
                                      <NotebookPen className="w-5 h-5 flex-shrink-0 text-judo-blue" />
                                      <span className="text-slate-950">
                                        {child.note.substring(
                                          1,
                                          child.note.length - 1
                                        )}
                                      </span>
                                    </div>
                                  )}

                                  <div className="flex flex-col sm:flex-row sm:items-center gap-2 sm:gap-6">
                                    <div className="flex items-center gap-1">
                                      <Phone className="w-5 h-5 flex-shrink-0 text-judo-blue" />
                                      <span className="text-slate-950">
                                        {child.phoneNumber}
                                      </span>
                                    </div>
                                    <div className="flex items-center gap-1">
                                      <AtSign className="w-5 h-5 flex-shrink-0 text-judo-blue" />
                                      <span className="text-slate-950">
                                        {child.email}
                                      </span>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          ))}
                      </div>
                    </div>
                  )}
              </div>
            </>
          )}
        </main>
      </div>
    </div>
  );
};

export default EventDetail;
