import { Box, Button, Chip, Divider, Paper, Stack, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import { useParams } from 'react-router-dom';

function FieldShow({ name, value }) {
  return (
    <Box>
      <Typography color='text.secondary' >
        { name }
      </Typography>
      <Typography variant='h6'>
        { value }
      </Typography>
    </Box>
  )
}

function ProtobufPage() {

  document.title = "Protobuf - Protobuf Management"

  const {id} = useParams()

  console.log("Current protbuf id is", id)



  return (
    <section>
      <Paper elevation={ 8 } component='article'>
        <Stack
          direction='row'
          justifyContent="flex-end"
          padding={ 1 }
        >
          <Button size='large' href={ window.location.pathname + "/edit-file"}>
            UPDATE FILE
          </Button>
          <Button size='large' href={ window.location.pathname + "/edit" }>EDIT</Button>
        </Stack>

        <Grid container spacing={ 4 } paddingX={ 4 } paddingTop={ 0 } paddingBottom={ 2 }>

          <Grid item xs={ 2 }>
            <FieldShow name="Group" value="Group-A" />
          </Grid>

          <Grid item xs={ 2 }>
            <FieldShow name="Application" value="app-1" />
          </Grid>

          <Grid item xs={ 2 }>
            <FieldShow name="Onwer" value="mashuguang" />
          </Grid>

          <Grid item xs={ 6 }>
            <Typography color='text.secondary' >
              Intro
            </Typography>
            <Typography variant='h6'>
              This is a demo project
            </Typography>
          </Grid>

          <Grid item xs={ 12 } >
            <Divider light />
          </Grid>

          <Grid item xs={ 2 }>
            <FieldShow name="ID" value="15" />
          </Grid>

          <Grid item xs={ 3 }>
            <FieldShow name="name" value="grpc_book_server" />
          </Grid>

          <Grid item xs={ 6 }>
            <FieldShow name="path" value="/group-A/app-1/grpc_book_server" />
          </Grid>

          <Grid item xs={ 2 }>
            <FieldShow name="protocol" value="grpc" />
          </Grid>

          <Grid item xs={ 3 }>
            <Box>
              <Typography color='text.secondary' >
                last version
              </Typography>

              <Typography variant='h6' >
                <Chip
                  label={ <Typography variant='h6'>1.0.1 </Typography> }
                />
              </Typography>
            </Box>
          </Grid>

          <Grid item xs={ 3 }>
            <FieldShow name="owner" value="mashuguang" />
          </Grid>

        </Grid>
      </Paper>
    </section>
  );
}

export default ProtobufPage;