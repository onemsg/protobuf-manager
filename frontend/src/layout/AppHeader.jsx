import HelpIcon from '@mui/icons-material/Help';
import { Avatar, Box, Button, IconButton, Toolbar, Typography } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import Tooltip from '@mui/material/Tooltip';
import { Link, useLocation } from 'react-router-dom';

const imgurl = "https://yt3.ggpht.com/ytc/AKedOLQhnYVtCeqU8x1xLs6h7AlCsIRfbZpcFK-4v0LOYw=s88-c-k-c0x00ffffff-no-rj"

function UserProfile({ name, imgUrl }) {
  return (
    <Tooltip
      title={ <Typography>{ name }</Typography> }
    >
      <IconButton>
        <Avatar
          alt="name"
          src={ imgUrl }
          sx={ { width: 32, height: 32 } }
        />
      </IconButton>
    </Tooltip>
  )
}

function NavTitle({path, name, isActive }) {
  return (
    < Button
      color="inherit"
      className={ isActive ? "nav-title is-active" : "nav-title"}
      disabled={ isActive }
      href={ path }
    >
      <Typography variant="h6" align="center" color="#f3e5f5">
        { name }
      </Typography>
    </Button>
  )
}

const titles = [
  {
    path: "/create-application",
    name: "Create Application"
  },
  {
    path: "/create-protobuf",
    name: "Create Protobuf"
  }
]

function AppHeader() {

  const location = useLocation()

  const matchPath = (path) => location.pathname.startsWith(path)

  return (
    <AppBar position='fixed' color='primary' component="header">

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

        <Box sx={ { marginX: 2 } }></Box>

        {
          titles.map(title => (<NavTitle key={title.name} path={ title.path } name={ title.name } isActive={ matchPath(title.path) } />))
        }

        <Box sx={ { flexGrow: 1 } } />

        <Tooltip title={ <Typography>Usage help</Typography> }>
          <IconButton
            size="large"
            color="inherit"
          >
            <HelpIcon />

          </IconButton>
        </Tooltip>

        <UserProfile name="nara" imgUrl={ imgurl }></UserProfile>
      </Toolbar>
    </AppBar>
  );
}

export default AppHeader;