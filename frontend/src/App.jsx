
import { ToastContainer } from 'react-toastify'
import { Routes, Route } from 'react-router-dom'
import 'react-toastify/dist/ReactToastify.css'
import Auth from './pages/Auth/Auth.jsx'
import HomePage from './pages/HomePage/HomePage.jsx'
import Dashboard from './pages/Dashboard/Dashboard.jsx'
import Register from './pages/Auth/Register.jsx'


function App() {


  return (
    <div className="bg-[#EEEEEE]" style={{ height: '100vh' }}>
      <div>
        <ToastContainer />
        <Routes>
          <Route path='/' element={<HomePage/>}/>
          <Route path='/Dashboard/:DashboardId' element={<Dashboard/>}/>
          <Route path='/Signup' element={<Auth/>}/>
          <Route path='/Login' element={<Auth/>}/>
        </Routes>
      </div>
    </div>
  )
}

export default App
