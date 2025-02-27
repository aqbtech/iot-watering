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
    <Box sx={{ minHeight: '100vh', display: 'flex' }}>
      {/* Cột chứa LoginForm */}
      <Box
        sx={{
          flex: 1,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          bgcolor: 'blue.50',
          p: 4
        }}
      >
        {isLogin && <LoginForm />}
        {isRegister && <RegisterForm />}
      </Box>

      {/* Cột chứa hình ảnh */}
      <Box
        sx={{
          flex: 1,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          height: '100vh', // Giới hạn chiều cao tối đa bằng chiều cao màn hình
          overflow: 'hidden' // Ngăn tràn nội dung nếu có
        }}
      >
        <Box
          component="img"
          src={assets.backgroundAuth}
          alt="background"
          sx={{
            width: '100%',
            maxHeight: '100vh', // Giới hạn chiều cao tối đa bằng chiều cao màn hình
            objectFit: 'cover', // Giữ đúng tỉ lệ hình ảnh mà không bị méo
            borderRadius: '80px 0px 0px 80px'
          }}
        />
      </Box>
    </Box>
  )
}

export default Auth
