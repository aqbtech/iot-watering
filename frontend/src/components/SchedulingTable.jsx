import { useState } from 'react'
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers'
import dayjs from 'dayjs'
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import { Box, Card, CardContent, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, List, ListItem, ListItemText } from '@mui/material'
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth'

const SchedulingTable = () => {
  const [selectedMonth, setSelectedMonth] = useState(dayjs('2025-05'))
  const schedule = [
    { date: '2025-05-02', time: '08:30', watering_time: '10 min' },
    { date: '2025-05-10', time: '15:00', watering_time: '10 min' },
    { date: '2025-05-20', time: '06:45', watering_time: '10 min' },
    { date: '2025-05-02', time: '08:30', watering_time: '10 min' },
    { date: '2025-05-02', time: '08:30', watering_time: '10 min' },
    { date: '2025-05-02', time: '08:30', watering_time: '10 min' }
  ]
  // Lọc các lịch tưới nước theo tháng đã chọn
  const filteredSchedule = schedule.filter((item) =>
    dayjs(item.date).isSame(selectedMonth, 'month')
  )

  return (
    <LocalizationProvider>
      <Card sx={{ width: 300, p: 2, borderRadius: 8 }}>
        <Typography variant="h6" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <CalendarMonthIcon /> {selectedMonth.format('MMMM YYYY')}
        </Typography>

        <DatePicker
          views={['year', 'month']}
          value={selectedMonth}
          onChange={(newValue) => setSelectedMonth(newValue)}
        />
        <Box sx={{ maxHeight: 150, overflow: 'auto' }}>
          <List>
            {filteredSchedule.length > 0 ? (
              filteredSchedule.map((item, index) => (
                <ListItem key={index}>
                  <ListItemText primary={`${item.date} - ${item.time} - ${item.watering_time}`} />
                </ListItem>
              ))
            ) : (
              <Typography sx={{ mt: 1, color: 'gray' }}>Không có lịch tưới</Typography>
            )}
          </List>
        </Box>
      </Card>
    </LocalizationProvider>
  )
}

export default SchedulingTable
