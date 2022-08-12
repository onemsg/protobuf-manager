import { Button, Container, Divider, Paper, Stack } from '@mui/material';
import * as monaco from 'monaco-editor';
import React, { useState } from 'react';

import SaveIcon from '@mui/icons-material/Save';
import SpellcheckIcon from '@mui/icons-material/Spellcheck';

class ProtobufEditor extends React.Component {
  constructor(props) {
    super(props);
  }

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


function EditProtobufFilePage() {

  const [editor, setEditor] = useState();

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
    <Container component="section">
      <Paper elevation={ 8 } component='article'>
        <Stack
          direction='row'
          justifyContent="flex-end"
          paddingX={2}
          paddingY={1}
          spacing={1}
        >
          <Button size='medium' variant='text' startIcon={ <SaveIcon /> } color="primary" onClick={ handleSave }>
            SAVE
          </Button>
          <Button size='medium' variant='text' startIcon={ <SpellcheckIcon /> } color='secondary' onClick={handleCheck}>
            Check
          </Button>
        </Stack>

      <Divider />

      <ProtobufEditor didMount={(_editor) => setEditor(_editor)} />
  
      </Paper>
    </Container>
  );
}

export default EditProtobufFilePage;