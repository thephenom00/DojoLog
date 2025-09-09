import { ChevronRight, Calendar, ClipboardList, UserPlus } from "lucide-react";
import React from "react";
import { Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import img from "../../imgs/logo-better.jpg";
import Header from "../components/Header";

const LandingPage = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <section className="pt-16 pb-16 md:pt-20 md:pb-24 px-4">
        <div className="container mx-auto max-w-6xl">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
            <div className="space-y-6 md:space-y-8 animate-slide-up">
              <div>
                <span className="inline-block py-1 px-3 rounded-full text-xs font-medium bg-blue-100 text-judo-blue mb-4">
                  Jednoduché sledování docházky
                </span>
                <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold tracking-tight text-gray-900">
                  Platforma, která usnadňuje{" "}
                  <span className="text-judo-blue">evidenci</span> docházky
                </h1>
              </div>
              <p className="text-lg text-gray-600 md:pr-10">
                Chytrá platforma, která usnadní život trenérům i rodičům.
                Sledujte docházku online a bez zbytečného papírování.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 mb-4">
                <Button
                  variant="contained"
                  onClick={() => navigate("/register")}
                  size="large"
                  className="w-[210px] sm:w-auto button-hover"
                  sx={{
                    backgroundColor: "#318CE7",
                  }}
                >
                  Vyzkoušet nyní
                  <ChevronRight size={16} className="ml-1" />
                </Button>
                <Button
                  onClick={() => navigate("/login")}
                  variant="outlined"
                  size="large"
                  className="w-[160px] sm:w-auto button-hover"
                >
                  Přihlásit se
                </Button>
              </div>
            </div>

            {/* Hero Image */}
            <div className="hidden md:flex justify-end items-center w-full p-6">
              <div className="rounded-2xl animate-scale-in">
                <div className="relative w-100 h-100 overflow-hidden rounded-2xl">
                  <img
                    src={img}
                    className="absolute inset-0 w-full h-full object-cover"
                    alt="Hero Logo"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 md:py-10 px-4 bg-gradient-to-b from-white to-gray-100">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12 md:mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Klíčové prvky
            </h2>
            <p className="text-gray-600 max-w-2xl mx-auto">
              Vše, co potřebujete k efektivní správě tréninků juda.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">
            {[
              {
                icon: <UserPlus className="h-10 w-10 text-judo-blue" />,
                title: "Snadná registrace",
                description:
                  "Zaregistrujte své dítě během pár minut – vše online, bez zbytečného papírování.",
              },
              {
                icon: <Calendar className="h-10 w-10 text-judo-blue" />,
                title: "Snadné přihlašování na tréninky",
                description:
                  "Vyberte si vyhovující tréninky a přihlaste své dítě na konkrétní dny přímo v aplikaci.",
              },
              {
                icon: <ClipboardList className="h-10 w-10 text-judo-blue" />,
                title: "Podrobnosti o trénincích a trenérech",
                description: "Mějte přehled o náplni tréninku a kdo ho povede.",
              },
            ].map((feature, i) => (
              <div
                key={i}
                className="glass-card p-6 flex flex-col items-center text-center animate-slide-up"
                style={{ animationDelay: `${i * 100}ms` }}
              >
                <div className="mb-4">{feature.icon}</div>
                <h3 className="text-xl font-semibold mb-2">{feature.title}</h3>
                <p className="text-gray-600">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 md:py-20 px-4">
        <div className="container mx-auto max-w-5xl">
          <div className="glass-card bg-gray-100 p-8 md:p-12 rounded-2xl text-center">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Staň se jedním z nás!
            </h2>
            <p className="text-gray-600 max-w-2xl mx-auto mb-8">
              Přidejte se k nám a vydejte se na cestu juda - umění, kde jemnost
              vítězí nad silou.
            </p>
            <div className="flex flex-col sm:flex-row justify-center gap-4">
              <Button
                onClick={() => navigate("/register")}
                size="large"
                variant="outlined"
                className="w-full sm:w-auto button-hover"
                sx={{
                  backgroundColor: "#318CE7",
                  color: "white",
                }}
              >
                Zaregistrovat
              </Button>
              <Button
                onClick={() => navigate("/login")}
                size="large"
                variant="outlined"
                className="w-full sm:w-auto button-hover"
              >
                Přihlásit se
              </Button>
            </div>
          </div>
        </div>
      </section>

      <footer className="py-8 border-t border-gray-300 mt-auto">
        <div className="container mx-auto max-w-6xl px-4">
          <div className="flex flex-col items-center justify-center text-center">
            <div className="text-sm text-gray-600">
              © 2025 David Le. Made with ❤️‍🔥.
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;
