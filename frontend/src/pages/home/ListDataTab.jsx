import { Box, Button, Chip, Dialog, DialogContent, DialogTitle, IconButton, Link, TableCell, TableRow, Tooltip, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid/DataGrid";
import React, { useEffect, useState } from "react";
import { formatTimeAgo } from "utils/CommonUtil";

import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneLight } from 'react-syntax-highlighter/dist/esm/styles/prism';

const HLCode = ({code}) => {
  return (
    <SyntaxHighlighter language="json" style={ oneLight }>
      { code }
    </SyntaxHighlighter>
  )
}

const testData0 = [
  {
    "id": 6,
    "path": "/api/people/list",
    "name": "所有个人信息",
    "intro": "所有个人信息接口",
    "expression": {
      "fullname": "#{Name.full_name}",
      "age": "#{number.number_between '15','50'}",
      "gender": "#{Gender.binaryTypes}",
      "address": {
        "city": "#{Address.city}",
        "street": "#{Address.streetAddress}",
        "zipCode": "#{Address.zipCode}"
      }
    },
    "type": "ARRAY",
    "createdTime": "2022-08-11T22:16:41.6263039"
  },
  {
    "id": 5,
    "path": "/api/people",
    "name": "个人信息",
    "intro": "个人信息接口",
    "expression": {
      "fullname": "#{Name.full_name}",
      "age": "#{number.number_between '15','50'}",
      "gender": "#{Gender.binaryTypes}",
      "address": {
        "city": "#{Address.city}",
        "street": "#{Address.streetAddress}",
        "zipCode": "#{Address.zipCode}"
      }
    },
    "type": "OBJECT",
    "createdTime": "2022-08-11T22:13:17.4111089"
  }
]

const testData = []
for (let i = 1; i < 50; i++) {
  let data = { ...testData0[i % 2], "id": i }
  testData.push(data)
}


const DEFAULT_PAGESIZE = 10

const ExpressionDialog = ({ onClose, value, open }) => {
  
  const handleClose = () => {
    onClose()
  }

  return (
    <Dialog onClose={handleClose} open={open}>
      <DialogTitle>Expression</DialogTitle>
      <DialogContent>
        <HLCode code={ JSON.stringify(value, null, 4) } />
      </DialogContent>
    </Dialog>
  )

}

export default function ListDataTab() {

  const [ dialogOpen, setDialogOpen ] = useState(false)
  const [ selectedExpression, setSelectedExpression ] = useState("")

  const openDialog = (value) => {
    setSelectedExpression(value)
    setDialogOpen(true)
  }

  const closeDialog = () => {
    setDialogOpen(false)
  }

  /** 列定义 */
  const COLUMN_SCHEMA = [
    {
      type: "number",
      field: "id",
      headerName: "ID",
      maxWidth: 70,
      sortable: false
    },
    {
      field: "path",
      headerName: "Path",
      minWidth: 150,
      flex: 1,
      renderCell: (params) => {
        return (
          <Link href={ params.value } underline="none">
            { params.value }
          </Link>
        )
      }
    },
    {
      field: "name",
      headerName: "Name",
      minWidth: 150,
      flex: 1,
      sortable: false,
      renderCell: (params) => (
        <Tooltip title={ params.row.intro }>
          <Box sx={ { textDecoration: "underline dotted" } }>{ params.value }</Box>
        </Tooltip>
      )
    },
    {
      field: "type",
      headerName: "Type",
      minWidth: 100,
      renderCell: (params) => (
        <Chip size="small" label={ params.value } color={ params.value === "OBJECT" ? "info" : "warning" } />
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
      field: "expression",
      headerName: "Expression",
      maxWidth: 100,
      renderCell: (params) => (
        <Button variant="text" onClick={ () => openDialog(params.value) }>详情</Button>
      )
    }
  ]

  const [isLoaded, setIsLoaded] = useState(true);
  const [rows, setRows] = useState([]);

  useEffect(() => {
    setRows(testData)
  }, [])

  return (
    <Box>
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
            "&	.MuiDataGrid-columnHeader:focus-within": {
              outline: "none"
            }
          }
        }
      />
      <ExpressionDialog 
        value={ selectedExpression } 
        open={dialogOpen} 
        onClose={ closeDialog }
      />
    </Box>
  )
}