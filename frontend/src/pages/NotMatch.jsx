import { Container, Typography } from '@mui/material';
import { Box } from '@mui/system';
import MainPart from '../layout/MainPart';

function NotMatch() {
  return (
    <MainPart>
      <Box sx={ { height: 500, display: "flex", alignItems: "center" } }>
        <Container>
          <Typography variant='h3' align="center">
            404 Not Match
          </Typography>

          <Typography variant='h4' align="center">
            没有相关页面!
          </Typography>
        </Container>
      </Box>
    </MainPart>
  );
}

export default NotMatch;