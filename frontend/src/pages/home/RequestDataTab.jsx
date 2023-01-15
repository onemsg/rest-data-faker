import SendIcon from '@mui/icons-material/Send';
import { LoadingButton } from '@mui/lab';
import { Box, Stack, TextField } from "@mui/material";
import ResultAlert from 'components/ResultAlert';
import React, { useState } from "react";
import { checkPath } from 'utils/CommonUtil';


export default function RequestDataTab({ onResultChange }) {

  const [path, setPath] = useState("")
  const [errors, setErrors] = useState({ path: { message: null, error: false } })
  const [loading, setLoading] = useState(false)

  const [snackbarData, setSnackbarData] = useState({
    open: false,
    severity: "warning",
    message: ""
  })

  const handleCloseSnackbar = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarData({ ...snackbarData, open: false });
  }

  const handlePathChange = (event) => {
    setPath(event.target.value)
  }

  const handleRequest = () => {
    if (!checkPath(path)) {
      setErrors({
        path: {
          message: "path is invalid",
          error: true
        }
      })
      return
    } else {
      setErrors({ path: { message: null, error: false } })
    }

    setLoading(true)
    fetch(path)
      .then(res => {
        if (res.ok) {
          return res.json()
        } else {
          throw res
        }
      }).then(data => {
        onResultChange(data)
      }).catch(error => {
        onResultChange(null)
        if (error instanceof Response) {
          if (error.status === 404) {
            setSnackbarData({
              open: true,
              severity: "warning",
              message: `${path} not found`
            });
          } else {
            setSnackbarData({
              open: true,
              severity: "error",
              message: `Request ${path} ${error.status} ${error.statusText}`
            });
          }
        } else {
          console.log(error)
          setSnackbarData({
            open: true,
            severity: "error",
            message: "Something error"
          });
        }
      }).finally(() => {
        setLoading(false)
      })
  }

  return (
    <Box component="article">
      <Stack m={1} direction="row" alignItems="baseline" spacing={ 1 } component="section">
        <TextField
          label="Path"
          required
          variant="standard"
          sx={ { width: "30ch" } }
          onChange={ handlePathChange }
          error={ errors.path.error }
          helperText={ errors.path.message ?? "pattern is /api/*"  }
        />
        <LoadingButton
          loading={ loading }
          loadingPosition='end'
          variant="contained"
          endIcon={ <SendIcon /> }
          onClick={ handleRequest }>
          Send
        </LoadingButton>
      </Stack>

      <ResultAlert 
        open={ snackbarData.open } 
        message={ snackbarData.message }
        severity={ snackbarData.severity }
        onClose={ handleCloseSnackbar }
      />
    </Box>
  )
}