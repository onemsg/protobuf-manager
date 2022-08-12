import { Autocomplete, Button, Container, Stack, TextField } from "@mui/material";
import { Box } from "@mui/system";

import {React, useState} from 'react';

const mockGouprs = [
  { label: "Group-A", value: "Group-A" },
  { label: "Group-B", value: "Group-B" },
  { label: "Group-C", value: "Group-C" },
  { label: "Group-D", value: "Group-D" },
  { label: "Group-E", value: "Group-E" },
  { label: "Group-F", value: "Group-F" },
]

function CreateApplicationPage() {

  document.title = "Create Application - Protobuf Management"

  const [group, setGroup] = useState("");
  const [application, setApplication] = useState("");
  const owner = "nara"
  const [intro, setIntro] = useState("");

  const handleCreate = (event) => {
    if (!event.target.form.checkValidity()) {
      return
    } else {
      event.preventDefault()
    }
    
    const requestData = {
      group: group,
      application: application,
      owner: owner,
      intro: intro
    }

    fetch("/api/application", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestData)
    }).then( res => res.json())
    .then( data => {
      //TODO 创建重构 从定向
      console.log(data)
    }, error => {
      console.log(error)
    })

  }

  return (
    <Container component="article" sx={ { width: '50%' } } >
        <Stack spacing={2} component="form" autoComplete="off">
          <Autocomplete
            disablePortal
            id='group-input-id'
            options={ mockGouprs }
            fullWidth
            renderInput={ params => 
              <TextField 
                { ...params } 
                label="Group" 
                variant="standard"
                value={group}
                onChange={event => setGroup(event.target.value)}
                required 
              /> }
          >
          </Autocomplete>

          <TextField 
            label="Application" 
            variant="standard" 
            fullWidth
            required 
            value={ application }
            onChange={ event => setApplication(event.target.value) }
          />

          <TextField 
            label="Owner" 
            sx={ { width: '25%' } } 
            variant="standard"
            defaultValue={ owner}
            InputProps={ {readOnly: true} }
            required
          />

          <TextField label="Intro" 
            fullWidth
            variant="standard" 
            multiline 
            rows='4' 
            value={ intro }
            onChange={ event => setIntro(event.target.value) }
          />

          <Box sx={{display: "flex", width: 350}}>
            <span className="has-flex-grow-1"></span>
            <Button 
              variant="text" 
              sx={ { width: 80 } } 
              size='large'
              type="sumbit"
              onClick={ handleCreate }
            >
              Create
            </Button>
          </Box>

        </Stack>
    </Container>
  );
}

export default CreateApplicationPage;