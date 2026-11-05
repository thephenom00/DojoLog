import React from "react";
import { useNavigate } from "react-router-dom";
import {
  School,
  UserPlus,
  Newspaper,
  CalendarPlus,
  CheckSquare,
  Accessibility,
  CalendarDays,
  ChartLine,
  UserRoundPlus,
} from "lucide-react";
import { ApiService } from "../../api/api";

const activeActions = [
  {
    icon: <Accessibility className="w-6 h-6 text-judo-blue" />,
    title: "Vytvořit záskok",
    description: "Přidat záskok za nepřítomného trenéra.",
    link: "/admin/add-trainer-substitution",
  },
  {
    icon: <CheckSquare className="w-6 h-6 text-judo-blue" />,
    title: "Schválit registraci dítěte",
    description: "Potvrdit žádost rodiče o přiřazení dítěte k tréninku.",
    link: "/admin/approve-registration",
  },
  {
    icon: <Newspaper className="w-6 h-6 text-judo-blue" />,
    title: "Publikovat aktualitu",
    description: "Zveřejnit oznámení, které se zobrazí trenérům a rodičům.",
    link: "/admin/create-news",
  },
  {
    icon: <CalendarDays className="w-6 h-6 text-judo-blue" />,
    title: "Akce",
    description: "Zobrazit seznam naplánovaných akcí nebo vytvořit novou akci.",
    link: "/events",
  },
  {
    icon: <ChartLine className="w-6 h-6 text-judo-blue" />,
    title: "Výkaz trenérů",
    description: "Vygenerovat výkaz pro trenéra.",
    link: "/admin/generate-report",
  },
];

const setupActions = [
  {
    icon: <School className="w-6 h-6 text-judo-blue" />,
    title: "Vytvořit školu",
    description: "Přidání nové školy do systému.",
    link: "/admin/create-school",
  },
  {
    icon: <CalendarPlus className="w-6 h-6 text-judo-blue" />,
    title: "Vytvořit trénink",
    description: "Založení nového tréninku a přiřazení ke škole.",
    link: "/admin/create-training",
  },
  {
    icon: <UserPlus className="w-6 h-6 text-judo-blue" />,
    title: "Zaregistrovat trenéra",
    description: "Zaregistrovat trenéra do systému",
    link: "/admin/register-trainer",
  },
  {
    icon: <CalendarPlus className="w-6 h-6 text-judo-blue" />,
    title: "Přidat prázdniny",
    description: "Přiřadit prázdniny ke všem nebo konkrétní škole.",
    link: "/admin/add-holiday",
  },
  {
    icon: <UserRoundPlus className="w-6 h-6 text-judo-blue" />,
    title: "Přiřadit trenéra",
    description: "Přidat existujícího trenéra k tréninku.",
    link: "/admin/add-trainer",
  },
];

const AdminDashboard = () => {
  const navigate = useNavigate();

  // TIME MACHINE
  const empty = async () => {
    ApiService.timeMachine();
  };

  return (
    <>
      <div className="mb-8">
        {/* <div className="mb-6">
          <button
            onClick={empty}
            className="hover:cursor-pointer px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow"
          >
            STROJ ČASU
          </button>
        </div> */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {activeActions.map((action, index) => (
            <div
              key={index}
              onClick={() => navigate(action.link)}
              className="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm hover:shadow-md cursor-pointer transition duration-200"
            >
              <div className="flex items-center gap-3 mb-3">
                {action.icon}
                <h2 className="text-lg font-semibold text-gray-800">
                  {action.title}
                </h2>
              </div>
              <p className="text-sm text-gray-600">{action.description}</p>
            </div>
          ))}
        </div>
      </div>

      <div>
        <div className="mt-20 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {setupActions.map((action, index) => (
            <div
              key={index}
              onClick={() => navigate(action.link)}
              className="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm hover:shadow-md cursor-pointer transition duration-200"
            >
              <div className="flex items-center gap-3 mb-3">
                {action.icon}
                <h2 className="text-lg font-semibold text-gray-800">
                  {action.title}
                </h2>
              </div>
              <p className="text-sm text-gray-600">{action.description}</p>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};

export default AdminDashboard;
