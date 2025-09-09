import React from "react";
import {
  Calendar,
  Clock,
  School,
  PersonStanding,
  Users,
  Phone,
  Baby,
} from "lucide-react";

const ParentUpcomingTrainingCard = ({ trainings }) => {
  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-[25px] font-semibold text-slate-800">
          Nadcházející tréninky
        </h2>
      </div>
      <div
        className={`grid gap-4 ${
          trainings.length === 1 ? "grid-cols-1" : "grid-cols-1 sm:grid-cols-2"
        }`}
      >
        {[...trainings]
          .sort((a, b) => {
            const dateA = new Date(a.date);
            const dateB = new Date(b.date);
            if (dateA - dateB !== 0) {
              return dateA - dateB;
            }

            const [startHourA, startMinuteA] = a.time
              .split(" - ")[0]
              .split(":")
              .map(Number);
            const [startHourB, startMinuteB] = b.time
              .split(" - ")[0]
              .split(":")
              .map(Number);
            if (startHourA !== startHourB) return startHourA - startHourB;
            return startMinuteA - startMinuteB;
          })
          .map((training) => (
            <div
              key={training.id}
              className="border border-slate-100 rounded-lg p-4 bg-white"
            >
              <div className="flex justify-between items-start mb-2">
                <div className="flex items-center justify-center">
                  <School className="w-7 h-7 mr-2 text-judo-blue" />
                  <h3 className="font-medium text-[21px] text-slate-800">
                    {training.location}
                  </h3>
                </div>
              </div>

              <div className="text-slate-600 space-y-2 text-sm">
                <div className="flex items-center">
                  <PersonStanding className="w-5 h-5 mr-2 text-judo-blue" />
                  <span className="text-[16px]">{training.title}</span>
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

              <hr className="my-4 border-slate-200" />

              <div className="text-slate-600 space-y-2 text-sm">
              <div className="flex items-center">
                  <Baby className="w-5 h-5 mr-2 text-judo-blue flex-shrink-0" />
                  <span className="text-[16px]">
                    Dítě: {" "}
                    {training.childNames.join(", ")}
                    </span>
                </div>

                <div className="flex items-center">
                  <Users className="w-5 h-5 mr-2 text-judo-blue flex-shrink-0" />
                  <span className="text-[16px]">
                    Trenér:{" "}
                    {training.trainerNames.join(", ")}
                  </span>
                </div>
                <div className="flex items-center">
                  <Phone className="w-5 h-5 mr-2 text-judo-blue flex-shrink-0" />
                  <span className="text-[16px]">
                    {training.trainerPhoneNumbers.join(", ")}
                  </span>
                </div>
              </div>
            </div>
          ))}
      </div>
    </div>
  );
};

export default ParentUpcomingTrainingCard;
