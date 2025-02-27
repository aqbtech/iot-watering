import React from 'react'
import {
  Box,
  Button,
  Checkbox,
  Container,
  Divider,
  FormControlLabel,
  Grid,
  Paper,
  TextField,
  Typography
} from '@mui/material'
import GoogleIcon from '@mui/icons-material/Google'
import FacebookIcon from '@mui/icons-material/Facebook'
import { Link } from 'react-router-dom'
const RegisterForm = () => {
  return (
    <Container component="main" maxWidth="sm" sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Paper elevation={3} sx={{ p: 4, borderRadius: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom>
                Create an Account
        </Typography>
        <Typography variant="body1" color="textSecondary" gutterBottom>
                Fill in the details below to register
        </Typography>

        <Box component="form" noValidate sx={{ mt: 3 }}>
          {/* Full Name */}
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField fullWidth label="First Name" variant="outlined" margin="normal" />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Middle & Last Name" variant="outlined" margin="normal" />
            </Grid>
          </Grid>

          {/* Email */}
          <TextField fullWidth label="Email" type="email" variant="outlined" margin="normal" />

          {/* Username */}
          <TextField fullWidth label="Username" variant="outlined" margin="normal" />

          {/* Phone Number */}
          <TextField fullWidth label="Phone Number" type="tel" variant="outlined" margin="normal" />

          {/* Date of Birth */}
          <TextField fullWidth label="Date of Birth" type="date" InputLabelProps={{ shrink: true }} variant="outlined" margin="normal" />

          {/* Password */}
          <TextField fullWidth label="Password" type="password" variant="outlined" margin="normal" />

          {/* Confirm Password */}
          <TextField fullWidth label="Confirm Password" type="password" variant="outlined" margin="normal" />

          {/* Terms & Conditions */}
          <FormControlLabel control={<Checkbox color="primary" />} label="I agree to the Terms and Conditions" />

          {/* Register Button */}
          <Button type="submit" fullWidth variant="contained" color="success" sx={{ mt: 2, py: 1.5 }}>
                    Register
          </Button>
        </Box>

        {/* Divider */}
        <Divider sx={{ my: 3 }}>Or</Divider>

        {/* Social Register */}
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Button fullWidth variant="outlined" startIcon={<GoogleIcon/>}>
                        Sign up with Google
            </Button>
          </Grid>
          <Grid item xs={6}>
            <Button fullWidth variant="outlined" startIcon={<FacebookIcon/>}>
                        Sign up with Facebook
            </Button>
          </Grid>
        </Grid>

        {/* Login Link */}
        <Typography variant="body2" align="center" sx={{ mt: 3 }}>
                Already have an account?{' '}
          <Typography
            component={Link} // Bọc Link trong Typography
            to="/login"
            color="primary"
            sx={{ fontWeight: 'bold', textDecoration: 'none', cursor: 'pointer' }}
          >
            Login
          </Typography>
        </Typography>
      </Paper>
    </Container>
  )
}

export default RegisterForm