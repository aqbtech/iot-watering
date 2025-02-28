import Button from '@mui/material/Button'
import { Link } from 'react-router-dom'
import { assets } from '../../assets/asset'
import Header from '../../components/Header'
import DeviceTable from '../../components/DeviceTable'

const HomePage = () => {
  return (
    <div style={{ minHeight: '100vh' }}>
      <Header/>
      <DeviceTable/>
    </div>
  )
}

export default HomePage
