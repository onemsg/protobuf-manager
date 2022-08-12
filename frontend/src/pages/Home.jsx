import { Button, styled, TextField, Typography } from '@mui/material';
import FormGroup from '@mui/material/FormGroup';
import { DataGrid } from '@mui/x-data-grid';
import React, { useEffect, useState } from 'react';

import {
  useSearchParams
} from 'react-router-dom'
import { AUTH_HEADERS } from '../api';


function SearchPanel({initSearchParams, onSearch, onReload}){
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
    <FormGroup row>
      <TextField 
        id="search-field" 
        label="搜索词"
        placeholder="服务名|PB名|Owner"
        variant='filled'
        sx={ { m: 2, width: 400} }
        defaultValue={ initSearchParams?.search || ""}
        onChange={ event => setSearch(event.target.value) } 
        onKeyUp={ handleKeyup }
      />

      <Button
        variant='contained'
        color='primary'
        onClick={ handleSearch }
        sx={{m:2, width: 100}}
      >
        Search
      </Button>

      <Button
        variant="contained"
        color="inherit"
        onClick={ onReload }
        sx={ { m: 2, width: 100 } }
      >
        Reload
      </Button>

    </FormGroup>
  )
}

/** 列定义 */
const COLUMN_SCHEMA = [
  {
    field: "id",
    headerName: "ID",
    maxWidth: 70,
    align: "right"
  },
  {
    field: "group",
    headerName: "Group",
    minWidth: 150,
    maxWidth: 200,
    flex: 1
  },
  {
    field: "application",
    headerName: "Application",
    minWidth: 250,
    flex: 1
  },
  {
    field: "name",
    headerName: "Name",
    minWidth: 250,
    flex: 1
  },
  {
    field: "path",
    headerName: "Path",
    maxWidth: 200,
    flex: 1
  },
  {
    field: "protocol",
    headerName: "Protocol",
    width: 150,
  },
  {
    field: "lastVersion",
    headerName: "Last Version",
    align: "right",
    minWidth: 150,
    maxWidth: 200,
    flex: 1
  },
  {
    field: "owner",
    headerName: "Owner",
    minWidth: 150,
    maxWidth: 200,
    flex: 1
  },
  {
    field: "modified",
    headerName: "Last Modified",
    minWidth: 200,
    flex: 1
  }
]

const DEFAULT_PAGEINDEX = 0
const DEFAULT_PAGESIZE = 20

function Home() {

  document.title = "Protobuf Management"

  const [isLoaded, setIsLoaded] = useState(true);

  const [urlParams, setUrlParams] = useSearchParams({})

  const [pageIndex, setPageIndex] = useState(DEFAULT_PAGEINDEX)
  const [pageSize, setPageSize] = useState(DEFAULT_PAGESIZE)
  const [total, setTotal] = useState(0)
  const [rows, setRows] = useState([]);

  const searchParams = {
    search: urlParams.get('search')
  }

  useEffect(() => {
    const _searchParams = {
      search: urlParams.get('search')
    }
    loadData({_searchParams}, pageIndex, pageSize)
  }, [urlParams])

  const loadData = (params, _pageIndex, _pageSize) => {
    console.log("[INFO] Start fetch data -", params, _pageIndex, _pageSize)
    setIsLoaded(false)
    let search = params?.search || ""
    const url = `/api/protobuf?search=${search}&pageIndex=${_pageIndex + 1}&pageSize=${[_pageSize]}`
    fetch(url, {headers: {...AUTH_HEADERS} })
      .then(res => res.json())
      .then(
        body => {
          setIsLoaded(true)
          setRows(body.data)
          setTotal(body.total)
        },
        err => {
          setIsLoaded(true)
          console.info("[ERROR] Http request %s failed - %s", url, err.toString())
        }
      )
  }

  const handleSearch = (submitSearchParams) => {
    if (submitSearchParams.search === searchParams.search) {
      loadData(searchParams, pageIndex, pageSize)
    }
    setUrlParams({ ...submitSearchParams})
  }

  const handleReload = () => {
    loadData(searchParams, pageIndex, pageSize)
  }

  const handlePageChange = (_page) => {
    setPageIndex(_page)
    loadData(searchParams, _page, pageSize)
  }

  const handlePageSizeChange = (_pageSize, details) => {
    setPageSize(_pageSize)
    loadData(searchParams, pageIndex, _pageSize)
  }

  return (
    <React.Fragment>
      <SearchPanel 
        initSearchParams={ searchParams }
        onSearch={handleSearch}
        onReload={handleReload}
      />

      <DataGrid
        autoHeight
        loading={ !isLoaded }
        rows={ rows }
        columns={ COLUMN_SCHEMA }
        page={ pageIndex }
        pageSize={ pageSize }
        onPageChange={ handlePageChange }
        onPageSizeChange={ handlePageSizeChange}
        rowsPerPageOptions={ [20, 50, 100] }
        paginationMode="server"
        rowCount={total}
        disableSelectionOnClick
      />
    </React.Fragment>
  );
}

export default Home;