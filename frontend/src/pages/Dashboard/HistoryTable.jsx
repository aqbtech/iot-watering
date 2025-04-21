import { Box, Card, Typography, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, MenuItem, Select, IconButton, Tooltip } from '@mui/material'
import { useState, useEffect, useRef } from 'react'
import CircularProgress from '@mui/material/CircularProgress'
import { getHistoryAPI } from '../../apis/deviceApi'
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker'
import dayjs from 'dayjs'
import RefreshIcon from '@mui/icons-material/Refresh'
import FilterListIcon from '@mui/icons-material/FilterList'
import ThermostatIcon from '@mui/icons-material/Thermostat'
import WaterDropIcon from '@mui/icons-material/WaterDrop'
import WbSunnyIcon from '@mui/icons-material/WbSunny'
import OpacityIcon from '@mui/icons-material/Opacity'

const HistoryTable = ({ deviceId }) => {
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [historyTable, setHistoryTable] = useState([])
  const [loading, setLoading] = useState(false)
  const [showFilters, setShowFilters] = useState(false)
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
    return value?.toString().slice(0, 2) // Lấy 2 ký tự đầu
  }

  const handleRefresh = () => {
    setHistoryTable([])
    getHistory()
  }


  return (
    <Card sx={{
      flex: 1,
      p: 3,
      bgcolor: '#FFFFFF',
      color: '#000',
      borderRadius: 4,
      boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
      overflow: 'hidden'
    }}>
      <Box sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        mb: 3
      }}>
        <Typography variant="h6" sx={{
          fontWeight: 600,
          color: '#2E7D32',
          display: 'flex',
          alignItems: 'center',
          gap: 1
        }}>
          <FilterListIcon /> History Data
        </Typography>

        <Box sx={{ display: 'flex', gap: 1 }}>
          <Tooltip title="Refresh Data">
            <IconButton
              onClick={handleRefresh}
              sx={{
                bgcolor: '#E8F5E9',
                color: '#2E7D32',
                '&:hover': { bgcolor: '#C8E6C9' }
              }}
            >
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title="Toggle Filters">
            <IconButton
              onClick={() => setShowFilters(!showFilters)}
              sx={{
                bgcolor: '#E8F5E9',
                color: '#2E7D32',
                '&:hover': { bgcolor: '#C8E6C9' }
              }}
            >
              <FilterListIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Bộ lọc */}
      {showFilters && (
        <Box sx={{
          display: 'flex',
          flexDirection: { xs: 'column', md: 'row' },
          gap: 2,
          mb: 3,
          p: 2,
          bgcolor: '#F5F5F5',
          borderRadius: 2
        }}>
          <DateTimePicker
            label="From"
            value={from ? dayjs(from) : null}
            onChange={(newValue) => setFrom(newValue ? newValue.valueOf() : null)}
            sx={{ flex: 1 }}
          />

          <DateTimePicker
            label="To"
            value={to ? dayjs(to) : null}
            onChange={(newValue) => setTo(newValue ? newValue.valueOf() : null)}
            sx={{ flex: 1 }}
          />

          <Select
            value={limit}
            onChange={(e) => setLimit(e.target.value)}
            sx={{
              minWidth: 120,
              bgcolor: 'white'
            }}
          >
            {[10, 20, 50, 100].map((size) => (
              <MenuItem key={size} value={size}>{size} items</MenuItem>
            ))}
          </Select>
        </Box>
      )}

      <TableContainer
        ref={tableRef}
        component={Paper}
        sx={{
          maxHeight: 400,
          overflowY: 'auto',
          borderRadius: 2,
          boxShadow: 'none',
          border: '1px solid #E0E0E0'
        }}
      >
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell sx={{
                bgcolor: '#E8F5E9',
                color: '#2E7D32',
                fontWeight: 600,
                fontSize: '0.9rem'
              }}>
                Time
              </TableCell>
              <TableCell sx={{
                bgcolor: '#E8F5E9',
                color: '#2E7D32',
                fontWeight: 600,
                fontSize: '0.9rem'
              }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <ThermostatIcon fontSize="small" /> Temperature
                </Box>
              </TableCell>
              <TableCell sx={{
                bgcolor: '#E8F5E9',
                color: '#2E7D32',
                fontWeight: 600,
                fontSize: '0.9rem'
              }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <WaterDropIcon fontSize="small" /> Soil Moisture
                </Box>
              </TableCell>
              <TableCell sx={{
                bgcolor: '#E8F5E9',
                color: '#2E7D32',
                fontWeight: 600,
                fontSize: '0.9rem'
              }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <OpacityIcon fontSize="small" /> Humidity
                </Box>
              </TableCell>
              <TableCell sx={{
                bgcolor: '#E8F5E9',
                color: '#2E7D32',
                fontWeight: 600,
                fontSize: '0.9rem'
              }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <WbSunnyIcon fontSize="small" /> Light
                </Box>
              </TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {historyTable.map((row, index) => (
              <TableRow
                key={index}
                sx={{
                  '&:nth-of-type(odd)': { bgcolor: '#F9F9F9' },
                  '&:hover': { bgcolor: '#F5F5F5' }
                }}
              >
                <TableCell sx={{ fontSize: '0.85rem' }}>
                  {formatTime(row.updatedTime)}
                </TableCell>
                <TableCell sx={{ fontSize: '0.85rem' }}>
                  {formatNumber(row.Temperature)} °C
                </TableCell>
                <TableCell sx={{ fontSize: '0.85rem' }}>
                  {formatNumber(row.soilMoisture)} %
                </TableCell>
                <TableCell sx={{ fontSize: '0.85rem' }}>
                  {formatNumber(row.Humidity)} %
                </TableCell>
                <TableCell sx={{ fontSize: '0.85rem' }}>
                  {formatNumber(row.Light)} lux
                </TableCell>
              </TableRow>
            ))}

            {loading && (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                  <CircularProgress size={24} sx={{ color: '#2E7D32' }} />
                </TableCell>
              </TableRow>
            )}

            {!loading && historyTable.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ py: 3, color: 'text.secondary' }}>
                  No data available
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {totalPages > 1 && (
        <Box sx={{
          display: 'flex',
          justifyContent: 'center',
          mt: 2,
          gap: 1
        }}>
          <Button
            variant="outlined"
            size="small"
            disabled={page === 0}
            onClick={() => setPage(prev => Math.max(0, prev - 1))}
            sx={{
              color: '#2E7D32',
              borderColor: '#2E7D32',
              '&:hover': {
                borderColor: '#1B5E20',
                bgcolor: 'rgba(46, 125, 50, 0.04)'
              }
            }}
          >
            Previous
          </Button>

          <Typography variant="body2" sx={{
            display: 'flex',
            alignItems: 'center',
            color: '#2E7D32'
          }}>
            Page {page + 1} of {totalPages}
          </Typography>

          <Button
            variant="outlined"
            size="small"
            disabled={page >= totalPages - 1}
            onClick={() => setPage(prev => Math.min(totalPages - 1, prev + 1))}
            sx={{
              color: '#2E7D32',
              borderColor: '#2E7D32',
              '&:hover': {
                borderColor: '#1B5E20',
                bgcolor: 'rgba(46, 125, 50, 0.04)'
              }
            }}
          >
            Next
          </Button>
        </Box>
      )}
    </Card>
  )
}

export default HistoryTable
