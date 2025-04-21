import {
  Box,
  Typography,
  Table,
  List,
  ListItem,
  Container
} from '@mui/material'
import HistoryTable from './HistoryTable'
import Header from '../../components/Header'
import PageLoadingSpinner from '../../components/PageLoadingSpinner'
import { useEffect, useState } from 'react'
import { selectedCurrentActiveDashboard, fetchDetailDashboard, updateState } from '../../redux/Slices/dashboardSlice'
import { useDispatch, useSelector } from 'react-redux'
import { useParams } from 'react-router-dom'
import DataField from './DataField'
import DeviceControls from './DeviceControls'
import ConfigModal from './ConfigModal'
import { triggerAPI, triggerFanAPI, triggerLightAPI, triggerPumpAPI, triggerSirenAPI, setConfigAPI } from '../../apis/deviceApi'
import SpaIcon from '@mui/icons-material/Spa'
import { toast } from 'react-toastify'
const Dashboard = () => {
  const dispatch = useDispatch()
  const { deviceId } = useParams()
  const [configModalOpen, setConfigModalOpen] = useState(false)
  // Lấy dữ liệu từ Redux
  const dashboard = useSelector(selectedCurrentActiveDashboard)


  useEffect(() => {
    dispatch(fetchDetailDashboard(deviceId))
  }, [dispatch, deviceId])

  if (!dashboard) {
    return <PageLoadingSpinner caption="Loading Dashboard..."/>
  }

  const handleToggleFan = async () => {
    const response = await triggerFanAPI(deviceId)
    dispatch(updateFan(dashboard.fan === 'active' ? 'inactive' : 'active'))
    return response
  }

  const handleToggleLight = async () => {
    const response = await triggerLightAPI(deviceId)
    dispatch(updateLight(dashboard.light === 'active' ? 'inactive' : 'active'))
    return response
  }

  const handleTogglePump = async () => {
    const response = await triggerPumpAPI(deviceId)
    dispatch(updatePump(dashboard.pump === 'active' ? 'inactive' : 'active'))
    return response
  }

  const handleToggleSiren = async () => {
    const response = await triggerSirenAPI(deviceId)
    dispatch(updateBuzzer(dashboard.buzzer === 'active' ? 'inactive' : 'active'))
    return response
  }


  return (
    <>
      <Header/>
      <Box sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 100%)',
        pb: 6
      }}>
        <Container maxWidth="lg" sx={{ pt: 4 }}>
          <Box sx={{
            display: 'flex',
            alignItems: 'center',
            gap: 2,
            mb: 4
          }}>
            <SpaIcon sx={{ fontSize: 40, color: '#2E7D32' }} />
            <Typography variant="h4" sx={{
              fontWeight: 600,
              color: '#2E7D32',
              textShadow: '0 1px 2px rgba(0,0,0,0.1)'
            }}>
              {dashboard?.name}
            </Typography>
          </Box>

          <Box sx={{
            display: 'flex',
            flexDirection: 'column',
            gap: 3,
            width: '100%'
          }}>
            <Box sx={{ width: '100%' }}>
              <DeviceControls
                pumpStatus={dashboard.pump === 'active'}
                fanStatus={dashboard.fan === 'active'}
                lightStatus={dashboard.light === 'active'}
                buzzerStatus={dashboard.buzzer === 'active'}
                onTogglePump={handleTogglePump}
                onToggleFan={handleToggleFan}
                onToggleLight={handleToggleLight}
                onToggleSiren={handleToggleSiren}
                onOpenConfig={() => setConfigModalOpen(true)}
              />
            </Box>

            <Box sx={{ width: '100%' }}>
              <DataField deviceId={deviceId}/>
            </Box>

            <Box sx={{ width: '100%' }}>
              <HistoryTable deviceId={deviceId}/>
            </Box>
          </Box>
        </Container>

        <ConfigModal
          open={configModalOpen}
          onClose={() => setConfigModalOpen(false)}
          deviceId={deviceId}
        />
      </Box>
    </>
  )
}

export default Dashboard
