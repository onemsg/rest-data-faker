import { Box, TextField } from "@mui/material";
import React from "react";

export default function RequestDataTab() {
  return (
    <Box>
      <Box component="form"
        noValidate
        autoComplete="off"
      >
        <TextField label="Path" />
      </Box>
    </Box>
  )
}