import CachedIcon from '@mui/icons-material/Cached';
import CreateIcon from '@mui/icons-material/Create';
import SearchIcon from '@mui/icons-material/Search';
import { Box, Button, IconButton, Stack, TextField } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import React, { useEffect, useState } from 'react';
import { useNavigate } from "react-router-dom";
import MainPart from '../layout/MainPart';

function ControlBar({onSearch, onReload}){
  const [search, setSearch] = useState("")

  const handleSearch = () => {
    onSearch({search})
  }

  const handleKeyup = event => {
    if (event.keyCode === 13){
      event.preventDefault()
      handleSearch()
    }
  }

  return (
    <Stack direction="row" alignItems="center" spacing={1} sx={{mb: 1}}>
      <TextField 
        id="search-field" 
        hiddenLabel
        placeholder="服务名|PB名|Owner"
        variant='standard'
        sx={ {width: 400} }
        onChange={ event => setSearch(event.target.value) } 
        onKeyUp={ handleKeyup }
      />

      <IconButton type="button" aria-label="search" onClick={ handleSearch }>
        <SearchIcon />
      </IconButton>

      <IconButton type="button" aria-label="reload" onClick={ onReload }>
        <CachedIcon />
      </IconButton>

      <Box sx={ { flex: 1 } }></Box>
      <Button
        color='primary'
        href='/protobuf/create'
        startIcon={ <CreateIcon /> }
      >
        Create
      </Button>
    </Stack>
  )
}

const DEFAULT_PAGEINDEX = 0
const DEFAULT_PAGESIZE = 20

function Home() {


  document.title = "Protobuf Management"

  const [isLoaded, setIsLoaded] = useState(true);

  const [request, setRequest] = useState({
    search: "",
    pageIndex: DEFAULT_PAGEINDEX,
    pageSize: DEFAULT_PAGESIZE,
  })

  const [data, setData] = useState({
    pages: 0,
    total: 0,
    rows: []
  })


  useEffect(() => {
    setIsLoaded(false)
    const query = new URLSearchParams({
      search: request.search,
      pageIndex: request.pageIndex,
      pageSize: request.pageSize
    })
    const url = "/api/protobuf?" + query.toString()
    console.log("[INFO] Start fetch data - ", url)
    fetch(url)
      .then(res => res.json())
      .then(
        body => {
          if (body.code === 0) {
            const data = body.data;
            setData({ pages: data.pages, total: data.total, rows: data.data })
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
  }, [request])


  const handleSearch = (params) => {
    setRequest({ ...request, search: params.search })
  }

  const navigate = useNavigate();

  const handleReload = () => {
    navigate(0)
  }

  const handlePageChange = (page) => {
    setRequest({...request, pageIndex: page})
  }

  const handlePageSizeChange = (pageSize, details) => {
    setRequest({ ...request, pageSize: pageSize })
  }

  const handleEnterDetail = (id) => {
    navigate("/protobuf/" + id)
  }

  /** 列定义 */
  const COLUMN_SCHEMA = [
    {
      field: "id",
      headerName: "ID",
      maxWidth: 70,
      align: "right",
      headerAlign: "right"
    },
    {
      field: "group",
      headerName: "Group",
      minWidth: 150,
    },
    {
      field: "application",
      headerName: "Application",
      minWidth: 150,
    },
    {
      field: "name",
      headerName: "Name",
      minWidth: 200,
    },
    {
      field: "path",
      headerName: "Path",
      minWidth: 200,
      flex: 1
    },
    {
      field: "protocol",
      headerName: "Protocol",
      maxWidth: 80,
      align: "right",
      headerAlign: "right"
    },
    {
      field: "currentVersionText",
      headerName: "Current Version",
      minWidth: 120,
      maxWidth: 200,
      align: "right",
      headerAlign: "right"
    },
    {
      field: "creator",
      headerName: "Creator",
      minWidth: 80,
      maxWidth: 150,
    },
    {
      field: "updatedTime",
      headerName: "Last Updated",
      minWidth: 200,
    },
    {
      field: 'actions',
      type: 'actions',
      minWidth: 150,
      getActions: (params) => [
        <Button key={ params.row.id } onClick={ () => handleEnterDetail(params.row.id) } >Detail</Button>,
      ]
    }
  ]

  return (
    <MainPart>
      <ControlBar 
        onSearch={handleSearch}
        onReload={handleReload}
      />

      <DataGrid
        autoHeight
        loading={ !isLoaded }
        rows={ data.rows }
        columns={ COLUMN_SCHEMA }
        page={ request.pageIndex }
        pageSize={ request.pageSize }
        onPageChange={ handlePageChange }
        onPageSizeChange={ handlePageSizeChange}
        rowsPerPageOptions={ [20, 50, 100] }
        paginationMode="server"
        rowCount={data.total}
        disableSelectionOnClick
      />
    </MainPart>
  );
}

export default Home;