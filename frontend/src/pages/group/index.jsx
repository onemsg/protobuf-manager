import CreateIcon from '@mui/icons-material/Create';
import { Box, Button, Stack } from "@mui/material";
import { DataGrid } from '@mui/x-data-grid';
import React, { useEffect, useState } from 'react';
import MainPart from '../../layout/MainPart';


const DEFAULT_PAGESIZE = 20

function GroupPage() {

  document.title = "Group - Protobuf Management"

  const [isLoaded, setIsLoaded] = useState(true);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGESIZE)
  const [data, setData] = useState([])

  useEffect(() => {
    setIsLoaded(false)
    const url = "/api/group"
    console.log("[INFO] Start fetch data - ", url)
    fetch(url)
      .then(res => res.json())
      .then(
        body => {
          if (body.code === 0) {
            setData(body.data)
          } else {
            alert(body.message)
          }
        },
        err => {
          console.info("[ERROR] Http request %s failed - %s", url, err.toString())
        }
      ).finally(() => {
        setIsLoaded(true)
      })
  }, [])

  /** 列定义 */
  const COLUMN_SCHEMA = [
    {
      field: "id",
      headerName: "ID",
      maxWidth: 70,
      align: "right"
    },
    {
      field: "name",
      headerName: "Name",
      minWidth: 150,
      maxWidth: 200,
      flex: 1
    },
    {
      field: "intro",
      headerName: "Intro",
      minWidth: 250,
      flex: 1,
      editable: true
    },
    {
      field: "creator",
      headerName: "Creator",
      maxWidth: 200
    },
    {
      field: "createdTime",
      headerName: "Created Time",
      minWidth: 200,
      flex: 1
    },
    {
      field: "updatedTime",
      headerName: "Last Updated",
      minWidth: 200,
      flex: 1
    }
  ]

  return (
    <MainPart>
      <Stack direction="row" mb={1}>
        <Box sx={ { flex: 1 } }></Box>
        <Button
          color='primary'
          href='/group/create'
          startIcon={ <CreateIcon /> }
        >
          Create
        </Button>
      </Stack>
      <DataGrid
        autoHeight
        loading={ !isLoaded }
        rows={ data }
        columns={ COLUMN_SCHEMA }
        pageSize={ pageSize }
        rowsPerPageOptions={ [20, 50, 100] }
        onPageSizeChange={ newPageSize => setPageSize(newPageSize)}
        disableSelectionOnClick
      />
    </MainPart>
  );
}

export default GroupPage;