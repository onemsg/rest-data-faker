import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';
import SendIcon from '@mui/icons-material/Send';
import { Box, Button, Chip, Dialog, Link, Tooltip, Typography } from "@mui/material";
import { GridActionsCellItem, GridToolbarContainer } from "@mui/x-data-grid";
import { DataGrid } from "@mui/x-data-grid/DataGrid";
import HLCode from "components/HLCode";
import React, { useEffect, useState } from "react";
import { formatTimeAgo } from "utils/CommonUtil";


import { Locales } from "constants/index";
import ResultAlert from 'components/ResultAlert';

const DEFAULT_PAGESIZE = 10

const JsonDialog = ({ title, value, open, onClose }) => {
  const handleClose = () => {
    onClose()
  }
  return (
    <Dialog onClose={ handleClose } open={ open }>
      <Typography variant="subtitle1" px={ 1 } pt={ 1 } color="grey">
        {title}
      </Typography>
      <Box px={ 1 }>
        <HLCode code={ JSON.stringify(value, null, 4) } />
      </Box>
    </Dialog>
  )
}


const DataToolbar = (props) => {
  const { handleReload } = props
  return (
    <GridToolbarContainer>
      <Button onClick={ handleReload } startIcon={ <RefreshIcon /> }>Refresh</Button>
    </GridToolbarContainer>
  )
}

const getLocaleLable = (locale) => {
  return Locales.find(v => v.value === locale).lable
}

export default function ListDataTab() {

  const [dialog, setDialog] = useState({
    title: "",
    value: "",
    open: false
  })

  const showExpression = (path, value) => {
    setDialog({
      title: `Expression of ${path}`,
      value: value,
      open: true
    })
  }

  const showRequestResult = (path, value) => {
    setDialog({
      title: `Request ${path} result`,
      value: value,
      open: true
    })
  }

  const closeDialog = () => {
    setDialog({...dialog, open: false})
  }

  const [snackbarData, setSnackbarData] = useState({
    open: false,
    severity: "info",
    message: ""
  })

  const handleCloseSnackbar = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarData({ ...snackbarData, open: false });
  }

  const handleRequest = (path) => {
    fetch(path)
      .then(res => {
        if (res.ok) {
          return res.json()
        } else {
          throw res
        }
      }).then(data => {
        showRequestResult(path, data)
      }).catch(error => {
        setSnackbarData({
          open: true,
          severity: "warning",
          message: `Request ${path} failed`
        });
        console.log(`Request ${path} failed`, error)
      }).finally(() => {
      })
  }

  const handleRemove = (id, path) => {
    const api = "/api/datafaker/remove?id=" + id
    fetch(api, {method: "delete"})
      .then(res => {
        if (res.ok) {
          setSnackbarData({
            open: true,
            severity: "info",
            message: `Remove ${path} successfully`
          })
          fetchData()
        } else {
          throw res
        }
      }).catch(error => {
        setSnackbarData({
          open: true,
          severity: "warning",
          message: `Remove ${path} failed`
        });
        console.log(`Remove ${path} failed`, error)
      }).finally(() => {
      })
  }

  /** 列定义 */
  const COLUMN_SCHEMA = [
    {
      type: "number",
      field: "id",
      headerName: "ID",
      maxWidth: 50,
      sortable: false
    },
    {
      field: "path",
      headerName: "Path",
      minWidth: 120,
      flex: 1,
      renderCell: (params) => {
        return (
          <Link href={ params.value } underline="none" target="_blank">
            { params.value }
          </Link>
        )
      }
    },
    {
      field: "name",
      headerName: "Name",
      minWidth: 120,
      flex: 1,
      sortable: false,
      renderCell: (params) => (
        <Tooltip title={ params.row.intro }>
          <Box sx={ { textDecoration: "underline dotted" } }>{ params.value }</Box>
        </Tooltip>
      )
    },
    {
      field: "expression",
      headerName: "Expression",
      maxWidth: 100,
      renderCell: (params) => (
        <Button variant="text" onClick={ () => showExpression(params.row.path, params.value) }>详情</Button>
      ),
      sortable: false
    },
    {
      field: "type",
      headerName: "Type",
      maxWidth: 100,
      renderCell: (params) => (
        <Chip size="small" label={ params.value } color={ params.value === "OBJECT" ? "info" : "warning" } />
      ),
      sortable: false
    },
    {
      field: "locale",
      headerName: "Locale",
      maxWidth: 80,
      renderCell: (params) => (
        <Chip size="small" label={ getLocaleLable(params.value) } />
      ),
      sortable: false
    },
    {
      field: "createdTime",
      headerName: "Created Time",
      minWidth: 120,
      valueFormatter: (params) => {
        if (params.value == null) return ""
        return formatTimeAgo(params.value)
      },
      sortable: false
    },
    {
      field: "actions",
      type: "actions",
      maxWidth: 80,
      getActions: (params) => [
        <Tooltip title="Request this api" key="1">
          <GridActionsCellItem icon={ <SendIcon /> } label="Request"  
            onClick={ () => handleRequest(params.row.path) }
          />
        </Tooltip>,
        <Tooltip title="Remove this api" key="2">
          <GridActionsCellItem icon={ <DeleteIcon /> } label="Delete" 
            onClick={() => handleRemove(params.row.id, params.row.path)}
          />
        </Tooltip>
      ]
    }
  ]

  const [isLoaded, setIsLoaded] = useState(true);
  const [rows, setRows] = useState([]);

  const fetchData = () => {
    setIsLoaded(true)
    fetch("/api/datafaker/list")
      .then(res => {
        if (res.ok) {
          return res.json()
        } else {
          throw res
        }
      }).then(data => {
        setRows(data)
      }).catch(error => {
        console.error("fetch data error", error)
        setRows([])
      }).finally(() => {
        setIsLoaded(true)
      })
  }

  useEffect(() => {
    fetchData()
  }, [])

  return (
    <Box component="article">
      <DataGrid
        autoHeight
        loading={ !isLoaded }
        rows={ rows }
        columns={ COLUMN_SCHEMA }
        pageSize={ DEFAULT_PAGESIZE }
        getRowId={ row => row.id }
        disableSelectionOnClick
        disableColumnFilter
        disableColumnMenu
        sx={
          {
            border: 0,
            "& .MuiDataGrid-cell:focus-within": {
              outline: "none"
            },
            "& .MuiDataGrid-columnHeader:focus-within": {
              outline: "none"
            }
          }
        }
        components={ {
          Toolbar: DataToolbar
        } }
        componentsProps={ {
          toolbar: {
            handleReload: fetchData
          }
        } }
      />
      <JsonDialog
        title={dialog.title}
        value={ dialog.value }
        open={ dialog.open }
        onClose={ closeDialog }
      />
      <ResultAlert
        open={ snackbarData.open }
        message={ snackbarData.message }
        severity={ snackbarData.severity }
        onClose={ handleCloseSnackbar }
      />
    </Box>
  )
}