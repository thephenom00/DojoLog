import React, { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import API_CONFIG from "../config/api.config";
import { mockData } from "../utils/mockData";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("access_token");
    if (token) {
      if (token === "DEMO_TRAINER") {
        setUser(mockData.trainer)
      } else if (token === "DEMO_PARENT") {
        setUser(mockData.parent)
      } else {
        try {
          const decoded = jwtDecode(token);
          setUser({
            email: decoded.sub,
            role: decoded.role,
            firstName: decoded.firstName,
            lastName: decoded.lastName,
          });
        } catch (error) {
          console.error("Invalid token:", error);
          setUser(null);
        }
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    localStorage.clear()
    const response = await fetch(`${API_CONFIG.BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) throw new Error("Invalid credentials");

    const data = await response.json();
    const { accessToken, refreshToken } = data;

    localStorage.setItem("access_token", accessToken);
    localStorage.setItem("refresh_token", refreshToken);

    const decoded = jwtDecode(accessToken);
    setUser({
      email: decoded.sub,
      role: decoded.role,
      firstName: decoded.firstName,
      lastName: decoded.lastName,
    });

    return data;
  };

  const logout = () => {
    setUser(null);
    localStorage.clear();
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, setUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
