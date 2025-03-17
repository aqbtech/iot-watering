
import { ToastContainer } from 'react-toastify'
import { Routes, Route, Navigate, Outlet } from 'react-router-dom'
import 'react-toastify/dist/ReactToastify.css'
import Auth from './pages/Auth/Auth.jsx'
import HomePage from './pages/HomePage/HomePage.jsx'
import Dashboard from './pages/Dashboard/Dashboard.jsx'
import { useSelector } from 'react-redux'
import { selectCurrentUser } from './redux/Slices/userSlice.js'

const ProtectedRoute = ({ user }) => {
  if (!user) return <Navigate to='/Login' replace={true} />
  return <Outlet />
}

function App() {
  const currentUser = useSelector(selectCurrentUser)

  return (
    <div className="bg-[#EEEEEE]" style={{ minHeight: '100vh' }}>
      <div>
        <ToastContainer />
        <Routes>
          <Route element={<ProtectedRoute user={currentUser}/>}>
            <Route path='/' element={<HomePage/>}/>
            <Route path='/Dashboard/:DashboardId' element={<Dashboard/>}/>
          </Route>
          <Route path='/Signup' element={<Auth/>}/>
          <Route path='/Login' element={<Auth/>}/>
        </Routes>
      </div>
    </div>
  )
}

export default App
