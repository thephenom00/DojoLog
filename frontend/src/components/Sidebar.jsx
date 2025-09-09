import React, { useState } from "react";
import {
  Home,
  FileText,
  Calendar,
  ClipboardList,
  BarChart,
  LogOut,
  Menu,
  X,
  Newspaper,
  CheckSquare,
  Accessibility,
  School,
  UserPlus,
  CalendarPlus,
  CalendarDays,
  ChartLine,
} from "lucide-react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Sidebar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isOpen, setIsOpen] = useState(false);
  const { user, logout } = useAuth();

  const trainerLinks = [
    { name: "Přehled", path: "/dashboard", icon: Home },
    { name: "Aktuality", path: "/news", icon: Newspaper },
    { name: "Akce", path: "/events", icon: Calendar },
    { name: "Report", path: "/report", icon: BarChart },
  ];

  const parentLinks = [
    { name: "Přehled", path: "/dashboard", icon: Home },
    { name: "Registrovat na trénink", path: "/register-child", icon: FileText },
    { name: "Akce", path: "/events", icon: Calendar },
    { name: "Moje dítě", path: "/my-child", icon: ClipboardList },
  ];

  const adminLinks = [
    { name: "Přehled", path: "/dashboard", icon: Home },
    {
      name: "Přidat Záskok",
      path: "/admin/add-trainer-substitution",
      icon: Accessibility,
    },
    {
      name: "Schválit registraci",
      path: "/admin/approve-registration",
      icon: CheckSquare,
    },
    {
      name: "Publikovat aktualitu",
      path: "/admin/create-news",
      icon: Newspaper,
    },
    { name: "Akce", path: "/events", icon: CalendarDays },
    { name: "Výkazy", path: "/admin/generate-report", icon: ChartLine },
  ];

  const setUpLinks = [
    { name: "Vytvořit školu", path: "/admin/create-school", icon: School },
    {
      name: "Vytvořit trénink",
      path: "/admin/create-training",
      icon: CalendarPlus,
    },
    {
      name: "Zaregistrovat trenéra",
      path: "/admin/register-trainer",
      icon: UserPlus,
    },
    {
      name: "Přidat prázdniny",
      path: "/admin/add-holiday",
      icon: CalendarPlus,
    },
  ];

  function getLinks(role) {
    switch (role) {
      case "ROLE_TRAINER":
        return trainerLinks;
      case "ROLE_PARENT":
        return parentLinks;
      case "ROLE_ADMIN":
        return adminLinks;
      default:
        return [];
    }
  }

  const links = getLinks(user.role);

  return (
    <>
      <div className="fixed top-4 left-4 z-50 md:hidden">
        <button
          onClick={() => setIsOpen(true)}
          className="hover:cursor-pointer p-2 rounded-md bg-white shadow-md"
        >
          <Menu size={24} />
        </button>
      </div>

      {isOpen && (
        <div
          className="fixed inset-0 bg-black/33 z-40 md:hidden"
          onClick={() => setIsOpen(false)}
        />
      )}

      <aside
        className={`
            fixed top-0 w-70 md:w-[250px] flex-shrink-0 left-0 z-50 bg-white min-h-screen transition-transform duration-300 flex flex-col 
            ${isOpen ? "translate-x-0" : "-translate-x-full"} 
            md:translate-x-0 md:relative md:flex`}
      >
        <div className="flex items-center justify-end p-4 md:hidden">
          <button
            onClick={() => setIsOpen(false)}
            className="hover:cursor-pointer"
          >
            <X size={24} />
          </button>
        </div>

        <nav className="flex flex-col px-4 pt-4 flex-grow">
          {links.map((link) => {
            const isActive = location.pathname === link.path;
            return (
              <button
                key={link.name}
                onClick={() => {
                  navigate(link.path);
                  setIsOpen(false);
                }}
                className={`flex items-center p-3 rounded-lg transition-colors hover:cursor-pointer ${
                  isActive
                    ? "bg-[#eef5ff] text-[#0177c8]"
                    : "text-gray-800 hover:bg-gray-100"
                }`}
              >
                <link.icon size={20} />
                <span className="ml-3 font-medium">{link.name}</span>
              </button>
            );
          })}

          {user.role === "ROLE_ADMIN" && (
            <div className="mt-6 border-t border-gray-300 pt-4 ">
              {setUpLinks.map((link) => {
                const isActive = location.pathname === link.path;
                return (
                  <button
                    key={link.name}
                    onClick={() => {
                      navigate(link.path);
                      setIsOpen(false);
                    }}
                    className={`flex items-center p-3 rounded-lg transition-colors hover:cursor-pointer ${
                      isActive
                        ? "bg-[#eef5ff] text-[#0177c8]"
                        : "text-gray-800 hover:bg-gray-100"
                    }`}
                  >
                    <link.icon size={20} />
                    <span className="ml-3 font-medium">{link.name}</span>
                  </button>
                );
              })}
            </div>
          )}

          <div className="mt-6 border-t border-gray-300 pt-4 ">
            <button
              onClick={() => {
                logout();
                navigate("/");
                setIsOpen(false);
              }}
              className="w-full flex items-center p-3 text-gray-700 hover:bg-gray-100 rounded-lg hover:cursor-pointer"
            >
              <LogOut size={20} />
              <span className="ml-3 font-medium">Odhlásit se</span>
            </button>
          </div>
        </nav>
      </aside>
    </>
  );
};

export default Sidebar;
