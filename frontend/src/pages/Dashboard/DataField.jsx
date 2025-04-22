import { Box, Typography } from '@mui/material'
import { useEffect, useState } from 'react'
import WaterDropOutlinedIcon from '@mui/icons-material/WaterDropOutlined'
import WbSunnyOutlinedIcon from '@mui/icons-material/WbSunnyOutlined'
import SpaOutlinedIcon from '@mui/icons-material/SpaOutlined'
import ThermostatOutlinedIcon from '@mui/icons-material/ThermostatOutlined'
import DataFrame from './dataFrame.jsx'
import { BASE_URL } from '../../util/constant.js'
import { EventSourcePolyfill } from 'event-source-polyfill'
import { useSelector } from 'react-redux'
import { selectCurrentUser } from '../../redux/Slices/userSlice.js'

const DataField = ({ deviceId }) => {
  const [data, setData] = useState({
    updatedTime: '1747221060',
    wateringTime: '--',
    Temperature: '--',
    soilMoisture: '--',
    Light: '--',
    Humidity: '--'
  })
  const user = useSelector(selectCurrentUser)
  const token = user?.token

  useEffect(() => {
    let eventSource

    const connect = () => {
      eventSource = new EventSourcePolyfill(`${BASE_URL}/device/v1/events?deviceId=${deviceId}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }

      })

      eventSource.onmessage = (event) => {
        const data = JSON.parse(event.data)
        console.log('Received data:', data)
        setData(data)
      }

      eventSource.onerror = (error) => {
        console.error('SSE error:', error)
        eventSource.close()
      }
    }

    setTimeout(() => {
      console.log('Mở connection SSE...')
      connect()
    }, 2000)

    return () => {
      console.log('Đóng connection SSE trước khi unmount...')
      eventSource?.close()
    }
  }, [deviceId])

  const formatTime = (timestamp) => {
    if (!timestamp) return 'Updating...'

    const date = new Date(timestamp)
    if (isNaN(date.getTime())) return 'Updating'

    return date.toLocaleString('en-US', {
      weekday: 'long',
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    })
  }
  const sensorData = [
    { label: 'Temperature', value: data ? `${data.Temperature} °C` : '...', icon: ThermostatOutlinedIcon },
    { label: 'Humidity', value: data ? `${data.Humidity} %` : '...', icon: WaterDropOutlinedIcon },
    { label: 'Light', value: data ? `${data.Light} Lux` : '...', icon: WbSunnyOutlinedIcon },
    { label: 'Soil Moisture', value: data ? `${data.soilMoisture} %` : '...', icon: SpaOutlinedIcon }
  ]

  return (
    <Box sx={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: 2,
      mt: 2,
      p: 3,
      background: '#FFFFFF',
      borderRadius: 4,
      boxShadow: '0 4px 12px rgba(0,0,0,0.05)'
    }}>
      <Box sx={{
        display: 'grid',
        gap: 2,
        width: '100%',
        gridTemplateColumns: {
          xs: 'repeat(2, 1fr)',
          sm: 'repeat(2, 1fr)',
          md: 'repeat(4, 1fr)',
          lg: 'repeat(4, 1fr)'
        },
        justifyContent: 'space-between'
      }}>
        {sensorData.map((item, index) => (
          <DataFrame name={item.label} value={parseFloat(item.value).toFixed(2)} icon={item.icon}
            key={index}/>
        ))}
      </Box>
      <Typography variant="subtitle1" sx={{ color: 'gray' }}>
                Updated: {formatTime(data.updatedTime)}
      </Typography>
    </Box>
  )
}

export default DataField