import { useEffect, useState } from 'react'
import {
  Box,
  Button,
  Typography,
  IconButton,
  Tooltip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material'
import WaterDropIcon from '@mui/icons-material/WaterDrop'
import AirIcon from '@mui/icons-material/Air'
import LightbulbIcon from '@mui/icons-material/Lightbulb'
import SettingsIcon from '@mui/icons-material/Settings'
import { getStatusConfigAPI, setStatusConfigAPI } from '../../apis/deviceApi.js'
import { toast } from 'react-toastify'

const DeviceControls = ({
  pumpStatus,
  fanStatus,
  lightStatus,
  onTogglePump,
  onToggleFan,
  onToggleLight,
  onOpenConfig,
  deviceId
}) => {
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [currentAction, setCurrentAction] = useState(null)
  const [active, setActive] = useState(false)

  useEffect(() => {
    const getStatusConfig = async () => {
      try {
        const res = await toast.promise(
          getStatusConfigAPI(deviceId),
          {
            pending: 'Downloading configuration of device',
            success: 'Download successfully!'
          }
        )
        setActive(res)
      } catch (err) {
        console.error('Lỗi khi gọi API getStatusConfig:', err)
      }
    }

    if (deviceId) {
      getStatusConfig()
    }
  }, [deviceId])

  const handleToggleMode = async () => {
    try {
      const newStatus = !active
      await toast.promise(
        setStatusConfigAPI(deviceId, newStatus),
        {
          pending: 'Updating mode...',
          success: `Switched to ${newStatus ? 'Auto' : 'Manual'} mode`
        }
      )
      setActive(newStatus)
    } catch (error) {
      console.error('Lỗi khi gọi API setStatusConfig:', error)
    }
  }

  const handleOpenConfirm = (action) => {
    setCurrentAction(() => action)
    setConfirmOpen(true)
  }

  const handleConfirm = () => {
    if (currentAction) currentAction()
    setConfirmOpen(false)
  }

  const handleCancel = () => {
    setConfirmOpen(false)
    setCurrentAction(null)
  }

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
        height: 90,
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
      onClick={() => handleOpenConfirm(onClick)}
    >
      <Icon sx={{ fontSize: 28, mt: 1 }}/>
      <Typography variant="body2">{title}</Typography>
      <Typography variant="caption" sx={{ opacity: 0.8, mt: 1 }}>
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
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Tooltip title={`Switch to ${active ? 'Manual' : 'Auto'} Mode`}>
            <Button
              variant="contained"
              onClick={handleToggleMode}
              sx={{
                bgcolor: active ? '#2E7D32' : '#D32F2F',
                color: 'white',
                textTransform: 'none',
                '&:hover': {
                  bgcolor: active ? '#1B5E20' : '#9A0007'
                }
              }}
            >
              {active ? 'Auto Mode' : 'Manual Mode'}
            </Button>
          </Tooltip>
          {active && <Tooltip title="Configure Auto Settings">
            <IconButton
              onClick={onOpenConfig}
              sx={{
                bgcolor: '#2E7D32',
                color: 'white',
                '&:hover': { bgcolor: '#1B5E20' }
              }}
            >
              <SettingsIcon/>
            </IconButton>
          </Tooltip>
          }
        </Box>
      </Box>


      <Box sx={{
        display: 'grid',
        gridTemplateColumns: {
          xs: '1fr',
          sm: 'repeat(2, 1fr)',
          md: 'repeat(3, 1fr)'
        },
        gap: 2,
        width: '100%',
        justifyItems: 'center' // căn phần tử theo chiều ngang ở giữa ô grid
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
      </Box>

      {/* Confirmation Dialog */}
      <Dialog
        open={confirmOpen}
        onClose={handleCancel}
      >
        <DialogTitle>Confirm Action</DialogTitle>
        <DialogContent>
          <DialogContentText>
                        Are you sure you want to perform this action?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancel} sx={{ color: '#D32F2F' }}>
                        Cancel
          </Button>
          <Button onClick={handleConfirm} sx={{ color: '#2E7D32' }} autoFocus>
                        Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default DeviceControls