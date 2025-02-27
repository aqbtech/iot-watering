import { configureStore } from '@reduxjs/toolkit'
import { activeDashboardReducer } from './Slices/dashboardSlice'

export const store = configureStore({
  reducer: {
    activedashboard: activeDashboardReducer
    // Add other reducers here
  }
})
