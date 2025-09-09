import React from "react";
import { useNavigate } from "react-router-dom";
import img from "../../imgs/judo-logo.png";
import { useAuth } from "../context/AuthContext";
import { Button } from "@mui/material";

const Header = ({ variant = "default" }) => {
  const navigate = useNavigate();
  const { user } = useAuth();

  let firstName = "";
  let lastName = "";
  let initials = "";

  const getInitials = (firstName, lastName) => {
    const firstInitial = firstName?.charAt(0)?.toUpperCase() || "";
    const lastInitial = lastName?.charAt(0)?.toUpperCase() || "";
    return firstInitial + lastInitial;
  };

  if (variant === "dashboard") {
    firstName = user.firstName || "";
    lastName = user.lastName || "";
    initials = getInitials(firstName, lastName);
  }

  return (
    <header
      className={`flex items-center justify-between py-3 px-3 ${
        variant === "dashboard" ? "sm:px-7" : "sm:px-35"
      } bg-white shadow-md `}
    >
      <div
        onClick={() =>
          variant === "dashboard" ? navigate("/dashboard") : navigate("/")
        }
        className="flex items-center space-x-2 hover:cursor-pointer"
      >
        <img
          src={img}
          alt="Logo"
          className="w-10 h-10 sm:w-15 sm:h-15 rounded-full object-cover"
        />
        <span className="text-xl sm:text-[28px] font-bold">
          <span className="text-black">Dojo</span>
          <span className="text-judo-blue">Log</span>
        </span>
      </div>

      {variant === "dashboard" ? (
        <div
          onClick={() => {
            if (!user?.demo) {
              navigate("/change-password");
            }
          }}
          className={`flex items-center space-x-2 ${
            user?.demo
              ? "cursor-default"
              : "hover:cursor-pointer"
          }`}
        >
          <div className="flex items-center justify-center w-9 h-9 sm:w-13 sm:h-13 rounded-full bg-judo-blue text-white text-sm sm:text-base font-bold">
            <div className="text-[14px] sm:text-xl">{initials}</div>
          </div>
        </div>
      ) : (
        <Button
          onClick={() => navigate("/demo-selection")}
          variant="contained"
          color="error"
          size="sm"
          className="w-auto button-hover"
        >
          Vyzkoušet demo
        </Button>
      )}
    </header>
  );
};

export default Header;
