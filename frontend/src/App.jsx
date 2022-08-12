import { Box } from '@mui/system';
import { Route, Routes } from 'react-router-dom';
import './index.css';
import AppHeader from './layout/AppHeader';
import MainPart from './layout/MainPart';
import CreateApplicationPage from './pages/create-application';
import Home from './pages/Home';
import NotMatch from './pages/NotMatch';
import ProtobufPage from './pages/protobuf';
import EditProtobufFilePage from './pages/protobuf/EditProtobufFilePage';
import EditProtobufPage from './pages/protobuf/EditProtobufPage'

function App() {
  return (
    <Box sx={ { display: 'flex' } }>
      <AppHeader />

      <MainPart>
        <Routes>
          <Route path='/' element={ <Home /> } />
          <Route path='/protobuf/:id' element={ <ProtobufPage /> } />
          <Route path='/protobuf/:id/edit' element={ <EditProtobufPage /> } />
          <Route path='/protobuf/:id/edit-file' element={ <EditProtobufFilePage /> } />
          <Route path='/create-application' element={ <CreateApplicationPage />} />
          <Route path='/create-protobuf' element={ <EditProtobufPage created/> } />\
          <Route path='*' element={ <NotMatch/> }></Route>
        </Routes>
      </MainPart>
    </Box>
  );
}

export default App;
