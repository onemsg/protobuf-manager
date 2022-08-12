import { Button, Container, FormControl, InputLabel, MenuItem, Select, Stack, TextField } from "@mui/material";
import { Box } from "@mui/system";
import { useMemo, useState } from "react";
import { useParams } from "react-router-dom";


const mockGouprs = [
  {
    label: "Group-A",
    value: "Group-A",
    applications: [
      {
        value: "application-1"
      },
      {
        value: "application-2"
      },
      {
        value: "application-3"
      }
    ]
  },
  {
    label: "Group-B",
    value: "Group-B",
    applications: [
      {
        value: "application-11"
      },
      {
        value: "application-12"
      },
      {
        value: "application-13"
      }
    ]
  },
  {
    label: "Group-C",
    value: "Group-C",
    applications: [
      {
        value: "application-21"
      },
      {
        value: "application-22"
      },
      {
        value: "application-23"
      }
    ]
  },
  {
    label: "Group-D",
    value: "Group-D",
    applications: [
      {
        value: "application-31"
      },
      {
        value: "application-32"
      },
      {
        value: "application-33"
      }
    ]
  },
  {
    label: "Group-E",
    value: "Group-E",
    applications: [
      {
        value: "application-41"
      },
      {
        value: "application-42"
      },
      {
        value: "application-43"
      }
    ]
  },
  {
    label: "Group-F",
    value: "Group-F",
    applications: [
      {
        value: "application-51"
      },
      {
        value: "application-52"
      },
      {
        value: "application-53"
      }
    ]
  },
]

/**
 * 创建/编辑 Protobuf 信息页面
 * @param {boolean} created 是否是创建页面
 * @returns 
 */
function EditProtobufPage({ created }) {

  document.title = created ? "Create Protobuf - Protobuf Management"
    : "Edit Protobuf - Protobuf Management"

  const { id } = useParams()

  const [group, setGroup] = useState("");
  const [application, setApplication] = useState("");
  const [name, setName] = useState("");
  const protocol = 'grpc'
  const owner = "nara"

  if (!created) {
    // TODO
  }


  const applications = useMemo(() => {
    return mockGouprs.find(_group => _group.value === group)?.applications || []
  }, [group])

  const handleCreate = (event) => {
    if (!event.target.form.checkValidity()) {
      return
    } else {
      event.preventDefault()
    }

    const requestData = {
      group: group,
      application: application,
      name: name,
      protocol: protocol,
      owner: owner,
    }

    fetch("/api/protobuf", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestData)
    }).then(res => res.json())
      .then(data => {
        //TODO 创建重构 从定向
        console.log(data)
      }, error => {
        console.log(error)
      })

  }

  const handleEdit = (event) => {
    if (!event.target.form.checkValidity()) {
      return
    } else {
      event.preventDefault()
    }

    const requestData = {
      group: group,
      application: application,
      name: name,
      protocol: protocol,
      owner: owner,
    }

    fetch(`/api/protobuf/${id}`, {
      method: "PUT",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestData)
    }).then(res => res.json())
      .then(data => {
        //TODO 创建重构 从定向
        console.log(data)
      }, error => {
        console.log(error)
      })

  }

  return (
    <Container component="article" sx={ { width: '50%' } } >
      <Stack spacing={ 2 } component="form" autoComplete="off">
        <FormControl fullWidth variant="standard">
          <InputLabel id='group-select-label' required>Group</InputLabel>
          <Select
            labelId='group-select-label'
            id='group-select'
            value={ group }
            label='Group'
            required
            onChange={ event => setGroup(event.target.value) }
          >
            {
              mockGouprs.map(_group => (
                <MenuItem key={ _group.value } value={ _group.value }>{ _group.label }</MenuItem>
              ))
            }
          </Select>
        </FormControl>

        <FormControl fullWidth variant="standard">
          <InputLabel id='application-select-label' required>Application</InputLabel>
          <Select
            labelId='application-select-label'
            id='application-select'
            value={ application }
            label='Application'
            required
            onChange={ event => setApplication(event.target.value) }
          >
            {
              applications.map(_application => (
                <MenuItem key={ _application.value } value={ _application.value }>{ _application.value }</MenuItem>
              ))
            }
          </Select>
        </FormControl>

        <TextField
          label="Protobuf Name"
          variant="standard"
          fullWidth
          required
          value={ name }
          onChange={ event => setName(event.target.value) }
        />

        <TextField
          label="protocol"
          variant="standard"
          sx={ { width: '25%' } }
          defaultValue={ protocol }
          InputProps={ { readOnly: true } }
          required
        />

        <TextField
          label="Owner"
          variant="standard"
          sx={ { width: '25%' } }
          defaultValue={ owner }
          InputProps={ { readOnly: true } }
          required
        />

        <Box sx={ { display: "flex", width: '100%' } }>
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

export default EditProtobufPage;