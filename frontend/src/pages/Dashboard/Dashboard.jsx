import { Box, Card, CardContent, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, List, ListItem, ListItemText } from '@mui/material'
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth'
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers'
import dayjs from 'dayjs'
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import DataFrame from '../../components/dataFrame'
import SchedulingTable from '../../components/SchedulingTable'
import HistoryTable from '../../components/HistoryTable'
import Header from '../../components/Header'
import WaterDropOutlinedIcon from '@mui/icons-material/WaterDropOutlined'
import WbSunnyOutlinedIcon from '@mui/icons-material/WbSunnyOutlined'
import SpaOutlinedIcon from '@mui/icons-material/SpaOutlined'
import ThermostatOutlinedIcon from '@mui/icons-material/ThermostatOutlined'

const Dashboard = () => {
  const data = [
    { label: 'Temperature', value: '27 °C', icon: ThermostatOutlinedIcon },
    { label: 'Humidity', value: '50%', icon: WaterDropOutlinedIcon },
    { label: 'Light', value: '100 Lux', icon: WbSunnyOutlinedIcon },
    { label: 'Soil Moisture', value: '60 %', icon: SpaOutlinedIcon }
  ]


  return (
    <>
      <Header/>
      <Box sx={{ p: 3, maxWidth: 1200, minWidth: 300, margin: 'auto' }}>

        <Typography variant="h4" fontWeight="bold">
          Jane Cooper
        </Typography>

        <Box
          sx={{
            display: 'flex',
            flexDirection: { xs: 'column', md: 'row' },
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: 2,
            mt: 2
          }}
        >
          <Button
            sx={{
              flexShrink: 0, // Giữ nguyên kích thước, không bị co
              width: { xs: '100%', md: 250 }, // Full khi nhỏ, 250px khi lớn
              height: 100, // Cao 100px
              fontSize: 20,
              fontWeight: 'bold',
              bgcolor: '#2E7D32',
              color: 'white',
              borderRadius: 2,
              '&:hover': { bgcolor: '#1B5E1F' }
            }}
            onClick={() => console.log('Toggled device')}
          >
            ON
          </Button>
          <Box
            sx={{
              display: 'grid',
              gap: 2,
              flex: 1,
              gridTemplateColumns: {
                xs: 'repeat(auto-fit, minmax(140px, 1fr))',
                md: 'repeat(auto-fit, minmax(200px, 1fr))'
              }, // Tự động co giãn và trải đều
              width: '100%'
            }}
          >
            {data.map((item, index) => (
              <DataFrame name={item.label} value={item.value} icon={item.icon} key={index} />
            ))}
          </Box>
        </Box>

        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 3, mt: 3 }}>
            <SchedulingTable/>
            <HistoryTable/>
          </Box>
        </LocalizationProvider>
      </Box>
    </>

  )
}

export default Dashboard
