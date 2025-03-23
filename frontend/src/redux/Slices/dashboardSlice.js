import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import axios from 'axios'
import authorizedAxios from '../../util/authorizeAxios'
import { BASE_URL } from '../../util/constant'

//khởi tạo giá trị state của 1 slice trong redux
const initialState = {
  currentDashboard: null
}

export const fetchDetailDashboard = createAsyncThunk(
  'activeDashboard/fetchDetailDashboard',
  async (deviceId) => {
    const response = await authorizedAxios.get(
      `${BASE_URL}/device/v1/detail?dvcId=${deviceId}`
    )
    return response.data.result
  }
)

export const activeDashboardSlice = createSlice({
  name: 'activeDashboard',
  initialState,
  reducers: {
    updateCurrentActiveDashboard: (state, action) => {
      state.currentDashboard = action.payload
    },
    updateState: (state, action) => {
      state.currentDashboard.status = action.payload
    }
  },
  extraReducers: (builder) => {
    builder
      // .addCase(fetchDetailDashboard.fulfilled, (state, action) => {
      //   // xử lý dữ liệu khi gọi API thành công, action.payload là dữ liệu trả về từ hàm trên
      //   const fullDashboard = action.payload

      //   //xử lý dữ liệu....

      //   //update lại dữ liệu
      //   state.currentDashboard = fullDashboard
      // })
      .addCase(fetchDetailDashboard.fulfilled, (state, action) => {
        state.currentDashboard = { ...action.payload, data: null } // Ban đầu data = null
      })
  }
})

export const { updateCurrentActiveDashboard, updateState} = activeDashboardSlice.actions

export const selectedCurrentActiveDashboard = (state) =>
  state.activedashboard.currentDashboard

export const activeDashboardReducer = activeDashboardSlice.reducer
