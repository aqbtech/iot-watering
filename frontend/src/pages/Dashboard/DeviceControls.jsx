import { Box, Button, Typography, IconButton, Tooltip } from '@mui/material'
import WaterDropIcon from '@mui/icons-material/WaterDrop'
import AirIcon from '@mui/icons-material/Air'
import LightbulbIcon from '@mui/icons-material/Lightbulb'
import NotificationsIcon from '@mui/icons-material/Notifications'
import SettingsIcon from '@mui/icons-material/Settings'

const DeviceControls = ({
  pumpStatus,
  fanStatus,
  lightStatus,
  buzzerStatus,
  onTogglePump,
  onToggleFan,
  onToggleLight,
  onToggleSiren,
  onOpenConfig
}) => {
  const ControlButton = ({
    title,
    status,
    icon: Icon,
    onClick,
    color = '#2E7D32',
    hoverColor = '#1B5E20'
  }) => (
    <Button
      variant="contained"
      sx={{
        width: { xs: '100%', sm: 200 },
        height: 80,
        fontSize: 16,
        fontWeight: 'bold',
        bgcolor: status ? color : '#D32F2F',
        color: 'white',
        borderRadius: 2,
        display: 'flex',
        flexDirection: 'column',
        gap: 1,
        '&:hover': {
          bgcolor: status ? hoverColor : '#9A0007'
        }
      }}
      onClick={onClick}
    >
      <Icon sx={{ fontSize: 28 }} />
      <Typography variant="body2">{title}</Typography>
      <Typography variant="caption" sx={{ opacity: 0.8 }}>
        {status ? 'ON' : 'OFF'}
      </Typography>
    </Button>
  )

  return (
    <Box sx={{
      display: 'flex',
      flexDirection: 'column',
      gap: 3,
      width: '100%',
      p: 3,
      background: 'linear-gradient(to bottom, #E8F5E9, #ffffff)',
      borderRadius: 4,
      boxShadow: '0 4px 12px rgba(0,0,0,0.05)'
    }}>
      <Box sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        mb: 2
      }}>
        <Typography variant="h6" sx={{ color: '#2E7D32', fontWeight: 600 }}>
          Device Controls
        </Typography>
        <Tooltip title="Configure Auto Settings">
          <IconButton
            onClick={onOpenConfig}
            sx={{
              bgcolor: '#2E7D32',
              color: 'white',
              '&:hover': { bgcolor: '#1B5E20' }
            }}
          >
            <SettingsIcon />
          </IconButton>
        </Tooltip>
      </Box>

      <Box sx={{
        display: 'grid',
        gridTemplateColumns: {
          xs: '1fr',
          sm: 'repeat(2, 1fr)',
          md: 'repeat(4, 1fr)',
          lg: 'repeat(4, 1fr)'
        },
        gap: 2,
        width: '100%',
        justifyContent: 'space-between'
      }}>
        <ControlButton
          title="Water Pump"
          status={pumpStatus}
          icon={WaterDropIcon}
          onClick={onTogglePump}
        />
        <ControlButton
          title="Fan"
          status={fanStatus}
          icon={AirIcon}
          onClick={onToggleFan}
        />
        <ControlButton
          title="Light"
          status={lightStatus}
          icon={LightbulbIcon}
          onClick={onToggleLight}
        />
        <ControlButton
          title="Siren"
          status={buzzerStatus}
          icon={NotificationsIcon}
          onClick={onToggleSiren}
        />
      </Box>
    </Box>
  )
}

export default DeviceControls