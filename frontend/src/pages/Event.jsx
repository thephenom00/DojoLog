import React, { useState, useEffect } from "react";
import Sidebar from "../components/Sidebar.jsx";
import Header from "../components/Header.jsx";
import EventCard from "../components/EventCard.jsx";
import { ApiService } from "../api/api.js";
import { formatDate, formatTime } from "../utils/trainingUtils.js";
import Loading from "../components/Loading.jsx";
import BackToDashboard from "../components/BackToDashboard.jsx";
import CreateEvent from "./admin/CreateEvent.jsx";
import { useAuth } from "../context/AuthContext.jsx";
import { mockData } from "../utils/mockData.js";

const Event = () => {
  const { user } = useAuth();
  const role = user.role;

  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isEvents, setIsEvents] = useState(true);

  useEffect(() => {
    if (isEvents) {
      if (!user?.demo) {
        const fetchEvent = async () => {
          try {
            setLoading(true);
            const data = await ApiService.getEvents();
            const formatted = data.map((event) => ({
              ...event,
              startDate: formatDate(event.startDate),
              endDate: event.endDate ? formatDate(event.endDate) : null,
              startTime: event.startTime ? formatTime(event.startTime) : null,
              endTime: event.endTime ? formatTime(event.endTime) : null,
              price: event.price.toLocaleString("cs-CZ"),
            }));
            setEvents(formatted);
          } catch (err) {
            console.error("Failed to fetch events", err);
          } finally {
            setLoading(false);
          }
        };
        fetchEvent();
      } else {
        {/*** DEMO EVENTS ***/}
        setLoading(true)
        setTimeout(() => {
          setLoading(false)
        }, "500")
        setEvents(mockData.events);
      }
    }
  }, [isEvents]);

  const handleEventCreated = () => {
    setIsEvents(true);
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard />
        <main className="flex-grow p-8 bg-gray-50 flex flex-col items-center">
          {role === "ROLE_ADMIN" && (
            <div className="flex bg-gray-100 rounded-lg p-1 mt-[20px]">
              <button
                onClick={() => setIsEvents(true)}
                className={`flex-1 w-[200px] py-3 px-3 rounded-md transition-all duration-300 ease-in-out hover:cursor-pointer ${
                  isEvents
                    ? "bg-white shadow-sm text-judo-blue font-semibold"
                    : "text-gray-600"
                }`}
              >
                Zobrazit akce
              </button>
              <button
                onClick={() => setIsEvents(false)}
                className={`flex-1 py-3 px-3 rounded-md transition-all duration-300 ease-in-out hover:cursor-pointer ${
                  isEvents
                    ? "text-gray-600"
                    : "bg-white shadow-sm text-judo-blue font-semibold"
                }`}
              >
                Vytvořit akci
              </button>
            </div>
          )}

          {loading ? (
            <Loading />
          ) : !isEvents && role === "ROLE_ADMIN" ? (
            <CreateEvent onEventCreated={handleEventCreated} />
          ) : (
            <EventCard events={events} />
          )}
        </main>
      </div>
    </div>
  );
};

export default Event;
