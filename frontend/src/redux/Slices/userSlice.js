import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import authorizedAxios from '../../util/authorizeAxios'
import { BASE_URL } from '../../util/constant'
import { axiosPublic } from '../../util/authorizeAxios'

//Định nghĩa initialState đúng cách
const initialState = {
  currentUser: null
}

//API gọi login
export const loginUserAPI = createAsyncThunk(
  'user/loginUserAPI',
  async (data) => {
    const response = await axiosPublic.post(`${BASE_URL}/auth/token`, data)
    return response.data
  }
)

export const logoutUserAPI = createAsyncThunk(
  'user/logoutUserAPI',
  async (token) => {
    const data = { token: token }
    const response = await authorizedAxios.post(`${BASE_URL}/auth/logout`, data)
    return response.data
  }
)

//Tạo slice đúng cách
export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    updateCurrentUser: (state, action) => {
      state.currentUser = action.payload
    }
  }, // Có thể thêm các reducers khác nếu cần
  extraReducers: (builder) => {
    builder.addCase(loginUserAPI.fulfilled, (state, action) => {
      state.currentUser = action.payload.result
    })
    builder.addCase(logoutUserAPI.fulfilled, (state) => {
      state.currentUser = null
    })
  }
})

//Selector để lấy user hiện tại
export const selectCurrentUser = (state) => {
  return state.user.currentUser
}

//Xuất reducer đúng cách
export const userReducer = userSlice.reducer
