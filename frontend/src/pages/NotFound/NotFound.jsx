import { Box, Button, Container, Typography } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline'
import HomeIcon from '@mui/icons-material/Home'

const NotFound = () => {
  const navigate = useNavigate()

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        background: 'linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 100%)'
      }}
    >
      <Container maxWidth="md">
        <Box
          sx={{
            textAlign: 'center',
            p: { xs: 3, md: 6 },
            background: 'rgba(255, 255, 255, 0.9)',
            borderRadius: 4,
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
            backdropFilter: 'blur(10px)'
          }}
        >
          <ErrorOutlineIcon
            sx={{
              fontSize: { xs: 100, md: 150 },
              color: '#2E7D32',
              mb: 2
            }}
          />

          <Typography
            variant="h1"
            sx={{
              fontSize: { xs: '4rem', md: '6rem' },
              fontWeight: 700,
              color: '#2E7D32',
              mb: 2
            }}
          >
            404
          </Typography>

          <Typography
            variant="h4"
            sx={{
              fontWeight: 600,
              color: '#1B5E20',
              mb: 2
            }}
          >
            Oops! Page Not Found
          </Typography>

          <Typography
            variant="body1"
            sx={{
              color: '#666',
              mb: 4,
              maxWidth: 600,
              mx: 'auto'
            }}
          >
            The page you are looking for might have been removed, had its name changed,
            or is temporarily unavailable.
          </Typography>

          <Button
            variant="contained"
            startIcon={<HomeIcon />}
            onClick={() => navigate('/')}
            sx={{
              bgcolor: '#2E7D32',
              '&:hover': { bgcolor: '#1B5E20' },
              px: 4,
              py: 1.5,
              borderRadius: 2,
              fontSize: '1.1rem'
            }}
          >
            Back to Home
          </Button>
        </Box>
      </Container>
    </Box>
  )
}

export default NotFound