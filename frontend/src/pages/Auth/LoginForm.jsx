import React from 'react'
import { TextField, Checkbox, FormControlLabel, Button, Typography, Divider, Box, Container, Grid, Paper } from '@mui/material'
import { assets } from '../../assets/asset.js'
import FacebookIcon from '@mui/icons-material/Facebook'
import GoogleIcon from '@mui/icons-material/Google'
import { Link } from 'react-router-dom'
const LoginForm = () => {
  return (
    <Container component="main" maxWidth="sm" sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Paper elevation={3} sx={{ p: 4, borderRadius: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom>
            Welcome back!
        </Typography>
        <Typography variant="body1" color="textSecondary" gutterBottom>
            Enter your credentials to access your account
        </Typography>

        <Box component="form" noValidate sx={{ mt: 3 }}>
          {/* Username */}
          <TextField
            fullWidth
            label="Username"
            variant="outlined"
            margin="normal"
            placeholder="Enter your email"
          />

          {/* Password */}
          <TextField
            fullWidth
            label="Password"
            type="password"
            variant="outlined"
            margin="normal"
            placeholder="Enter your password"
          />

          {/* Remember & Forgot Password */}
          <Box display="flex" justifyContent="space-between" alignItems="center" sx={{ mt: 1 }}>
            <FormControlLabel
              control={<Checkbox color="primary" />}
              label="Remember for 30 days"
            />
            <Typography variant="body2" color="primary" sx={{ cursor: 'pointer' }}>
                Forgot password?
            </Typography>
          </Box>

          {/* Login Button */}
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="success"
            sx={{ mt: 2, py: 1.5 }}
          >
              Login
          </Button>
        </Box>

        {/* Divider */}
        <Divider sx={{ my: 3 }}>Or</Divider>

        {/* Social Login */}
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Button
              fullWidth
              variant="outlined"
              startIcon={<GoogleIcon/>}
            >
                Sign in with Google
            </Button>
          </Grid>
          <Grid item xs={6}>
            <Button
              fullWidth
              variant="outlined"
              startIcon={<FacebookIcon/>}
            >
                Sign in with Facebook
            </Button>
          </Grid>
        </Grid>

        {/* Sign Up Link */}
        <Typography variant="body2" align="center" sx={{ mt: 3 }}>
            Don't have an account?{' '}
          <Typography
            component={Link} // Bọc Link trong Typography
            to="/signup"
            color="primary"
            sx={{ fontWeight: 'bold', textDecoration: 'none', cursor: 'pointer' }}
          >
                Sign up
          </Typography>
        </Typography>
      </Paper>
    </Container>
  )
}

export default LoginForm