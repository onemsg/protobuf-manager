import { Button, Container, FormControl, InputLabel, MenuItem, Select, Stack, TextField } from "@mui/material";
import { Box } from "@mui/system";

import CreateIcon from '@mui/icons-material/Create';
import { React, useEffect, useState } from 'react';
import { useForm } from "react-hook-form";
import MainPart from "../../layout/MainPart";

function CreateProtobufPage() {

  document.title = "Create Application - Protobuf Management"

  const [groups, setGroups] = useState([])
  useEffect(() => {
    const url = "/api/group/name"
    fetch(url).then(res => res.json())
      .then(body => {
        if (body.code === 0) {
          setGroups(body.data)
        } else {
          alert(body.message)
        }
      })
  })

  const [selectedGroupId, setSelectedGroupId] = useState('')
  const [applications, setApplications] = useState([])
  const [applicationId, setApplicationId] = useState('')

  useEffect(() => {
    setApplicationId("")
    if (selectedGroupId) {
      const url = "/api/application/name?groupId=" + selectedGroupId
      fetch(url).then(res => res.json())
        .then(body => {
          if (body.code === 0) {
            setApplications(body.data)
          } else {
            alert(body.message)
          }
        })
    }
  }, [selectedGroupId])

  const { register, handleSubmit } = useForm();

  const onSubmit = (data) => {
    const url = "/api/protobuf"
    data = { ...data, applicationId: applicationId }
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

  return (
    <MainPart backPath="/" title="Create Protobuf">
      <Container component="section" sx={ { width: '50%' } } >
        <Stack spacing={ 2 } component="form" autoComplete="off" onSubmit={ handleSubmit(onSubmit) }>

          <FormControl variant="standard" sx={ { width: "30ch" } }>
            <InputLabel id="group-select-label">Group</InputLabel>
            <Select
              labelId="group-select-label"
              label="Group"
              value={ selectedGroupId }
              onChange={ event => setSelectedGroupId(event.target.value) }
            >
              {
                groups.map(group => <MenuItem value={ group.id } key={ group.id } >{ group.name }</MenuItem>)
              }
            </Select>
          </FormControl>

          <FormControl variant="standard" sx={ { width: "30ch" } }>
            <InputLabel id="application-select-label">Application</InputLabel>
            <Select
              labelId="application-select-label"
              label="Application"
              value={applicationId}
              onChange={ event => setApplicationId(event.target.value) }
              required
            >
              {
                applications.map(app => <MenuItem value={ app.id } key={ app.id } >{ app.name }</MenuItem>)
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

          <TextField label="Protocol"
            fullWidth
            variant="standard"
            value="grpc"
            InputProps={ { readOnly: true } }
            required
            { ...register("protocol") }
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

export default CreateProtobufPage;