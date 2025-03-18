import { Box, Card, CardContent, Typography, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, TextField, MenuItem, Select } from '@mui/material'
import { useState, useEffect, useRef } from 'react'
import CircularProgress from '@mui/material/CircularProgress'
import { getHistoryAPI } from '../apis/deviceApi'
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker'
import dayjs from 'dayjs'

const HistoryTable = ({ deviceId }) => {
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [historyTable, setHistoryTable] = useState([])
  const [loading, setLoading] = useState(false)
  const tableRef = useRef(null)

  const [limit, setLimit] = useState(10)
  const [from, setFrom] = useState(dayjs().subtract(10, 'minute').valueOf())
  const [to, setTo] = useState(dayjs().valueOf())


  const getHistory = async () => {
    if (!deviceId || loading) return

    setLoading(true)

    let timeFrom = from ? from.valueOf() : ''
    let timeTo = to ? to.valueOf() : ''

    const response = await getHistoryAPI(deviceId, timeFrom, timeTo, limit, page)
    if (response) {
      setHistoryTable((prev) => [...prev, ...response.content])
      setTotalPages(response?.totalPages)
    }
    setLoading(false)
  }

  useEffect(() => {
    setHistoryTable([]) // Reset dữ liệu khi thay đổi filter
    getHistory()
  }, [deviceId, limit, from, to])


  const formatTime = (timestamp) => {
    if (!timestamp) return 'Updating...'
    let date
    if (!isNaN(timestamp)) {
    // Nếu timestamp là UNIX timestamp (số), cần nhân 1000 để chuyển từ giây -> ms
      date = new Date(Number(timestamp) * 1000)
    } else {
    // Nếu timestamp là dạng chuỗi ISO 8601, dùng Date trực tiếp
      date = new Date(timestamp)
    }
    return date.toLocaleString('en-US', {
      weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false
    })
  }

  const formatNumber = (value) => {
    return value.toString().slice(0, 2) // Lấy 2 ký tự đầu
  }

  return (
    <Card sx={{ flex: 1, p: 2, bgcolor: '#FFFFFF', color: '#000', borderRadius: 8, maxHeight: 450 }}>
      <Typography variant="h6" fontWeight="bold">History</Typography>

      {/* Bộ lọc */}
      {/* Bộ lọc thời gian và số phần tử */}
      <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
        <DateTimePicker
          label="From"
          value={from ? dayjs(from) : null} // Chuyển đổi timestamp thành dayjs
          onChange={(newValue) => setFrom(newValue ? newValue.valueOf() : null)} // Chuyển về timestamp
        />

        <DateTimePicker
          label="To"
          value={to ? dayjs(to) : null} // Chuyển đổi timestamp thành dayjs
          onChange={(newValue) => setTo(newValue ? newValue.valueOf() : null)} // Chuyển về timestamp
        />
        <Select value={limit} onChange={(e) => setLimit(e.target.value)}>
          {[10, 20, 50, 100].map((size) => (
            <MenuItem key={size} value={size}>{size} items</MenuItem>
          ))}
        </Select>
      </Box>

      <TableContainer ref={tableRef} component={Paper} sx={{ mt: 2, maxHeight: 300, overflowY: 'auto' }}>
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell sx={{ color: '#000' }}>Time</TableCell>
              <TableCell sx={{ color: '#000' }}>Temperature</TableCell>
              <TableCell sx={{ color: '#000' }}>Soil Moisture</TableCell>
              <TableCell sx={{ color: '#000' }}>Humidity</TableCell>
              <TableCell sx={{ color: '#000' }}>Light</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {historyTable.length > 0 ? (
              historyTable.map((row, index) => (
                <TableRow key={index}>
                  <TableCell>{formatTime(row.updatedTime)}</TableCell>
                  <TableCell>{formatNumber(row.Temperature)} °C</TableCell>
                  <TableCell>{formatNumber(row.soilMoisture)} %</TableCell>
                  <TableCell>{formatNumber(row.Humidity)} %</TableCell>
                  <TableCell>{formatNumber(row.Light)} Lux</TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} align="center">No Data</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {loading && (
        <div style={{ display: 'flex', justifyContent: 'center', padding: 10 }}>
          <CircularProgress size={25} />
        </div>
      )}
    </Card>
  )
}


export default HistoryTable
