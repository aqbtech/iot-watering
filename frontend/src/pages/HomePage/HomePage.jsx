import { Grid2, Container, Pagination, Box, Typography } from '@mui/material'
import Header from '../../components/Header'
import DeviceItem from './DeviceItem'
import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { getListDevicesAPI } from '../../apis/deviceApi'

const HomePage = () => {
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [listDevice, setListDevice] = useState([])

  const getListDevice = async (page) => {
    toast.promise(
      getListDevicesAPI(page, 9),
      {
        pending: 'Loading your devices...'
      }
    ).then(devices => {
      console.log(devices)
      setListDevice(devices.content),
      setTotalPages(devices.totalPages)
    }
    )
  }

  useEffect(() => {
    getListDevice(page)
  }, [page])

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
      <Header />
      <Typography variant='h3' style={{ textAlign: 'center', margin: '20px 0' }}>
        -- Your Devices --
      </Typography>
      <Container maxWidth="lg">
        <Grid2 container spacing={3} mt={2}>
          {listDevice.map((device) => (
            <Grid2 item xs={12} sm={6} md={4} key={device.deviceId}>
              <DeviceItem device={device} />
            </Grid2>
          ))}
        </Grid2>

        {/* Phân trang */}
        <Box display="flex" justifyContent="center" mt={4}>
          <Pagination
            count={totalPages}
            page={page}
            onChange={(event, value) => setPage(value)}
            color="primary"
          />
        </Box>
      </Container>
    </div>
  )
}

export default HomePage
