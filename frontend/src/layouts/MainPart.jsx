import { Box, Toolbar } from "@mui/material";
import React from "react";

function MainPart(props) {
  return (
    <Box component="main" width={'100%'}>
      <Toolbar variant="regular" component="section" />
      <Box padding={6} pt={3} component="section">
        { props.children }
      </Box>
    </Box>
  );
}

export default MainPart;