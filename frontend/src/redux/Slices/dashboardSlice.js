import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import axios from 'axios'
import { BASE_URL } from '../../util/constant'
//khởi tạo giá trị state của 1 slice trong redux

const initialState = {
  currentDashboard: null
}

export const fetchDetailDashboard = createAsyncThunk(
  'activeDashboard/fetchDetailDashboard',
  async (dashboardId) => {
    // Lấy dữ liệu từ server
    const response = await axios.get(`/api/dashboard/${dashboardId}`)
    return response.data
  }
)

export const activeDashboardSlice = createSlice({
  name: 'activeDashboard',
  initialState,
  reducers: {
    //Reducers: xử lý dữ liệu đồng bộ
    updateCurrentActiveDashboard: (state, action) => {
      const fullDashboard = action.payload

      //xử lý dữ liệu....

      //update lại dữ liệu
      state.currentDashboard = fullDashboard
    }
  },
  extraReducers: (builder) => {
    builder.addCase(fetchDetailDashboard.fulfilled, (state, action) => {
      // xử lý dữ liệu khi gọi API thành công, action.payload là dữ liệu trả về từ hàm trên
      const fullDashboard = action.payload

      //xử lý dữ liệu....

      //update lại dữ liệu
      state.currentDashboard = fullDashboard
    })
  }
})

export const { updateCurrentActiveDashboard } = activeDashboardSlice.actions

export const selectedCurrentActiveDashboard = (state) => {
  return state.activeDashboard.currentDashboard
}

export const activeDashboardReducer = activeDashboardSlice.reducer
