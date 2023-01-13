import { Box, Container, Paper, Tab } from "@mui/material";
import React, { useState } from "react";
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';
import ListDataCard from "./ListDataTab";
import CreateDataTab from "./CreateDataTab";
import RequestDataTab from "./RequestDataTab";

export default function Home() {

  const [value, setValue] = useState("1");

  function handleChange(event, newValue) {
    setValue(newValue)

  }

  return (
    <Container>
      <Paper sx={ {px: 2} } >
        <TabContext value={ value }>
          <Box sx={ { borderBottom: 1, borderColor: 'divider' } }>
            <TabList variant="fullWidth" onChange={ handleChange }>
              <Tab label="List" value="1" />
              <Tab label="Create" value="2" />
              <Tab label="Request" value="3" />
            </TabList>
          </Box>
          <TabPanel value="1">
            <ListDataCard />
          </TabPanel>
          <TabPanel value="2">
            <CreateDataTab />
          </TabPanel>
          <TabPanel value="3">
            <RequestDataTab />
          </TabPanel>
        </TabContext>
      </Paper>
    </Container>
  )
}