import React from "react";
import { useNavigate } from "react-router-dom";
import { Card, CardContent, Typography, CardActionArea } from "@mui/material";
import { ClipboardPen, CircleUser } from "lucide-react";
import Header from "../components/Header";
import { useAuth } from "../context/AuthContext";
import { mockData } from "../utils/mockData";

const DemoSelection = () => {
  const navigate = useNavigate();
  const { setUser } = useAuth();

  const handleSelect = (role) => {
    const token = role === "ROLE_PARENT" ? "DEMO_PARENT" : "DEMO_TRAINER";
    const user = role === "ROLE_PARENT" ? mockData.parent : mockData.trainer;
    localStorage.setItem("access_token", token)
    setUser(user)
    navigate("/dashboard");
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <Header />

      <main className="flex-grow flex flex-col items-center px-4 pb-12 mt-20">
        <div className="mb-3">
          <Typography variant="h4" fontWeight={600}>
            Vyzkoušej naši aplikaci v DEMO režimu
          </Typography>
        </div>

        <Typography
          variant="body1"
          sx={{
            color: "text.secondary",
            maxWidth: 500,
            textAlign: "center",
            mb: 4,
          }}
        >
          Vyber si roli a zkus si, co všechno naše aplikace umí. V demo režimu
          si můžeš vše vyzkoušet bez registrace.
        </Typography>

        <div className="flex flex-col sm:flex-row gap-6 items-center justify-center">
          {/* Trainer Card */}
          <Card
            onClick={() => handleSelect("ROLE_TRAINER")}
            sx={{ width: 280 }}
          >
            <CardActionArea>
              <CardContent>
                <div className="flex justify-center items-center mb-2">
                  <ClipboardPen className="w-25 h-25 text-judo-blue" />
                </div>
                <div className="mt-3 flex justify-center items-center mb-2">
                  <Typography gutterBottom variant="h5" component="div">
                    Trenér
                  </Typography>
                </div>

                <Typography variant="body2" sx={{ color: "text.secondary" }}>
                  Evidování docházky dětí, vyplnění obsahu tréninku a přístup k
                  měsíčnímu výkazu.
                </Typography>
              </CardContent>
            </CardActionArea>
          </Card>

          {/* Parent Card */}
          <Card onClick={() => handleSelect("ROLE_PARENT")} sx={{ width: 280 }}>
            <CardActionArea>
              <CardContent>
                <div className="flex justify-center items-center mb-2">
                  <CircleUser className="w-25 h-25 text-judo-blue" />
                </div>
                <div className="flex mt-3 justify-center items-center mb-2">
                  <Typography gutterBottom variant="h5" component="div">
                    Rodič
                  </Typography>
                </div>

                <Typography variant="body2" sx={{ color: "text.secondary" }}>
                  Registrace dítěte na tréninky nebo akce a zobrazit jeho
                  docházku na tréninky.
                </Typography>
              </CardContent>
            </CardActionArea>
          </Card>
        </div>
      </main>
    </div>
  );
};

export default DemoSelection;
