import AppBar from '@mui/material/AppBar'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'



export default function AppHeader(spring) {
  return (
    <AppBar position="fixed" color="primary" component='header'>
      <Toolbar variant='regular'>
        <Typography variant="h4" flexGrow={1} align="center">
          Data Faker
        </Typography>
        <Typography variant="h6">
          Github
        </Typography>
      </Toolbar>
    </AppBar>
  )
}