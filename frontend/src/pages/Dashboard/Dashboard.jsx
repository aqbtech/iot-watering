import { Box, Card, CardContent, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, List, ListItem, ListItemText } from '@mui/material'
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth'
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers'
import dayjs from 'dayjs'
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import SchedulingTable from '../../components/SchedulingTable'
import HistoryTable from '../../components/HistoryTable'
import Header from '../../components/Header'
import PageLoadingSpinner from '../../components/PageLoadingSpinner'
import { useEffect, useState } from 'react'
import { selectedCurrentActiveDashboard, fetchDetailDashboard } from '../../redux/Slices/dashboardSlice'
import { useDispatch, useSelector } from 'react-redux'
import { useParams } from 'react-router-dom'
import DataField from './DataField'
import { triggerAPI } from '../../apis/deviceApi'


const Dashboard = () => {
  const dispatch = useDispatch()
  const { deviceId } = useParams()

  // Lấy dữ liệu từ Redux
  const dashboard = useSelector(selectedCurrentActiveDashboard)

  useEffect(() => {
    dispatch(fetchDetailDashboard(deviceId))
  }, [dispatch, deviceId])

  if (!dashboard) {
    return <PageLoadingSpinner caption="Loading Board..." />
  }

  const handleToggleDevice = async () => {
    const response = await triggerAPI(deviceId)
    console.log('123', response)
    return response
  }


  return (
    <>
      <Header />
      <Box sx={{ p: 3, maxWidth: 1200, minWidth: 300, margin: 'auto' }}>
        <Typography variant="h4" fontWeight="bold">
          {dashboard?.name} {/* Sử dụng `dashboard` thay vì `detailDevice` */}
        </Typography>
        <Button
          sx={{
            flexShrink: 0,
            width: { xs: '100%', md: 250 },
            height: 100,
            fontSize: 20,
            fontWeight: 'bold',
            bgcolor: dashboard.status === 'active' ? '#2E7D32' : '#D32F2F',
            color: 'white',
            borderRadius: 2,
            '&:hover': { bgcolor: dashboard.status === 'active' ? '#1B5E1F' : '#9A0007' }
          }}
          onClick={handleToggleDevice}
        >
          {dashboard.status}
        </Button>
        <DataField deviceId={deviceId} />
        <HistoryTable deviceId={deviceId} />
      </Box>
    </>
  )
}

export default Dashboard
