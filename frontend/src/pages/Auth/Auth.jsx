import { Box } from '@mui/material'
import { assets } from '../../assets/asset'
import LoginForm from './LoginForm.jsx'
import RegisterForm from './RegisterForm.jsx'
import { useLocation } from 'react-router-dom'

const Auth = () => {
  const location = useLocation()
  const isLogin = location.pathname === '/Login'
  const isRegister = location.pathname === '/Signup'

  return (
    <Box sx={{
      maxHeight: '100vh',
      display: 'flex',
      overflow: 'hidden',
      background: 'linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 100%)'
    }}>
      {/* Form Column */}
      <Box
        sx={{
          flex: 1,
          display: 'flex',
          alignItems: 'flex-start',
          justifyContent: 'center',
          px: { xs: 2, sm: 4, md: 6 },
          overflow: 'auto',
          '&::-webkit-scrollbar': {
            width: '8px'
          },
          '&::-webkit-scrollbar-track': {
            background: 'rgba(0,0,0,0.1)',
            borderRadius: '4px'
          },
          '&::-webkit-scrollbar-thumb': {
            background: '#2E7D32',
            borderRadius: '4px',
            '&:hover': {
              background: '#1B5E20'
            }
          }
        }}
      >
        <Box sx={{
          width: '100%',
          maxWidth: 600,
          mt: 2
        }}>
          {isLogin && <LoginForm />}
          {isRegister && <RegisterForm />}
        </Box>
      </Box>

      {/* Image Column */}
      <Box
        sx={{
          flex: 1,
          display: { xs: 'none', md: 'flex' },
          alignItems: 'center',
          justifyContent: 'center',
          height: '100vh',
          overflow: 'hidden',
          position: 'relative'
        }}
      >
        <Box
          component="img"
          src={assets.backgroundAuth}
          alt="background"
          sx={{
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            borderRadius: '80px 0 0 80px'
          }}
        />
      </Box>
    </Box>
  )
}

export default Auth
