import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { IconButton, Stack, Tooltip, Typography } from "@mui/material";


function BackTitle({path, title}) {
  return ( 
    <Stack component="section" direction="row" alignItems="center" spacing={2} sx={{ml: 2}}>
      <Tooltip title={"Back to " + path}>
        <IconButton href={path} color='primary'>
          <ArrowBackIcon />
        </IconButton>
      </Tooltip>

      <Typography variant='h6' color="GrayText">
        {title}
      </Typography>
    </Stack> 
  );
}

export default BackTitle;