import React from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import { getDayName, formatTime } from "../../utils/trainingUtils";

const ReportTable = ({ trainerReport }) => {
  const getCzechWord = (type) => {
    switch (type) {
      case "Training":
        return "Trénink";
      case "Substitution":
        return "Záskok";
      default:
        return null;
    }
  };

  return (
    <div className="w-[350px] sm:w-[850px]">
      <TableContainer
        component={Paper}
        sx={{
          borderRadius: "12px",
          overflow: "hidden",
          mt: "30px",
          overflowX: "auto",
        }}
      >
        <Table size="small">
          <TableHead sx={{ backgroundColor: "#f9fafb" }}>
            <TableRow>
              <TableCell sx={headerStyle}>Datum</TableCell>
              <TableCell sx={headerStyle}>Název</TableCell>
              <TableCell sx={headerStyle}>Den</TableCell>
              <TableCell sx={headerStyle}>Čas</TableCell>
              <TableCell sx={headerStyle}>Počet hodin</TableCell>
              <TableCell sx={headerStyle}>Typ</TableCell>
              <TableCell sx={headerStyle}>Sazba</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {[...trainerReport]
              .sort((a, b) => {
                const parseDate = (dateStr) => {
                  if (dateStr.includes("-")) {
                    const endPart = dateStr.trim().split("-")[1].trim();
                    const [day, month] = endPart.split(".");
                    return new Date(new Date().getFullYear(), parseInt(month) - 1, parseInt(day));
                  }
                  const [day, month, year] = dateStr.split(".");
                  return new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
                };
                return parseDate(a.date) - parseDate(b.date);
              })
              .map((row, index) => (
                <TableRow key={index}>
                  <TableCell sx={cellStyle}>
                    <div className="flex items-center gap-2 whitespace-nowrap">
                      {/* <CalendarDays className="text-gray-500 w-4 h-4" /> */}
                      {row.date}
                    </div>
                  </TableCell>
                  <TableCell sx={cellStyle}>
                    <div className="flex items-center gap-2 whitespace-nowrap">
                      {/* <MapPin className="text-gray-500 w-4 h-4" /> */}
                      {row.name}
                    </div>
                  </TableCell>
                  <TableCell sx={cellStyle}>
                    {getDayName(row.dayOfWeek)}
                  </TableCell>
                  <TableCell sx={cellStyle}>
                    {row.hours > 0 && (
                      <div className="flex items-center gap-2 whitespace-nowrap">
                        {/* <Clock className="text-gray-500 w-4 h-4" /> */}
                        {formatTime(row.startTime)} - {formatTime(row.endTime)}
                      </div>
                    )}
                  </TableCell>
                  <TableCell sx={cellStyle}>
                    {row.hours > 0 ? `${row.hours} h` : ""}
                  </TableCell>
                  <TableCell sx={cellStyle}>
                    <div className="flex items-center gap-2 whitespace-nowrap">
                      {getCzechWord(row.type)
                        ? getCzechWord(row.type)
                        : row.type}
                    </div>
                  </TableCell>
                  <TableCell sx={cellStyle}>
                    <div className="flex items-center gap-2 whitespace-nowrap">
                      {/* <DollarSign className="text-gray-500 w-4 h-4" /> */}
                      {row.money} Kč
                    </div>
                  </TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

const headerStyle = {
  padding: "12px",
  fontSize: "17px",
  color: "#374151",
  fontWeight: "bold",
};

const cellStyle = {
  padding: "14px",
  fontSize: "15px",
};

export default ReportTable;
