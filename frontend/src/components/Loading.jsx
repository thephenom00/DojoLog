import React from "react";
import { Box, Paper, CircularProgress, Typography } from "@mui/material";

const Loading = () => (
  <Box display="flex" justifyContent="center" alignItems="center" height="300px">
    <Paper elevation={1} sx={{ padding: 4, textAlign: "center" }}>
      <CircularProgress />
      <Typography variant="h6" sx={{ marginTop: 2 }}>
        {"Načítání..."}
      </Typography>
    </Paper>
  </Box>
);

export default Loading;
