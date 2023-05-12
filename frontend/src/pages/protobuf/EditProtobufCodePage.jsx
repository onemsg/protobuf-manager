import { Button, Container, Divider, Paper, Stack, Box, Typography } from '@mui/material';
import * as monaco from 'monaco-editor';
import React, { useEffect, useState } from 'react';

import SaveIcon from '@mui/icons-material/Save';
import SpellcheckIcon from '@mui/icons-material/Spellcheck';
import { useParams } from 'react-router-dom';

import MainPart from "../../layout/MainPart";

class ProtobufEditor extends React.Component {
  componentDidMount() {
    const editorOptions = {
      value: "",
      language: 'proto',
      lineNumbers: 'on',
    }
    const editor = monaco.editor.create(document.getElementById("protobuf-editor"), editorOptions)
    this.props.didMount(editor)
    // editor.getValue()
  }

  render() {
    return (
      <article id="protobuf-editor" style={{height: '720px'}}/>
    ); 
  }
}


function EditProtobufCodePage() {

  const [editor, setEditor] = useState();

  const { id } = useParams()
  const [protobufCode, setProtobufCode] = useState({})
  useEffect(() => {
    const url = `/api/protobuf/${id}/current-code`
    fetch(url)
      .then(res => res.json())
      .then(body => {
        if (body.code === 0) {
          setProtobufCode(body.data)
        } else {
          alert(body.message)
        }
      }, err => {
        alert(err)
      })
  }, [id])

  const handleSave = () => {

    if ( window.confirm("Ready to save?")) {
      const value = editor.getValue()
      // TODO
    }
  }

  const handleCheck = () => {
    // TODO
    console.log(editor.getValue())
  }

  return (
    <MainPart backPath={"/protobuf/" + id} title="Update Protobuf Code">
      <Container component="section">
        <Paper elevation={ 8 } component='article'>
          <Stack
            direction='row'
            paddingX={ 2 }
            paddingY={ 1 }
            spacing={ 1 }
          >
            <Box sx={{ flexGrow: 1}}></Box>
            <Button size='medium' variant='text' startIcon={ <SaveIcon /> } color="primary" onClick={ handleSave }>
              SAVE
            </Button>
            <Button size='medium' variant='text' startIcon={ <SpellcheckIcon /> } color='secondary' onClick={ handleCheck }>
              Check
            </Button>
          </Stack>

          <Divider />

          <ProtobufEditor didMount={ (_editor) => setEditor(_editor) } />

        </Paper>
      </Container>
    </MainPart>


  );
}

export default EditProtobufCodePage;