import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  Typography,
  Divider
} from '@mui/material'
import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { updateConfig } from '../../redux/Slices/dashboardSlice'
import { setConfigAPI } from '../../apis/deviceApi'
import { toast } from 'react-toastify'
import { selectedCurrentActiveDashboard } from '../../redux/Slices/dashboardSlice'

const ConfigModal = ({ open, onClose, deviceId }) => {
  const dashboard = useSelector(selectedCurrentActiveDashboard)
  const dispatch = useDispatch()

  const [temperature, setTemperature] = useState(dashboard.configPump || 0)
  const [humidity, setHumidity] = useState(dashboard.configFan || 0)
  const [soilMoisture, setSoilMoisture] = useState(dashboard.configSiren || 0)
  const [light, setLight] = useState(dashboard.configLight || 0)

  const handleChange = (value, setValue) => {
    if (value < 0) {
      setValue(0)
      return
    }
    if (value > 100) {
      setValue(100)
      return
    }
    setValue(value)
  }

  const handleSaveConfig = async () => {
    const data = {
      temperature: temperature,
      humidity: humidity,
      soilMoisture: soilMoisture,
      light: light,
      deviceId: deviceId
    }

    await toast.promise(
      setConfigAPI(data),
      {
        loading: 'Saving config...',
        success: 'Config saved successfully'
      }
    ).then(() => {
      dispatch(updateConfig(data))
      onClose()
    })
  }

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 2,
          background: 'linear-gradient(to bottom, #ffffff, #f5f5f5)'
        }
      }}
    >
      <DialogTitle sx={{
        bgcolor: '#2E7D32',
        color: 'white',
        fontWeight: 600
      }}>
        Auto Control Configuration
      </DialogTitle>
      <DialogContent sx={{ mt: 2 }}>
        <Box>
          <Typography variant="subtitle1" sx={{ mb: 1, color: '#2E7D32', fontWeight: 500 }}>
            Temperature Control
          </Typography>
          <TextField
            label="°C"
            type="number"
            value={temperature}
            onChange={(e) => handleChange(e.target.value, setTemperature)}
            fullWidth
            size="small"
          />
        </Box>
        <Divider sx={{ my: 2 }} />
        <Box>
          <Typography variant="subtitle1" sx={{ mb: 1, color: '#2E7D32', fontWeight: 500 }}>
            Humidity Control
          </Typography>
          <TextField
            label="%"
            type="number"
            value={humidity}
            onChange={(e) => handleChange(e.target.value, setHumidity)}
            fullWidth
            size="small"
          />
        </Box>
        <Divider sx={{ my: 2 }} />
        <Box>
          <Typography variant="subtitle1" sx={{ mb: 1, color: '#2E7D32', fontWeight: 500 }}>
            Soil Moisture Control
          </Typography>
          <TextField
            label="%"
            type="number"
            value={soilMoisture}
            onChange={(e) => handleChange(e.target.value, setSoilMoisture)}
            fullWidth
            size="small"
          />
        </Box>
        <Divider sx={{ my: 2 }} />
        <Box>
          <Typography variant="subtitle1" sx={{ mb: 1, color: '#2E7D32', fontWeight: 500 }}>
            Light Control
          </Typography>
          <TextField
            label="lux"
            type="number"
            value={light}
            onChange={(e) => handleChange(e.target.value, setLight)}
            fullWidth
            size="small"
          />
        </Box>
      </DialogContent>
      <DialogActions sx={{ p: 3, gap: 1 }}>
        <Button
          onClick={onClose}
          variant="outlined"
          sx={{
            color: '#2E7D32',
            borderColor: '#2E7D32',
            '&:hover': {
              borderColor: '#1B5E20',
              bgcolor: 'rgba(46, 125, 50, 0.04)'
            }
          }}
        >
          Cancel
        </Button>
        <Button
          onClick={handleSaveConfig}
          variant="contained"
          sx={{
            bgcolor: '#2E7D32',
            '&:hover': { bgcolor: '#1B5E20' }
          }}
        >
          Save Changes
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default ConfigModal