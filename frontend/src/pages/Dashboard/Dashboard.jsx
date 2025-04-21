import {
  Box,
  Typography,
  Container
} from '@mui/material'
import HistoryTable from './HistoryTable'
import Header from '../../components/Header'
import PageLoadingSpinner from '../../components/PageLoadingSpinner'
import { useEffect, useState } from 'react'
import { selectedCurrentActiveDashboard, fetchDetailDashboard } from '../../redux/Slices/dashboardSlice'
import { useDispatch, useSelector } from 'react-redux'
import { useParams } from 'react-router-dom'
import DataField from './DataField'
import DeviceControls from './DeviceControls'
import ConfigModal from './ConfigModal'
import {
  triggerFanAPI,
  triggerLightAPI,
  triggerPumpAPI
} from '../../apis/deviceApi'
import SpaIcon from '@mui/icons-material/Spa'
import { toast } from 'react-toastify'
import { updateFan, updatePump, updateLight } from '../../redux/Slices/dashboardSlice'

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
    const data = {
      deviceId: deviceId,
      state: dashboard.fan === '1' ? false : true
    }
    toast.promise(triggerFanAPI(data), {
      pending: 'Turning on the fan...'
    }
    ).then(() => {
      dispatch(updateFan(dashboard.fan === '1' ? '0' : '1'))
    })
  }

  const handleToggleLight = async () => {
    const data = {
      deviceId: deviceId,
      state: dashboard.light === '1' ? false : true
    }
    toast.promise(triggerLightAPI(data), {
      pending: 'Turning on the light...'
    }
    ).then(() => {
      dispatch(updateLight(dashboard.light === '1' ? '0' : '1'))
    })
  }

  const handleTogglePump = async () => {
    const data = {
      deviceId: deviceId,
      state: dashboard.pump === '1' ? false : true
    }
    toast.promise(triggerPumpAPI(data), {
      pending: 'Turning on the pump...'
    }
    ).then(() => {
      dispatch(updatePump(dashboard.pump === '1' ? '0' : '1'))
    })
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
            <SpaIcon sx={{ fontSize: 40, color: '#2E7D32' }}/>
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
                pumpStatus={dashboard.pump === '1'}
                fanStatus={dashboard.fan === '1'}
                lightStatus={dashboard.light === '1'}
                onTogglePump={handleTogglePump}
                onToggleFan={handleToggleFan}
                onToggleLight={handleToggleLight}
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