import { Box, Button, Typography } from '@mui/material'
import { useEffect, useState } from 'react'
import WaterDropOutlinedIcon from '@mui/icons-material/WaterDropOutlined'
import WbSunnyOutlinedIcon from '@mui/icons-material/WbSunnyOutlined'
import SpaOutlinedIcon from '@mui/icons-material/SpaOutlined'
import ThermostatOutlinedIcon from '@mui/icons-material/ThermostatOutlined'
import DataFrame from '../../components/dataFrame'
import { getLatestDataAPI } from '../../apis/deviceApi'

const DataField = ({ deviceId }) => {
  // const mockDashboard = {
  //   detailDevice: { deviceName: 'Smart Sensor', status: 'ON' },
  //   historyTable: [
  //     {
  //       time: '11:11 14/05/2025',
  //       wateringTime: 10,
  //       temperature: 27,
  //       soilMoisture: 60,
  //       light: 60,
  //       humidity: 60,
  //       status: 'success'
  //     },
  //     {
  //       time: '11:12 14/05/2025',
  //       wateringTime: 12,
  //       temperature: 28,
  //       soilMoisture: 58,
  //       light: 65,
  //       humidity: 62,
  //       status: 'success'
  //     },
  //     {
  //       time: '11:11 14/05/2025',
  //       wateringTime: 10,
  //       temperature: 27,
  //       soilMoisture: 60,
  //       light: 60,
  //       humidity: 60,
  //       status: 'success'
  //     },
  //     {
  //       time: '11:11 14/05/2025',
  //       wateringTime: 10,
  //       temperature: 27,
  //       soilMoisture: 60,
  //       light: 60,
  //       humidity: 60,
  //       status: 'success'
  //     },
  //     {
  //       time: '11:11 14/05/2025',
  //       wateringTime: 10,
  //       temperature: 27,
  //       soilMoisture: 60,
  //       light: 60,
  //       humidity: 60,
  //       status: 'success'
  //     },
  //     {
  //       time: '11:11 14/05/2025',
  //       wateringTime: 10,
  //       temperature: 27,
  //       soilMoisture: 60,
  //       light: 60,
  //       humidity: 60,
  //       status: 'success'
  //     }
  //   ],
  //   data: {
  //     Temperature: 25,
  //     Humidity: 60,
  //     Light: 300,
  //     soilMoisture: 45
  //   }
  // }
  // const dispatch = useDispatch()
  // const data = mockDashboard.data
  const [data, setData] = useState({
    updatedTime: '1747221060',
    wateringTime: '--',
    Temperature: '--',
    soilMoisture: '--',
    Light: '--',
    Humidity: '--'
  })
  const [error, setError] = useState(false) // Trạng thái lỗi

  const getData = async (deviceId) => {
    try {
      const response = await getLatestDataAPI(deviceId)
      if (!response.error) {
        setData(response)
        console.log('Data:', response)
        setError(false) // Reset lỗi nếu lấy dữ liệu thành công
      } else {
        throw new Error('API Error')
      }
    } catch (err) {
      console.error('Lỗi khi gọi API:', err)
      setError(true) // Đánh dấu có lỗi
    }
  }

  useEffect(() => {
    if (error) return // Nếu lỗi, không gọi API nữa

    const interval = setInterval(() => {
      getData(deviceId)
    }, 3000)

    return () => clearInterval(interval)
  }, [deviceId, error])

  const formatTime = (timestamp) => {
    if (!timestamp) return 'Đang cập nhật...'

    let date
    if (!isNaN(timestamp)) {
    // Nếu timestamp là UNIX timestamp (số), cần nhân 1000 để chuyển từ giây -> ms
      date = new Date(Number(timestamp) * 1000)
    } else {
    // Nếu timestamp là dạng chuỗi ISO 8601, dùng Date trực tiếp
      date = new Date(timestamp)
    }

    return date.toLocaleString('en-US', {
      weekday: 'long', // Thứ mấy (Ví dụ: "Thứ Hai")
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
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, mt: 2 }}>
      <Box sx={{ display: 'grid', gap: 2, width: '100%', gridTemplateColumns: { xs: 'repeat(auto-fit, minmax(140px, 1fr))', md: 'repeat(auto-fit, minmax(200px, 1fr))' } }}>
        {sensorData.map((item, index) => (
          <DataFrame name={item.label} value={item.value} icon={item.icon} key={index} />
        ))}
      </Box>
      <Typography variant="subtitle1" sx={{ color: 'gray' }}>
        Updated: {formatTime(data.updatedTime)}
      </Typography>
    </Box>

  )
}

export default DataField
