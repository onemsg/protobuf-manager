import { Box } from '@mui/system';
import { Route, Routes, useNavigation } from 'react-router-dom';
import './index.css';
import AppHeader from './layout/AppHeader';
import NavigationMenu from './layout/NavigationMenu';
import Home from './pages/Home';
import NotMatch from './pages/NotMatch';
import ApplicationPage from './pages/application';
import CreateApplicationPage from './pages/application/CreateApplicationPage';
import GroupPage from './pages/group';
import CreateGroupPage from './pages/group/CreateGroupPage';
import ProtobufDetailPage from './pages/protobuf';
import CreateProtobufPage from './pages/protobuf/CreateProtobufPage';
import EditProtobufPage from './pages/protobuf/EditProtobufPage';
import EditProtobufCodePage from './pages/protobuf/EditProtobufCodePage';
import { LinearProgress } from '@mui/material';



function App() {

  return (
    <Box sx={ { display: 'flex' } }>
      <AppHeader />
      <NavigationMenu />
      <Routes>
        <Route path='/' element={ <Home /> } />
        <Route path='/protobuf/create' element={ <CreateProtobufPage /> } />
        <Route path='/protobuf/:id' element={ <ProtobufDetailPage /> } />
        <Route path='/protobuf/edit/:id' element={ <EditProtobufPage /> } />
        <Route path='/protobuf/edit-code/:id' element={ <EditProtobufCodePage /> } />
        <Route path='/application' element={ <ApplicationPage /> } />
        <Route path='/application/create' element={ <CreateApplicationPage /> } />
        <Route path='/group' element={ <GroupPage /> } />
        <Route path='/group/create' element={ <CreateGroupPage /> } />
        <Route path='*' element={ <NotMatch /> }></Route>
      </Routes>
    </Box>
  );
}

export default App;
