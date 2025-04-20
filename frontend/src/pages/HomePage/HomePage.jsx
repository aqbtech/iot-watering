import {
  Container,
  Typography,
  Box,
  Grid,
  Pagination,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  useTheme,
  useMediaQuery
} from '@mui/material'
import Header from '../../components/Header'
import DeviceItem from './DeviceItem'
import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { getListDevicesAPI, addDeviceAPI } from '../../apis/deviceApi'
import AddIcon from '@mui/icons-material/Add'

const HomePage = () => {
  const theme = useTheme()
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'))

  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [listDevice, setListDevice] = useState([])
  const [openAddModal, setOpenAddModal] = useState(false)
  const [newDeviceId, setNewDeviceId] = useState('')
  const [newNameDevice, setNewNameDevice] = useState('')

  const getListDevice = async (page) => {
    toast.promise(
      getListDevicesAPI(page, 9),
      {
        pending: 'Loading your devices...'
      }
    ).then(devices => {
      setListDevice(devices.content)
      setTotalPages(devices.totalPages)
    })
  }

  useEffect(() => {
    getListDevice(page)
  }, [page])

  const handleAddDevice = () => {
    toast.promise(
      addDeviceAPI(newDeviceId, newNameDevice),
      {
        pending: 'Adding device...',
        success: 'Device added successfully!'
      }
    ).then(() => {
      setOpenAddModal(false)
      setNewDeviceId('')
      setNewNameDevice('')
      getListDevice(page)
    })
  }

  return (
    <Box sx={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 100%)',
      pb: { xs: 3, sm: 4, md: 6 }
    }}>
      <Header />

      <Container maxWidth="lg" sx={{
        pt: { xs: 2, sm: 3, md: 4 },
        px: { xs: 2, sm: 3, md: 4 }
      }}>
        <Box sx={{
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          justifyContent: 'space-between',
          alignItems: { xs: 'stretch', sm: 'center' },
          gap: { xs: 2, sm: 0 },
          mb: { xs: 3, sm: 4 }
        }}>
          <Typography
            variant={isMobile ? 'h6' : 'h5'}
            sx={{
              fontWeight: 600,
              color: '#2E7D32',
              textAlign: { xs: 'center', sm: 'left' }
            }}
          >
            Your Devices
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setOpenAddModal(true)}
            sx={{
              bgcolor: '#2E7D32',
              '&:hover': { bgcolor: '#1B5E20' },
              width: { xs: '100%', sm: 'auto' }
            }}
          >
            Add Device
          </Button>
        </Box>

        {/* Devices Grid */}
        {listDevice.length > 0 ? (
          <Grid container spacing={{ xs: 2, sm: 3 }}>
            {listDevice.map((device) => (
              <Grid item xs={12} sm={6} md={4} key={device.deviceId}>
                <DeviceItem device={device} />
              </Grid>
            ))}
          </Grid>
        ) : (
          <Box
            sx={{
              textAlign: 'center',
              py: { xs: 4, sm: 6 },
              px: { xs: 2, sm: 4 },
              background: 'rgba(255, 255, 255, 0.9)',
              borderRadius: 4,
              boxShadow: '0 2px 8px rgba(0,0,0,0.05)'
            }}
          >
            <Typography variant={isMobile ? 'body1' : 'h6'} sx={{ color: '#666', mb: 1 }}>
              No devices found
            </Typography>
            <Typography variant="body2" sx={{ color: '#888' }}>
              Add your first device to get started
            </Typography>
          </Box>
        )}

        {/* Pagination */}
        {listDevice.length > 1 && (
          <Box sx={{
            display: 'flex',
            justifyContent: 'center',
            mt: { xs: 3, sm: 4 }
          }}>
            <Pagination
              count={totalPages}
              page={page}
              onChange={(event, value) => setPage(value)}
              color="primary"
              size={isMobile ? 'small' : 'medium'}
              sx={{
                '& .MuiPaginationItem-root': {
                  color: '#2E7D32',
                  '&.Mui-selected': {
                    bgcolor: '#2E7D32',
                    color: 'white',
                    '&:hover': {
                      bgcolor: '#1B5E20'
                    }
                  }
                }
              }}
            />
          </Box>
        )}
      </Container>

      {/* Add Device Modal */}
      <Dialog
        open={openAddModal}
        onClose={() => setOpenAddModal(false)}
        PaperProps={{
          sx: {
            borderRadius: 2,
            width: { xs: '90%', sm: 400 },
            maxWidth: '90vw'
          }
        }}
      >
        <DialogTitle sx={{
          bgcolor: '#2E7D32',
          color: 'white',
          fontWeight: 600,
          fontSize: { xs: '1.2rem', sm: '1.5rem' }
        }}>
          Add New Device
        </DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Device Name"
            value={newNameDevice}
            onChange={(e) => setNewNameDevice(e.target.value)}
            placeholder="Enter name's device"
            sx={{ mt: 1 }}
            size={isMobile ? 'small' : 'medium'}
          />
        </DialogContent>
        <DialogContent sx={{ mt: -2 }}>
          <TextField
            fullWidth
            label="Device ID"
            value={newDeviceId}
            onChange={(e) => setNewDeviceId(e.target.value)}
            placeholder="Enter device ID"
            size={isMobile ? 'small' : 'medium'}
          />
        </DialogContent>
        <DialogActions sx={{
          p: { xs: 1.5, sm: 2 },
          gap: { xs: 0.5, sm: 1 },
          flexDirection: { xs: 'column', sm: 'row' }
        }}>
          <Button
            onClick={() => setOpenAddModal(false)}
            sx={{
              color: '#666',
              width: { xs: '100%', sm: 'auto' }
            }}
          >
            Cancel
          </Button>
          <Button
            variant="contained"
            onClick={handleAddDevice}
            disabled={!newDeviceId.trim() || !newNameDevice.trim()}
            sx={{
              bgcolor: '#2E7D32',
              '&:hover': { bgcolor: '#1B5E20' },
              width: { xs: '100%', sm: 'auto' }
            }}
          >
            Add Device
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default HomePage
