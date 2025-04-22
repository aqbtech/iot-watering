import { Box, Card, Typography } from '@mui/material'
import React from 'react'
import ThermostatOutlinedIcon from '@mui/icons-material/ThermostatOutlined'


const DataFrame = ({ name, value, icon: Icon }) => {
  return (
    <Card sx={{
      height: 200,
      width: '80%',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      p: 2,
      background: '#2E7D32',
      borderRadius: 4
    }}>
      <Typography sx={{ fontSize: 16, color: 'white', fontWeight: 500 }}>
        {name}
      </Typography>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, marginTop: 2, justifyContent: 'space-between' }}>
        <Typography sx={{ fontSize: 24, fontWeight: 500, color: 'white' }}>
          {value}
        </Typography>
        <Card sx={{
          height: 140,
          width: 80,
          background: '#66BB69',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          borderRadius: 2
        }}>
          {Icon && <Icon sx={{ fontSize: 40, color: 'white' }} />}
        </Card>
      </Box>
    </Card>


  )
}

export default DataFrame
