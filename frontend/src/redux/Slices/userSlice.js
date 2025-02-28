import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import authorizedAxios from '../../util/authorizeAxios'
import { BASE_URL } from '../../util/constant'

//Định nghĩa initialState đúng cách
const initialState = {
  currentUser: null
}

//API gọi login
export const loginUserAPI = createAsyncThunk(
  'user/loginUserAPI',
  async (data) => {
    const response = await authorizedAxios.post(`${BASE_URL}/auth/token`, data)
    return response.data
  }
)

//Tạo slice đúng cách
export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {}, // Có thể thêm các reducers khác nếu cần
  extraReducers: (builder) => {
    builder.addCase(loginUserAPI.fulfilled, (state, action) => {
      state.currentUser = action.payload
    })
  }
})

//Selector để lấy user hiện tại
export const selectCurrentUser = (state) => state.user.currentUser

//Xuất reducer đúng cách
export const userReducer = userSlice.reducer
