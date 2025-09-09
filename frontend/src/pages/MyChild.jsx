import React, { useEffect, useState } from "react";
import { Baby } from "lucide-react";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";
import { ApiService } from "../api/api";
import { useAuth } from "../context/AuthContext";
import Loading from "../components/Loading";
import { Dialog, DialogContent, DialogActions, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
} from "@mui/material";
import BackToDashboard from "../components/BackToDashboard";
import { mockData } from "../utils/mockData";

const MyChild = () => {
  const { user } = useAuth();
  const email = user.email;

  const [loading, setLoading] = useState(false);
  const [children, setChildren] = useState([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [attendanceData, setAttendanceData] = useState([]);

  useEffect(() => {
    if (!email) return;
    if (!user?.demo) {
      const fetchChildren = async () => {
        try {
          setLoading(true);
          const children = await ApiService.getParentChildren(email);
          setChildren(children);
        } catch (err) {
          console.error("Failed to fetch parents children", err);
        } finally {
          setLoading(false);
        }
      };

      fetchChildren();
    } else {
      setLoading(true)
      setTimeout(() => {
        setLoading(false)
      }, "500")

      const children = JSON.parse(localStorage.getItem("children"));
      if (children) {
        setChildren(children);
      }
    }
  }, [email]);

  const handleOpenDialog = async (childId) => {
    let data;
    if (!user?.demo) {
      data = await ApiService.getChildAttencanceDetail(childId);
    } else {
      const children = JSON.parse(localStorage.getItem("children"));
      if (children) {
        const child = children.find((c) => c.id === childId);
        const trainingId = child.requestedTrainingId;

        const schoolName = trainingId === 1 ? "2. ZŠ Plzeň" : "33. ZŠ Plzeň"

        data = mockData.pastTrainings
        .filter((training) => training.schoolName === schoolName)
        .map((training) => ({
          ...training,
          school: training.schoolName,
        }));
      }
    }

    const sortedAttendance = [...data].sort((a, b) => {
      const [dayA, monthA, yearA] = a.date.split(".").map(Number);
      const [dayB, monthB, yearB] = b.date.split(".").map(Number);
      const dateA = new Date(yearA, monthA - 1, dayA);
      const dateB = new Date(yearB, monthB - 1, dayB);
      return dateA - dateB;
    });

    setAttendanceData(sortedAttendance);
    setOpenDialog(true);
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-grow flex flex-col">
        <Header variant="dashboard" />
        <BackToDashboard />

        <main className="flex-grow px-4 sm:px-8 py-8 bg-gray-50 flex flex-col items-center">
          {loading ? (
            <Loading />
          ) : (
            <div className="w-full max-w-2xl space-y-6 mt-[20px]">
              {children.length > 0 ? (
                children.map((child) => (
                  <div
                    key={child.id}
                    className="bg-white rounded-2xl shadow-md p-2 flex items-center gap-4"
                  >
                    <div className="flex items-center justify-center bg-blue-100 text-judo-blue rounded-full w-14 h-14 shadow-sm">
                      <Baby className="w-8 h-8" />
                    </div>
                    <div className="flex-1">
                      <h2 className="text-md text-gray-800">
                        {child.firstName} {child.lastName}
                      </h2>
                    </div>
                    <div
                      className="bg-judo-blue hover:bg-blue-700 hover:cursor-pointer text-white px-4 py-2 text-sm rounded-xl font-medium transition-colors text-center"
                      onClick={() => handleOpenDialog(child.id)}
                    >
                      Zobrazit docházku
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-gray-600 text-center">
                  Nemáte zaregistrované žádné dítě.
                </p>
              )}
            </div>
          )}
        </main>

        <Dialog
          open={openDialog}
          onClose={() => setOpenDialog(false)}
          maxWidth="sm"
          fullWidth
        >
          <DialogContent dividers>
            {attendanceData.length === 0 ? (
              <p
                style={{ textAlign: "center", padding: "20px", color: "#666" }}
              >
                Dítě zatím neabsolvovalo žádný trénink.
              </p>
            ) : (
              <TableContainer
                component={Paper}
                elevation={2}
                sx={{
                  borderRadius: 3,
                  overflow: "hidden",
                }}
              >
                <Table size="small">
                  <TableHead>
                    <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                      <TableCell sx={{ fontWeight: "bold" }}>Datum</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Škola</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Popis</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {attendanceData.map((record, idx) => (
                      <TableRow key={idx}>
                        <TableCell>{new Date(record.date).toLocaleDateString("de-DE")}</TableCell>
                        <TableCell>{record.school}</TableCell>
                        <TableCell>
                          <div
                            style={{
                              maxHeight: 100,
                              overflowY: "auto",
                              whiteSpace: "pre-wrap",
                            }}
                          >
                            {record.description}
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </DialogContent>

          <DialogActions>
            <Button
              onClick={() => {
                setOpenDialog(false);
              }}
            >
              Zavřít
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    </div>
  );
};

export default MyChild;
