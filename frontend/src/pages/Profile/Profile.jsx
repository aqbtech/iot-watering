import {
  Box,
  Container,
  Paper,
  Typography,
  Avatar,
  Button,
  Divider,
  TextField,
  Grid,
  IconButton,
  Tooltip
} from '@mui/material'
import EditIcon from '@mui/icons-material/Edit'
import SaveIcon from '@mui/icons-material/Save'
import CancelIcon from '@mui/icons-material/Cancel'
import Header from '../../components/Header'
import { useState, useEffect } from 'react'
import { getProfileAPI, updateProfileAPI } from '../../apis/deviceApi'
import { toast } from 'react-toastify'

const Profile = () => {
  const [isEditing, setIsEditing] = useState(false)
  const [profile, setProfile] = useState({
    firstName: 'Chinh',
    lastName: 'Tran',
    fullName: 'Chinh Tran',
    email: 'chinh.dang5504@hcmut.edu.vn',
    phone: '0123456789',
    username: 'chinh.dang5504',
    dateOfBirth: '2004-01-01'
  })
  const [editedProfile, setEditedProfile] = useState({ ...profile })
  const [errors, setErrors] = useState({})

  const getProfile = async () => {
    toast.promise(
      getProfileAPI(),
      {
        loading: 'Loading...',
        success: 'Profile loaded successfully'
      }
    ).then((response) => {
      setProfile(response)
    })
  }

  useEffect(() => {
    getProfile()
  }, [])

  const validateForm = () => {
    const newErrors = {}

    // Validate First Name
    if (!editedProfile.firstName.trim()) {
      newErrors.firstName = 'First name is required'
    } else if (editedProfile.firstName.length < 2) {
      newErrors.firstName = 'First name must be at least 2 characters'
    }

    // Validate Last Name
    if (!editedProfile.lastName.trim()) {
      newErrors.lastName = 'Last name is required'
    } else if (editedProfile.lastName.length < 2) {
      newErrors.lastName = 'Last name must be at least 2 characters'
    }

    // Validate Email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!editedProfile.email.trim()) {
      newErrors.email = 'Email is required'
    } else if (!emailRegex.test(editedProfile.email)) {
      newErrors.email = 'Invalid email format'
    }

    // Validate Phone
    const phoneRegex = /^[0-9]{10}$/
    if (!editedProfile.phone.trim()) {
      newErrors.phone = 'Phone number is required'
    } else if (!phoneRegex.test(editedProfile.phone)) {
      newErrors.phone = 'Phone number must be 10 digits'
    }

    // Validate Date of Birth
    if (!editedProfile.dateOfBirth) {
      newErrors.dateOfBirth = 'Date of birth is required'
    } else {
      const dob = new Date(editedProfile.dateOfBirth)
      const today = new Date()
      if (dob > today) {
        newErrors.dateOfBirth = 'Date of birth cannot be in the future'
      }
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleEdit = () => {
    setIsEditing(true)
    setEditedProfile({ ...profile })
    setErrors({})
  }

  const handleSave = () => {
    if (validateForm()) {
      toast.promise(
        updateProfileAPI(editedProfile),
        {
          loading: 'Updating...',
          success: 'Profile updated successfully'
        }
      ).then(() => {
        setProfile({ ...editedProfile })
        setIsEditing(false)
      })
    } else {
      toast.error('Please fix the errors before saving')
    }
  }

  const handleCancel = () => {
    setIsEditing(false)
    setEditedProfile({ ...profile })
    setErrors({})
  }

  const handleChange = (field) => (event) => {
    setEditedProfile({
      ...editedProfile,
      [field]: event.target.value
    })
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors({
        ...errors,
        [field]: ''
      })
    }
  }

  return (
    <>
      <Header />
      <Box
        sx={{
          minHeight: '100vh',
          background: 'linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 100%)',
          py: 4
        }}
      >
        <Container maxWidth="md">
          <Paper
            elevation={0}
            sx={{
              p: 4,
              borderRadius: 4,
              background: 'rgba(255, 255, 255, 0.9)',
              backdropFilter: 'blur(10px)'
            }}
          >
            {/* Header Section */}
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 4 }}>
              <Avatar
                sx={{
                  width: 120,
                  height: 120,
                  bgcolor: '#2E7D32',
                  fontSize: '3rem',
                  mr: 3
                }}
              >
                {profile.firstName.charAt(0) + profile.lastName.charAt(0)}
              </Avatar>
              <Box sx={{ flex: 1 }}>
                <Typography variant="h4" sx={{ color: '#2E7D32', fontWeight: 600, mb: 1 }}>
                  {profile.firstName + ' ' + profile.lastName}
                </Typography>
              </Box>
              {!isEditing ? (
                <Tooltip title="Edit Profile">
                  <IconButton
                    onClick={handleEdit}
                    sx={{
                      bgcolor: '#2E7D32',
                      color: 'white',
                      '&:hover': { bgcolor: '#1B5E20' }
                    }}
                  >
                    <EditIcon />
                  </IconButton>
                </Tooltip>
              ) : (
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <Tooltip title="Save Changes">
                    <IconButton
                      onClick={handleSave}
                      sx={{
                        bgcolor: '#2E7D32',
                        color: 'white',
                        '&:hover': { bgcolor: '#1B5E20' }
                      }}
                    >
                      <SaveIcon />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Cancel">
                    <IconButton
                      onClick={handleCancel}
                      sx={{
                        bgcolor: '#D32F2F',
                        color: 'white',
                        '&:hover': { bgcolor: '#9A0007' }
                      }}
                    >
                      <CancelIcon />
                    </IconButton>
                  </Tooltip>
                </Box>
              )}
            </Box>

            <Divider sx={{ my: 3 }} />

            {/* Profile Form */}
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="First Name"
                  value={isEditing ? editedProfile.firstName : profile.firstName}
                  onChange={handleChange('firstName')}
                  disabled={!isEditing}
                  error={!!errors.firstName}
                  helperText={errors.firstName}
                  sx={{ mb: 2 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Last Name"
                  value={isEditing ? editedProfile.lastName : profile.lastName}
                  onChange={handleChange('lastName')}
                  disabled={!isEditing}
                  error={!!errors.lastName}
                  helperText={errors.lastName}
                  sx={{ mb: 2 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  type="email"
                  fullWidth
                  label="Email"
                  value={isEditing ? editedProfile.email : profile.email}
                  onChange={handleChange('email')}
                  disabled={!isEditing}
                  error={!!errors.email}
                  helperText={errors.email}
                  sx={{ mb: 2 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Phone"
                  value={isEditing ? editedProfile.phone : profile.phone}
                  onChange={handleChange('phone')}
                  disabled={!isEditing}
                  error={!!errors.phone}
                  helperText={errors.phone}
                  sx={{ mb: 2 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  type="date"
                  fullWidth
                  label="Date of Birth"
                  value={isEditing ? editedProfile.dateOfBirth : profile.dateOfBirth}
                  onChange={handleChange('dateOfBirth')}
                  disabled={!isEditing}
                  error={!!errors.dateOfBirth}
                  helperText={errors.dateOfBirth}
                  InputLabelProps={{
                    shrink: true
                  }}
                  sx={{ mb: 2 }}
                />
              </Grid>
            </Grid>
          </Paper>
        </Container>
      </Box>
    </>
  )
}

export default Profile