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
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as yup from 'yup'
import { toast } from 'react-toastify'
import { registerUserAPI } from '../../apis/deviceApi.js'

// 🛠 Xây dựng schema validation với Yup
const schema = yup.object({
  firstName: yup.string().required('First name is required.'),
  lastName: yup.string().required('Last name is required.'),
  email: yup.string().matches(/^\S+@\S+\.\S+$/, 'Email is invalid.').required('Email is required.'),
  username: yup.string().min(8, 'Username must be at least 8 characters.').max(20, 'Username cannot exceed 20 characters.').required('Username is required.'),
  phoneNumber: yup.string().matches(/^[0-9]{10,11}$/, 'Phone number is invalid.'),
  dob: yup.string().required('Date of birth is required.'),
  password: yup.string().min(8, 'Password must be at least 8 characters.').matches(/^(?=.*[a-zA-Z])(?=.*\d)/, 'Password must include letters and numbers.').required('Password is required.'),
  confirmPassword: yup.string().oneOf([yup.ref('password')], 'Passwords must match.').required('Confirm password is required.'),
  terms: yup.bool().oneOf([true], 'You must agree to the terms.')
})

const RegisterForm = () => {
  const navigate = useNavigate()

  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm({
    resolver: yupResolver(schema)
  })

  const onSubmit = (data) => {
    toast.promise(
      registerUserAPI(data),
      {
        pending: 'Registration is in progress...'
      }
    ).then(user => {
      navigate('/Login')
    })
  }

  return (
    <Container component="main" maxWidth="sm" sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Paper elevation={3} sx={{ p: 4, borderRadius: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Create an Account
        </Typography>
        <Typography variant="body1" color="textSecondary" gutterBottom>
          Fill in the details below to register
        </Typography>

        <Box component="form" noValidate sx={{ mt: 3 }} onSubmit={handleSubmit(onSubmit)}>
          {/* Full Name */}
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField fullWidth label="First Name" variant="outlined" margin="normal" {...register('firstName')} error={!!errors.firstName} helperText={errors.firstName?.message} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Last Name" variant="outlined" margin="normal" {...register('lastName')} error={!!errors.lastName} helperText={errors.lastName?.message} />
            </Grid>
          </Grid>

          {/* Email */}
          <TextField fullWidth label="Email" type="email" variant="outlined" margin="normal" {...register('email')} error={!!errors.email} helperText={errors.email?.message} />

          {/* Username */}
          <TextField fullWidth label="Username" variant="outlined" margin="normal" {...register('username')} error={!!errors.username} helperText={errors.username?.message} />

          {/* Phone Number */}
          <TextField fullWidth label="Phone Number" type="tel" variant="outlined" margin="normal" {...register('phoneNumber')} error={!!errors.phoneNumber} helperText={errors.phoneNumber?.message} />

          {/* Date of Birth */}
          <TextField fullWidth label="Date of Birth" type="date" InputLabelProps={{ shrink: true }} variant="outlined" margin="normal" {...register('dob')} error={!!errors.dob} helperText={errors.dob?.message} />

          {/* Password */}
          <TextField fullWidth label="Password" type="password" variant="outlined" margin="normal" {...register('password')} error={!!errors.password} helperText={errors.password?.message} />

          {/* Confirm Password */}
          <TextField fullWidth label="Confirm Password" type="password" variant="outlined" margin="normal" {...register('confirmPassword')} error={!!errors.confirmPassword} helperText={errors.confirmPassword?.message} />

          {/* Terms & Conditions */}
          <FormControlLabel control={<Checkbox {...register('terms')} color="primary" />} label="I agree to the Terms and Conditions" />
          <Typography variant="body2" color="error">{errors.terms?.message}</Typography>

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
            <Button fullWidth variant="outlined" startIcon={<GoogleIcon />}>
              Sign up with Google
            </Button>
          </Grid>
          <Grid item xs={6}>
            <Button fullWidth variant="outlined" startIcon={<FacebookIcon />}>
              Sign up with Facebook
            </Button>
          </Grid>
        </Grid>

        {/* Login Link */}
        <Typography variant="body2" align="center" sx={{ mt: 3 }}>
          Already have an account?{' '}
          <Typography component={Link} to="/Login" color="primary" sx={{ fontWeight: 'bold', textDecoration: 'none', cursor: 'pointer' }}>
            Login
          </Typography>
        </Typography>
      </Paper>
    </Container>
  )
}

export default RegisterForm
