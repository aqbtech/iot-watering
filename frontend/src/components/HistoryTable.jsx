import { Box, Card, CardContent, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, List, ListItem, ListItemText } from '@mui/material'

const HistoryTable = () => {
  const data = [
    { time: '11:11 14/05/2025', watering_time: '10 min', temperature: '27 °C', soilMoisture: '60%', humidity: '85%', light: '60 Lux' },
    { time: '11:11 14/05/2025', watering_time: '10 min', temperature: '27 °C', soilMoisture: '60%', humidity: '85%', light: '60 Lux' },
    { time: '11:11 14/05/2025', watering_time: '10 min', temperature: '27 °C', soilMoisture: '60%', humidity: '85%', light: '60 Lux' },
    { time: '11:11 14/05/2025', watering_time: '10 min', temperature: '27 °C', soilMoisture: '60%', humidity: '85%', light: '60 Lux' },
    { time: '11:11 14/05/2025', watering_time: '10 min', temperature: '27 °C', soilMoisture: '60%', humidity: '85%', light: '60 Lux' },
    { time: '11:11 14/05/2025', watering_time: '10 min', temperature: '27 °C', soilMoisture: '60%', humidity: '85%', light: '60 Lux' }
  ]
  return (
    <Card sx={{ flex: 1, p: 2, bgcolor: '#FFFFFF', color: '#000', borderRadius: 8, maxHeight: 300 }}>
      <Typography variant="h6" fontWeight="bold">History</Typography>

      <TableContainer component={Paper} sx={{ mt: 2, maxHeight: 200, overflowY: 'auto' }}>
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell sx={{ color: '#000' }}>Time</TableCell>
              <TableCell sx={{ color: '#000' }}>Watering time</TableCell>
              <TableCell sx={{ color: '#000' }}>Temperature</TableCell>
              <TableCell sx={{ color: '#000' }}>Soil Moisture</TableCell>
              <TableCell sx={{ color: '#000' }}>Humidity</TableCell>
              <TableCell sx={{ color: '#000' }}>Light</TableCell>\
            </TableRow>
          </TableHead>

          <TableBody>
            {data.map((row, index) => (
              <TableRow key={index}>
                <TableCell sx={{ color: '#000' }}>{row.time}</TableCell>
                <TableCell sx={{ color: '#000' }}>{row.watering_time}</TableCell>
                <TableCell sx={{ color: '#000' }}>{row.temperature}</TableCell>
                <TableCell sx={{ color: '#000' }}>{row.soilMoisture}</TableCell>
                <TableCell sx={{ color: '#000' }}>{row.humidity}</TableCell>
                <TableCell sx={{ color: '#000' }}>{row.light}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Card>


  )
}

export default HistoryTable
