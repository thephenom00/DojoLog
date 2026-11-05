import { React, useEffect, useState } from "react";
import Sidebar from "../components/Sidebar.jsx";
import Header from "../components/Header.jsx";
import NewsCard from "../components/NewsCard.jsx";
import { ApiService } from "../api/api.js";
import Loading from "../components/Loading.jsx";
import BackToDashboard from "../components/BackToDashboard.jsx";
import { useAuth } from "../context/AuthContext.jsx";
import { mockData } from "../utils/mockData.js";

const News = () => {
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();

  useEffect(() => {
    if (!user?.demo) {
      const fetchNews = async () => {
        try {
          setLoading(true);
          const news = await ApiService.getNews();

          setNews(news);
        } catch (err) {
          console.error("Failed to fetch trainings", err);
        } finally {
          setLoading(false);
        }
      };

      fetchNews();
    } else {
      setLoading(true);
      setTimeout(() => {
        setLoading(false);
        }, "500");
      setNews(mockData.news);
    }
  }, []);

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard />

        <main className="flex-grow p-8 bg-gray-50 flex flex-col gap-6 items-center">
          {loading ? <Loading /> : <NewsCard news={news} />}
        </main>
      </div>
    </div>
  );
};

export default News;
