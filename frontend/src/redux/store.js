import { configureStore } from '@reduxjs/toolkit'
import { activeDashboardReducer } from './Slices/dashboardSlice'
import { userReducer } from './Slices/userSlice'

export const store = configureStore({
  reducer: {
    activedashboard: activeDashboardReducer,
    user: userReducer
  }
})
