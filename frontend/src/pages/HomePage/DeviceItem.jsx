import { Box, Card, CardContent, Stack, Typography, Button, Chip } from '@mui/material'
import WaterDropOutlinedIcon from '@mui/icons-material/WaterDropOutlined'
import WbSunnyOutlinedIcon from '@mui/icons-material/WbSunnyOutlined'
import SpaOutlinedIcon from '@mui/icons-material/SpaOutlined'
import ThermostatOutlinedIcon from '@mui/icons-material/ThermostatOutlined'
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline'
import VisibilityIcon from '@mui/icons-material/Visibility'
import { Link } from 'react-router-dom'

const DeviceItem = ({ device, onDelete }) => {
  const { name, location, temperature, light, humidity, soilMoisture, status } = device

  return (
    <Card
      sx={{
        width: 300,
        padding: 2,
        borderRadius: 4,
        boxShadow: '0px 4px 10px rgba(0, 0, 0, 0.1)',
        background: '#FFFFFF'
      }}
    >
      <CardContent>
        {/* Tiêu đề thiết bị */}
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="h6" fontWeight="bold">{name}</Typography>
          <Chip
            label={status === 'active' ? 'Enable' : 'Disable'}
            color={status === 'active' ? 'success' : 'error'}
            size="small"
          />
        </Stack>

        {/* Địa điểm */}
        <Typography variant="body2" color="text.secondary" mb={2}>
          {location}
        </Typography>

        {/* Thông tin cảm biến */}
        <Stack direction="row" justifyContent="space-between">
          <Box display="flex" alignItems="center" gap={0.5}>
            <ThermostatOutlinedIcon color="primary" />
            <Typography variant="body2">{temperature}°C</Typography>
          </Box>
          <Box display="flex" alignItems="center" gap={0.5}>
            <WbSunnyOutlinedIcon color="warning" />
            <Typography variant="body2">{light} Lux</Typography>
          </Box>
        </Stack>
        <Stack direction="row" justifyContent="space-between" mt={1}>
          <Box display="flex" alignItems="center" gap={0.5}>
            <WaterDropOutlinedIcon color="info" />
            <Typography variant="body2">{humidity}%</Typography>
          </Box>
          <Box display="flex" alignItems="center" gap={0.5}>
            <SpaOutlinedIcon color="success" />
            <Typography variant="body2">{soilMoisture}%</Typography>
          </Box>
        </Stack>

        {/* Nút thao tác */}
        <Stack direction="row" justifyContent="space-between" mt={2}>
          <Button
            variant="outlined"
            color="error"
            size="small"
            startIcon={<DeleteOutlineIcon />}
            onClick={() => onDelete(device)}
          >
            Xóa
          </Button>
          <Button
            variant="contained"
            color="primary"
            size="small"
            startIcon={<VisibilityIcon />}
            component={Link}
            to={`/Dashboard/${device.deviceId}`}
          >
            Xem chi tiết
          </Button>
        </Stack>
      </CardContent>
    </Card>
  )
}

export default DeviceItem
