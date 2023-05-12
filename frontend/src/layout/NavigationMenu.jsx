import ArticleIcon from '@mui/icons-material/Article';
import GridViewIcon from '@mui/icons-material/GridView';
import GroupIcon from '@mui/icons-material/Group';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Toolbar from '@mui/material/Toolbar';
import * as React from 'react';
import { useLocation } from 'react-router-dom';

const drawerWidth = 240;

export default function NavigationMenu() {
  const location = useLocation()

  const matchPath = (path) => location.pathname.startsWith(path)
  const isRoot = () => location.pathname === "/"

  return (
    <Box>
      <Drawer
        variant="permanent"
        sx={ {
          width: drawerWidth,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box' },
        } }
      >
        <Toolbar />
        <List>
          <ListItem disablePadding>
            <ListItemButton href='/' selected={ isRoot() } >
              <ListItemIcon><ArticleIcon /></ListItemIcon>
              <ListItemText primary="Protobuf" />
            </ListItemButton>
          </ListItem>
          <ListItem disablePadding>
            <ListItemButton href='/application' selected={ matchPath("/application") }  >
              <ListItemIcon><GridViewIcon /></ListItemIcon>
              <ListItemText primary="Application" />
            </ListItemButton>
          </ListItem>
          <ListItem disablePadding>
            <ListItemButton href='/group' selected={ matchPath("/group") } >
              <ListItemIcon><GroupIcon /></ListItemIcon>
              <ListItemText primary="Group" />
            </ListItemButton>
          </ListItem>
        </List>
      </Drawer>
    </Box>
  );
}