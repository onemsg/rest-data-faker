import { yupResolver } from '@hookform/resolvers/yup';
import Editor from '@monaco-editor/react';
import { Box, Button, FormHelperText, IconButton, InputLabel, MenuItem, Snackbar, TextField, Tooltip, Typography } from "@mui/material";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { checkPath } from "utils/CommonUtil";
import * as yup from "yup";
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import { CloseOutlined, HelpOutlineRounded } from '@mui/icons-material';

const DEFAULT_EXPRESSION_VALUE = "{\n\t\n}"

const Locales = [
  {
    value: "zh_CN",
    lable: "中文简体"
  },
  {
    value: "en",
    lable: "英语"
  },
  {
    value: "ko",
    lable: "韩语"
  },
  {
    value: "ja",
    lable: "日语"
  },
]

const schema = yup.object({
  path: yup.string().test("is-path", (value) => checkPath(value),).required(),
  name: yup.string().required(),
  intro: yup.string(),
  local: yup.string().required()
}).required()

export default function CreateDataTab() {

  const [snackbarOpen, setSnackbarOpen] = useState(false)

  const handleCloseSnackbar = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarOpen(false);
  }

  const SnackbarAction = (
    <React.Fragment>
      <Button color="secondary" size="small" onClick={ handleCloseSnackbar }>
        UNDO
      </Button>
      <IconButton
        size="small"
        aria-label="close"
        color="inherit"
        onClick={ handleCloseSnackbar }
      >
        <CloseOutlined fontSize="small" />
      </IconButton>
    </React.Fragment>
  );

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

  const onSubmit = data => {
    data = { ...data, expression: expressionValue }
    try {
      JSON.parse(expressionValue)
      console.log(data)
    } catch (error) {
      setExpreesionError(true)
    }
    // TODO
    setSnackbarOpen(true)
  }

  return (
    <Box>
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
          // @ts-ignore
          error={ formState.errors?.path ? true : false }
          // @ts-ignore
          helperText={ formState.errors?.path?.message }
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
        <TextField select label="语言" defaultValue="zh_CN" variant='standard'
          sx={ { my: 1, width: "15ch" } }
          { ...register("local") }
        >
          {
            Locales.map((local) => (
              <MenuItem key={ local.value } value={ local.value }>{ local.lable }</MenuItem>
            ))
          }
        </TextField>
        <Box sx={ { m: 1, width: "50ch" } }>
          <Tooltip title="JSON 表达式">
            <InputLabel required sx={ { width: "max-content"}}>
              Expression
            </InputLabel>
          </Tooltip>

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
        <Box sx={ { m: 1 } }>
          <Button variant="contained" type="submit" size="large">Create</Button>
        </Box>
      </Box>

      <Snackbar
        open={ snackbarOpen }
        autoHideDuration={ 6000 }
        onClose={ handleCloseSnackbar }
        message="创建成功"
        action={SnackbarAction}
      />
    </Box>
  )
}