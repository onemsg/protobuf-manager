import HelpIcon from '@mui/icons-material/Help';
import { Box, IconButton, Toolbar, Typography } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import Tooltip from '@mui/material/Tooltip';
import { Link } from 'react-router-dom';
import UserProfile from './UserProfile';



function AppHeader() {
  return (
    <AppBar position='fixed' color='primary' component="header" sx={ { zIndex: (theme) => theme.zIndex.drawer + 1 } }>

      <Toolbar variant='dense'>
        <Link to="/" className="is-clear" >
          <Typography
            variant="h6"
            align="center"
            color="#fff0f0"
          >
            Protobuf Management
          </Typography>
        </Link>

        <Box sx={ { flexGrow: 1 } } />

        <Tooltip title={ <Typography>Usage help</Typography> }>
          <IconButton
            size="large"
            color="inherit"
          >
            <HelpIcon />
          </IconButton>
        </Tooltip>

        <UserProfile />
      </Toolbar>
    </AppBar>
  );
}

export default AppHeader;