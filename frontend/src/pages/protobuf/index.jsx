import DeleteIcon from '@mui/icons-material/Delete';
import DriveFileRenameOutlineIcon from '@mui/icons-material/DriveFileRenameOutline';
import EditIcon from '@mui/icons-material/Edit';
import InputIcon from '@mui/icons-material/Input';
import { Box, Button, Chip, Divider, IconButton, Paper, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip, Typography } from '@mui/material';
import Grid from '@mui/material/Unstable_Grid2';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import MainPart from '../../layout/MainPart';
import DownloadIcon from '@mui/icons-material/Download';

function FieldShow({ name, value, color }) {
  return (
    <Box>
      <Typography color='text.secondary' >
        { name }
      </Typography>
      <Typography variant='h6' color={color}>
        { value }
      </Typography>
    </Box>
  )
}

function ProtobufDetailPage() {

  document.title = "Protobuf - Protobuf Management"

  const {id} = useParams()
  const [protobuf, setProtobuf] = useState({})
  useEffect(() => {
    const url = `/api/protobuf/${id}`
    fetch(url)
      .then(res => res.json())
      .then(body => {
        if (body.code === 0) {
          setProtobuf(body.data)
        } else {
          alert(body.message)
        }
      }, err => {
        alert(err)
      })
  }, [id])

  const [protobufCodes, setProtobufCodes] = useState([])
  useEffect(() => {
    const url = `/api/protobuf/${id}/code`
    fetch(url)
      .then(res => res.json())
      .then(body => {
        if (body.code === 0) {
          setProtobufCodes(body.data)
        } else {
          alert(body.message)
        }
      }, err => {
        alert(err)
      })
  }, [id])

  const handleDelete = () => {
    if (window.confirm("是否删除 Protobuf " + id)) {
      alert("已删除")
    }
  }

  return (
    <MainPart>
      <Paper elevation={ 4 } component='article'>
        <Stack direction='row'>
          <Box sx={ { flexGrow: 1 } }></Box>
          <Button href={ "/protobuf/edit-code/" + id } size="large" startIcon={ <DriveFileRenameOutlineIcon />} >
            UPDATE CODE
          </Button>
          <Button href={ "/protobuf/edit/" + id } size="large" startIcon={ <EditIcon /> } >
            EDIT
          </Button>
          <Button size="large" startIcon={ <DeleteIcon /> } onClick={ handleDelete }>
            Delete
          </Button>
        </Stack>

        <Grid container spacing={ 4 } paddingX={ 4 } paddingTop={ 0 } paddingBottom={ 2 }>
          <Grid item xs={ 2 }>
            <FieldShow name="Group" value={protobuf.group} color="primary" />
          </Grid>

          <Grid item xs={ 2 }>
            <FieldShow name="Application" value={ protobuf.application } color="primary" />
          </Grid>

          <Grid item xs={ 3 }>
            <FieldShow name="Name" value={ protobuf.name } color="primary" />
          </Grid>

          <Grid item xs={ 5 }>
            <Typography color='text.secondary' >
              Intro
            </Typography>
            <Typography variant='h6' color="GrayText">
              {protobuf.intro}
            </Typography>
          </Grid>

          <Grid item xs={ 12 } >
            <Divider light />
          </Grid>

          <Grid item xs={ 1 }>
            <FieldShow name="ID" value={protobuf.id} color="primary" />
          </Grid>

          <Grid item xs={ 6 }>
            <FieldShow name="Path" value={ protobuf.path } color="primary" />
          </Grid>

          <Grid item xs={ 2 }>
            <FieldShow name="Protocol" value={ protobuf.protocol } color="primary" />
          </Grid>

          <Grid item xs={ 3 }>
            <Box>
              <Typography color='text.secondary' >
                Current version
              </Typography>

              <Typography variant='h6' >
                <Chip
                  label={ <Typography variant='h6'> { protobuf.currentVersionText && "-" }</Typography> }
                />
              </Typography>
            </Box>
          </Grid>

          <Grid item xs={ 2 }>
            <FieldShow name="Last Updated" value={ protobuf.updatedTime } color="GrayText" />
          </Grid>

          <Grid item xs={ 2 }>
            <FieldShow name="Creator" value={ protobuf.creator } color="GrayText" />
          </Grid>
        </Grid>
      </Paper>

      <Box sx={{height: "48px"}}></Box>

      <Paper elevation={ 4 } component='article'>

      </Paper>

      <TableContainer component={ Paper } sx={{px:4, pt:2, width: "768px", maxHeight: "440px"} } >
        <Table stickyHeader >
          <TableHead >
            <TableRow>
              <TableCell align='right'>
                <Typography color='text.secondary'>Version</Typography>
              </TableCell>
              <TableCell>
                <Typography color='text.secondary'>Creator</Typography>
              </TableCell>
              <TableCell>
                <Typography color='text.secondary'>Created Time</Typography>
              </TableCell>
              <TableCell>
                <Typography color='text.secondary'>Actions</Typography>
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {
              protobufCodes.map(code => (
                <TableRow key={code.id}>
                  <TableCell align='right' color='blue'>
                    <Chip label={code.versionText} color="primary" variant={ code.isCurrent ? "filled" : "outlined"} />
                  </TableCell>
                  <TableCell>{code.creator}</TableCell>
                  <TableCell>{ code.createdTime }</TableCell>
                  <TableCell>
                    <Tooltip title="Download code file of this version ">
                      <IconButton>
                        <DownloadIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Edit new code from this version">
                      <IconButton>
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Set this version as current version">
                      <IconButton>
                        <InputIcon />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))
            }

          </TableBody>
        </Table>
      </TableContainer>
    </MainPart>
  );
}

export default ProtobufDetailPage;