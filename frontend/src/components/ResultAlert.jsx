import { Alert, Snackbar } from "@mui/material";
import React from "react";

export default function ResultAlert({ open, message, severity, onClose }) {

  let alert = <Alert onClose={ onClose } >{ message }</Alert>

  if (severity === "warning") {
    alert = (<Alert onClose={ onClose } severity='warning'>{ message }</Alert>)
  } else if (severity === "error") {
    alert = (<Alert onClose={ onClose } severity='error'>{ message }</Alert>)
  }

  return (
    <Snackbar
      open={ open }
      autoHideDuration={ 6000 }
      onClose={ onClose }
    >
      { alert }
    </Snackbar>
  )
}