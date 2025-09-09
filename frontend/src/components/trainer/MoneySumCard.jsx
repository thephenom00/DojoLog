import React from "react";
import { Paper, Typography, Box } from "@mui/material";
import { DollarSign, } from "lucide-react";

const MoneySumCard = ({ moneySum }) => {
  return (
    <Paper
      elevation={2}
      sx={{
        borderRadius: "12px",
        padding: "20px",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        width: { xs: "350px", sm: "700px" },
        mt: "30px",
        backgroundColor: "#f9fafb",
      }}
    >
      <Box display="flex" alignItems="center" gap={2}>
        <DollarSign className="text-judo-blue" size={28} />
        <Box>
          <Typography
            variant="h5"
            sx={{ color: "#1e3a8a", fontWeight: "bold" }}
          >
            {moneySum} Kč
          </Typography>
          <Typography variant="body2" sx={{ color: "#6b7280" }}>
            Vydělaných peněz tento měsíc
          </Typography>
        </Box>
      </Box>
    </Paper>
  );
};

export default MoneySumCard;
