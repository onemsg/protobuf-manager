import { Button, Container, Stack, TextField } from "@mui/material";
import { Box } from "@mui/system";

import CreateIcon from '@mui/icons-material/Create';
import { React } from 'react';
import { useForm } from "react-hook-form";
import MainPart from '../../layout/MainPart';


function CreateGroupPage() {

  document.title = "Create Group - Protobuf Management"

  const { register, handleSubmit } = useForm();

  const onSubmit = (data) => {
    const url = "/api/group"
    fetch(url, {body: JSON.stringify(data), headers: {"Content-Type": "application/json"}, method: "POST"})
      .then(res => res.json())
      .then(body => {
        if (body.code === 0) {
          alert("Create OK")
        } else {
          alert(body.message)
        }
      }, err => {
        alert(err)
      })
  }

  return (
    <MainPart backpath="/group" title="Create Group">
      <Container component="section" sx={ { width: '50%' } } >
        <Stack spacing={ 2 } component="form" autoComplete="off" onSubmit={ handleSubmit(onSubmit) }>
          <TextField
            label="Name"
            variant="standard"
            sx={ { width: "30ch" } }
            required
            { ...register("name") }
          />

          <TextField label="Intro"
            fullWidth
            variant="standard"
            { ...register("intro") }
          />

          <Box>
            <Button type="sumbit" startIcon={<CreateIcon/>}>
              Create
            </Button>
          </Box>
        </Stack>
      </Container>
    </MainPart>

  );
}

export default CreateGroupPage;