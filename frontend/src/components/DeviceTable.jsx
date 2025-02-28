import React, { useState } from 'react'
import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Typography,
  Pagination
} from '@mui/material'
import { Link } from 'react-router-dom'


const deviceData = [
  { name: 'Jane Cooper', location: 'Thủ Đức', serial: '(225) 555-0118', status: 'Active' },
  { name: 'Floyd Miles', location: 'TPHCM', serial: '(205) 555-0100', status: 'Inactive' },
  { name: 'Ronald Richards', location: 'Đồng Nai', serial: '(302) 555-0107', status: 'Inactive' },
  { name: 'Marvin McKinney', location: 'Biên Hòa', serial: '(252) 555-0126', status: 'Active' },
  { name: 'Jerome Bell', location: 'KTX-A', serial: '(629) 555-0129', status: 'Active' },
  { name: 'Kathryn Murphy', location: 'KTX-B', serial: '(406) 555-0120', status: 'Active' },
  { name: 'Jacob Jones', location: 'KTX-A', serial: '(208) 555-0112', status: 'Active' },
  { name: 'Kristin Watson', location: 'KTX-B', serial: '(704) 555-0127', status: 'Inactive' },
  { name: 'Jane Cooper', location: 'Thủ Đức', serial: '(225) 555-0118', status: 'Active' },
  { name: 'Floyd Miles', location: 'TPHCM', serial: '(205) 555-0100', status: 'Inactive' },
  { name: 'Ronald Richards', location: 'Đồng Nai', serial: '(302) 555-0107', status: 'Inactive' },
  { name: 'Marvin McKinney', location: 'Biên Hòa', serial: '(252) 555-0126', status: 'Active' },
  { name: 'Jerome Bell', location: 'KTX-A', serial: '(629) 555-0129', status: 'Active' },
  { name: 'Kathryn Murphy', location: 'KTX-B', serial: '(406) 555-0120', status: 'Active' },
  { name: 'Jacob Jones', location: 'KTX-A', serial: '(208) 555-0112', status: 'Active' },
  { name: 'Kristin Watson', location: 'KTX-B', serial: '(704) 555-0127', status: 'Inactive' }
]

const ITEMS_PER_PAGE = 10

const DeviceTable = () => {
  const [page, setPage] = useState(1)
  const startIndex = (page - 1) * ITEMS_PER_PAGE
  const endIndex = startIndex + ITEMS_PER_PAGE
  const paginatedData = deviceData.slice(startIndex, endIndex)

  return (
    <Box sx={{ maxWidth: 900, margin: 'auto', p: 3 }}>
      <Paper sx={{ p: 3, borderRadius: 3 }}>
        {/* Header */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h5" fontWeight="bold">
            All Devices
          </Typography>
          <Button variant="contained" sx={{ backgroundColor: '#14b8a6' }}>
            + Add device
          </Button>
        </Box>

        {/* Table */}
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell><b>Device name</b></TableCell>
                <TableCell><b>Location</b></TableCell>
                <TableCell><b>Serial number</b></TableCell>
                <TableCell><b>Delete</b></TableCell>
                <TableCell><b>Status</b></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {paginatedData.map((device, index) => (
                <TableRow key={index} sx={{ '&:hover': { backgroundColor: '#f3f4f6', cursor: 'pointer' } }}>
                  <TableCell component={Link} to={`/Dashboard/${device.id}`} style={{ textDecoration: 'none' }}>{device.name}</TableCell>
                  <TableCell>{device.location}</TableCell>
                  <TableCell>{device.serial}</TableCell>
                  <TableCell>
                    <Button variant="contained" sx={{ backgroundColor: '#ef4444' }}>Delete</Button>
                  </TableCell>
                  <TableCell>
                    <Button variant="contained" sx={{ backgroundColor: device.status === 'Active' ? '#14b8a6' : '#ef4444' }}>
                      {device.status}
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Pagination */}
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
          <Pagination count={Math.ceil(deviceData.length / ITEMS_PER_PAGE)} page={page} onChange={(e, value) => setPage(value)} />
        </Box>
      </Paper>
    </Box>
  )
}

export default DeviceTable
