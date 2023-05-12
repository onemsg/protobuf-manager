import { Button, Container, Stack, TextField, Select, FormControl, InputLabel } from "@mui/material";
import { Box } from "@mui/system";

import { React, useEffect, useState } from 'react';
import { useForm } from "react-hook-form";
import CreateIcon from '@mui/icons-material/Create';
import MainPart from "../../layout/MainPart";

import { useParams } from "react-router-dom";

function EditProtobufPage() {

  document.title = "Create Application - Protobuf Management"

  const { register, handleSubmit } = useForm();

  const onSubmit = (data) => {
    const url = `/api/protobuf/${id}/intro`
    fetch(url, { body: JSON.stringify(data), headers: { "Content-Type": "application/json" }, method: "PATCH" })
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

  const { id } = useParams()
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


  return (
    <MainPart backPath={"/protobuf/" + id} title="Edit Protobuf">
      <Container component="section" sx={ { width: '50%' } } >
        <Stack spacing={ 2 } component="form" autoComplete="off" onSubmit={ handleSubmit(onSubmit) }>

          <FormControl variant="standard" sx={ { width: "30ch" } }>
            <InputLabel id="group-select-label">Group</InputLabel>
            <Select
              labelId="group-select-label"
              label="Group"
              value={protobuf.group}
            >
            </Select>
          </FormControl>

          <FormControl variant="standard" sx={ { width: "30ch" } }>
            <InputLabel id="application-select-label">Group</InputLabel>
            <Select
              labelId="application-select-label"
              label="Application"
              value={ protobuf.application }
            >
            </Select>
          </FormControl>

          <TextField
            label="Name"
            variant="standard"
            sx={ { width: "30ch" } }
            value={protobuf.name}
          />

          <TextField label="Intro"
            fullWidth
            variant="standard"
            defaultValue={ protobuf.intro}
            { ...register("intro") }
          />

          <TextField label="Protocol"
            fullWidth
            variant="standard"
            value={protobuf.protocol}
            InputProps={ { readOnly: true } }
          />

          <Box>
            <Button type="sumbit" startIcon={ <CreateIcon /> }>
              Update
            </Button>
          </Box>
        </Stack>
      </Container>
    </MainPart>

  );
}

export default EditProtobufPage;