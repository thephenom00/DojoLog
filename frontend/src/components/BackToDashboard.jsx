import React from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

const BackToDashboard = () => {
  const navigate = useNavigate();

  return (
    <div className="top-17 sm:top-25 px-5 absolute">
      <button
        onClick={() => navigate("/dashboard")}
        className="flex items-center text-judo-blue hover:underline text-[15px] hover:cursor-pointer"
      >
        <ArrowLeft className="w-4 h-4 mr-2" />
        Zpět na dashboard
      </button>
    </div>
  );
};

export default BackToDashboard;
