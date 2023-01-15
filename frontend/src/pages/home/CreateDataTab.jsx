import { yupResolver } from '@hookform/resolvers/yup';
import Editor from '@monaco-editor/react';
import { HelpOutline } from '@mui/icons-material';
import AddIcon from '@mui/icons-material/Add';
import { LoadingButton } from '@mui/lab';
import { Box, FormControl, FormControlLabel, FormHelperText, FormLabel, IconButton, InputLabel, MenuItem, Radio, RadioGroup, TextField, Tooltip } from "@mui/material";
import { Stack } from '@mui/system';
import ResultAlert from 'components/ResultAlert';
import { Locales } from 'constants/index';
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { checkPath } from "utils/CommonUtil";
import * as yup from "yup";


const CREATE_OBJECT_API = "/api/datafaker/create-object"
const CREATE_ARRAY_API = "/api/datafaker/create-array"

const DEFAULT_EXPRESSION_VALUE = "{\n\t\n}"

const schema = yup.object({
  path: yup.string().test("is-path", (value) => checkPath(value),).required(),
  name: yup.string().required(),
  intro: yup.string(),
  locale: yup.string().required()
}).required()

const openExpressionHelp = () => {
  window.open("https://www.datafaker.net/documentation/expressions/", "_blank")
}

export default function CreateDataTab() {

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

  const { register, handleSubmit, formState } = useForm({
    resolver: yupResolver(schema)
  });

  const [expressionValue, setExpressionValue] = useState(DEFAULT_EXPRESSION_VALUE)
  const [expreesionError, setExpreesionError] = useState(false)

  const handleEditorDidMount = (editor, monaco) => {
    // setExpressionValue("")
  }

  const handleEditorChange = (value, event) => {
    try {
      JSON.parse(value)
      setExpreesionError(false)
    } catch (error) {
      setExpreesionError(true)
    }
    setExpressionValue(value)
  }

  const [creating, setCreating] = useState(false)

  const onSubmit = data => {
    try {
      JSON.parse(expressionValue)
    } catch (error) {
      setExpreesionError(true)
      return
    }

    data = { ...data, expression: JSON.parse(expressionValue) }

    const url = data.type === "array" ? CREATE_ARRAY_API : CREATE_OBJECT_API

    setCreating(true)
    fetch(url, {
      method: "POST",
      body: JSON.stringify(data)
    }).then(res => {
      if (res.ok) {
        setSnackbarData({
          open: true,
          severity: "success",
          message: `${data.path} created successfully`
        })
      } else {
        throw res
      }
    }).catch(error => {
      if (error instanceof Response) {
        error.json()
          .then(data => {
            setSnackbarData({
              open: true,
              severity: "warning",
              message: data.message
            })
          }).catch(error => {
            console.error("Create failed.", error)
            setSnackbarData({
              open: true,
              severity: "warning",
              message: `Create ${data.path} failed`
            })
          })
      } else {
        console.error("Create failed.", error)
        setSnackbarData({
          open: true,
          severity: "warning",
          message: `Create ${data.path} failed`
        })
      }
    }).finally(() => {
      setCreating(false)
    })
  }

  return (
    <Box component="article">
      <Box component="form"
        noValidate
        autoComplete="off"
        display="flex"
        flexDirection="column"
        onSubmit={ handleSubmit(onSubmit) }
        sx={ {
          "& .MuiTextField-root": { m: 1 }
        } }
      >
        <TextField
          variant='standard'
          label="Path"
          required
          sx={ { my: 1, width: "30ch" } }
          error={ formState.errors?.path ? true : false }
          // @ts-ignore
          helperText={ formState.errors?.path?.message ?? "pattern is /api/*" }
          { ...register("path") }
        />

        <TextField
          variant='standard'
          label="Name"
          required
          sx={ { width: "30ch" } }
          { ...register("name") }
        />

        <TextField
          variant='standard'
          label="Intro"
          sx={ { width: "50ch" } }
          { ...register("intro") }
        />

        <Box sx={ { m: 1, width: "65ch" } }>
          <Stack direction="row" alignItems="center">
            <InputLabel required sx={ { width: "max-content" } }>
              Expression
            </InputLabel>
            <Tooltip title="Open expression document">
              <IconButton onClick={openExpressionHelp}>
                <HelpOutline />
              </IconButton>
            </Tooltip>
          </Stack>
          
          <Box py={ 1 }>
            <Editor
              height="20ch"
              defaultLanguage="json"
              defaultValue={ DEFAULT_EXPRESSION_VALUE }
              onMount={ handleEditorDidMount }
              onChange={ handleEditorChange }
              options={ {
                lineNumbers: "off",
                minimap: {
                  enabled: false
                }
              } }
            ></Editor>
          </Box>
          {
            expreesionError && <FormHelperText error={ true }>expression is invalid</FormHelperText>
          }
        </Box>

        <FormControl sx={ { m: 1 } }>
          <FormLabel>Type</FormLabel>
          <RadioGroup row defaultValue="object" { ...register("type") }>
            <FormControlLabel value="object" control={ <Radio /> } label="Object" />
            <FormControlLabel value="arrar" control={ <Radio /> } label="Array" />
          </RadioGroup>
        </FormControl>

        <TextField select label="locale" defaultValue="zh_CN" variant='standard'
          sx={ { width: "15ch" } }
          { ...register("locale") }
        >
          {
            Locales.map((locale) => (
              <MenuItem key={ locale.value } value={ locale.value }>{ locale.lable }</MenuItem>
            ))
          }
        </TextField>

        <Box sx={ { mx: 1, mt: 2 } }>
          <LoadingButton loading={ creating } variant="contained" type="submit" endIcon={ <AddIcon /> }>Create</LoadingButton>
        </Box>
      </Box>

      <ResultAlert
        open={ snackbarData.open }
        message={ snackbarData.message }
        severity={ snackbarData.severity }
        onClose={ handleCloseSnackbar }
      />
    </Box>
  )
}