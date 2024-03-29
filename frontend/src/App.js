import { Box } from '@mui/material';
import Home from './pages/home';
import AppHeader from './layouts/AppHeader';
import MainPart from './layouts/MainPart';
import React from 'react';

function App() {
  return (
    <Box sx={ { display: 'flex' } }>
      <AppHeader />
      <MainPart>
        <Home />
      </MainPart>
    </Box>
  );
}

export default App;
