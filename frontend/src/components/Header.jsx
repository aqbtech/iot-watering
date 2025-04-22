import React, { useState } from 'react'
import { AppBar, Toolbar, IconButton, Typography, Box, Menu, MenuItem } from '@mui/material'
import { Link, useNavigate } from 'react-router-dom'
import { assets } from '../assets/asset'
import { toast } from 'react-toastify'
import { useDispatch } from 'react-redux'
import { logoutUserAPI } from '../redux/Slices/userSlice'
import { useSelector } from 'react-redux'
import { selectCurrentUser, updateCurrentUser } from '../redux/Slices/userSlice'

const Header = () => {
  const [anchorEl, setAnchorEl] = useState(null)
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const currentUser = useSelector(selectCurrentUser)

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget)
  }

  const handleMenuClose = () => {
    setAnchorEl(null)
  }

  const handleLogout = () => {
    toast.promise(dispatch(logoutUserAPI(currentUser.token)), {
      pending: 'Logging out...'
    }).finally(() => {
      dispatch(updateCurrentUser(null))
      navigate('/Login')
    })
  }

  return (
    <AppBar position="static" sx={{ backgroundColor: 'white', color: 'black', boxShadow: 1 }}>
      <Toolbar sx={{ display: 'flex', justifyContent: 'space-between' }}>
        <Box component={Link} to="/" sx={{ display: 'flex', alignItems: 'center', textDecoration: 'none', color: 'inherit' }}>
          <img src={assets.logo} alt="logo" style={{ height: 40 }} />
          <Typography variant="h6">
            SmartPlant
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <IconButton onClick={handleMenuOpen}>
            <img src={assets.account} alt="user" style={{ height: 24 }} />
          </IconButton>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleMenuClose} sx={{ mt: 1 }}>
            <MenuItem component={Link} to="/Profile" onClick={handleMenuClose}>
              My Profile
            </MenuItem>
            <MenuItem onClick={handleLogout}>Logout</MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  )
}

export default Header
