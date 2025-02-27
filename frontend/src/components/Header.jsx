import React, { useState } from 'react'
import { AppBar, Toolbar, IconButton, Typography, Box, Menu, MenuItem } from '@mui/material'
import { Link } from 'react-router-dom'
import { assets } from '../assets/asset'

const Header = () => {
  const [anchorEl, setAnchorEl] = useState(null)

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget)
  }

  const handleMenuClose = () => {
    setAnchorEl(null)
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
          <IconButton>
            <img src={assets.bell_ring} alt="bell" style={{ height: 24 }} />
          </IconButton>
          <IconButton>
            <img src={assets.account} alt="user" style={{ height: 24 }} />
          </IconButton>
          <IconButton onClick={handleMenuOpen}>
            <img src={assets.menu} alt="menu" style={{ height: 24 }} />
          </IconButton>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleMenuClose} sx={{ mt: 1 }}>
            <MenuItem component={Link} to="/myProfile" onClick={handleMenuClose}>
              My Profile
            </MenuItem>
            <MenuItem onClick={handleMenuClose}>Logout</MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  )
}

export default Header
