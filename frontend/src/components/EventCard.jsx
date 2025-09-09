import React from "react";
import { Calendar, Clock, Users, MapPin, Banknote } from "lucide-react";
import { Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

const EventCard = ({ events }) => {
  const navigate = useNavigate();

  return (
    <>
      {events.length > 0 ? (
        <div className="bg-white rounded-lg shadow-md p-6 mt-[18px] sm:w-[900px]">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-[25px] font-semibold text-slate-800">Akce</h2>
          </div>
          <div
            className="grid gap-4 grid-cols-1 sm:grid-cols-2"
          >
            {[...events]
              .sort((a, b) => {
                const dateA = new Date(a.date);
                const dateB = new Date(b.date);
                if (dateA - dateB !== 0) {
                  return dateB - dateA;
                }
              })
              .map((event) => (
                <div
                  key={event.id}
                  className="border border-slate-100 rounded-lg p-4 bg-white flex flex-col justify-between h-full"
                >
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex-1">
                      <h3 className="font-medium text-[21px] text-slate-800 break-words">
                        {event.name}
                      </h3>
                    </div>

                    <span className="text-sm px-2 py-1 mt-[5px] ml-[5px] bg-blue-50 text-judo-blue rounded-full w-[65px] flex-shrink-0 text-center flex items-center justify-center">
                      <div className="flex items-center gap-1">
                        <Users size={14} className="shrink-0" />
                        <span className="leading-none">
                          {event.takenPlaces}/{event.places}
                        </span>
                      </div>
                    </span>
                  </div>

                  <div className="text-slate-600 space-y-2 text-sm">
                    <div className="flex items-center">
                      <Calendar className="w-5 h-5 mr-2 text-judo-blue shrink-0" />
                      <span className="text-[16px]">
                        {event.endDate
                          ? `${event.startDate} - ${event.endDate}`
                          : event.startDate}
                      </span>
                    </div>
                    <div className="flex items-center">
                      <MapPin className="w-5 h-5 mr-2 text-judo-blue shrink-0" />
                      <span className="text-[16px]">{event.location}</span>
                    </div>
                    {event.startTime && event.endTime && (
                      <div className="flex items-center">
                        <Clock className="w-5 h-5 mr-2 text-judo-blue shrink-0" />
                        <span className="text-[16px]">
                          {event.startTime} - {event.endTime}
                        </span>
                      </div>
                    )}
                    <div className="flex items-center">
                      <Banknote className="w-5 h-5 mr-2 text-judo-blue shrink-0" />
                      <span className="text-[16px]">
                        {event.price !== "0" ? `${event.price} Kč` : "Zdarma"}
                      </span>
                    </div>
                  </div>
                  <div className="flex justify-end">
                    <Button
                      onClick={() =>
                        navigate(`/events/detail/${event.id}`, {
                          state: { event },
                        })
                      }
                    >
                      Detaily akce
                    </Button>
                  </div>
                </div>
              ))}
          </div>
        </div>
      ) : (
        <p className="text-gray-600 text-center mt-8">Momentálně nejsou vypsané žádné akce.</p>
      )}
    </>
  );
};

export default EventCard;
