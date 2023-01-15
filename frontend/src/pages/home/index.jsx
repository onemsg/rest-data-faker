import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';
import { Box, Container, Paper, Tab } from "@mui/material";
import HLCode from "components/HLCode";
import React, { useState } from "react";
import { isEmpty } from "utils/CommonUtil";
import CreateDataTab from "./CreateDataTab";
import ListDataCard from "./ListDataTab";
import RequestDataTab from "./RequestDataTab";

export default function Home() {

  const [tab, setTab] = useState("1");

  function handleTabChange(event, newValue) {
    setTab(newValue)
  }

  const [requestResult, setRequestResult] = useState("")

  function handleRequestResult(newResult) {
    setRequestResult(newResult)
  }

  return (
    <Container>
      <Paper sx={ {px: 2} } >
        <TabContext value={ tab }>
          <Box sx={ { borderBottom: 1, borderColor: 'divider' } }>
            <TabList variant="fullWidth" onChange={ handleTabChange }>
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
            <RequestDataTab onResultChange={handleRequestResult}/>
          </TabPanel>
        </TabContext>
      </Paper>

      <Paper sx={ { mt: 4, display: ((tab === "3" && !isEmpty(requestResult)) ? "block" : "none") }} >
        <HLCode code={ JSON.stringify(requestResult, null, 4)}></HLCode>
      </Paper>
    </Container>
  )
}