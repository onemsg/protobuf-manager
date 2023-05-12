import { Button, Container, Stack, TextField, Select, MenuItem, FormControl, InputLabel } from "@mui/material";
import { Box } from "@mui/system";

import { React, useEffect, useState } from 'react';
import { useForm } from "react-hook-form";
import BackTitle from "../../layout/BackTitle";
import CreateIcon from '@mui/icons-material/Create';
import MainPart from "../../layout/MainPart";

function CreateApplicationPage() {

  document.title = "Create Application - Protobuf Management"

  const { register, handleSubmit } = useForm();

  const onSubmit = (data) => {
    const url = "/api/application"
    fetch(url, { body: JSON.stringify(data), headers: { "Content-Type": "application/json" }, method: "POST" })
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

  const [groups, setGroups] = useState([])
  useEffect(() => {
    const url = "/api/group/name"
    fetch(url).then(res => res.json())
      .then(body => {
        if (body.code === 0 ) {
          setGroups(body.data)
        } else {
          alert(body.message)
        }
      })
  })

  return (
    <MainPart backPath="/application" title="Create Application">
      <Container component="section" sx={ { width: '50%' } } >
        <Stack spacing={ 2 } component="form" autoComplete="off" onSubmit={ handleSubmit(onSubmit) }>
          
          <FormControl variant="standard" sx={ { width: "30ch" } }>
            <InputLabel id="group-select-label">Group</InputLabel>
            <Select
              labelId="group-select-label"
              label="Group"
              { ...register("groupId") }
            >
              {
                groups.map(group => <MenuItem value={ group.id } key={ group.id } >{ group.name }</MenuItem>)
              }
            </Select>
          </FormControl>

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
            <Button type="sumbit" startIcon={ <CreateIcon /> }>
              Create
            </Button>
          </Box>
        </Stack>
      </Container>
    </MainPart>

  );
}

export default CreateApplicationPage;