import { CreateSlice } from '@reduxjs/toolkit'

initialState = {
  currentUser: null
}

export const userSlice = CreateSlice({
  name: 'user',
  initialState,
  reducers: {
    //Reducers: xử lý dữ liệu đồng bộ
    updateUser: (state, action) => {
      const fullUser = action.payload

      //xử lý dữ liệu....

      //update lại dữ liệu
      state.currentUser = fullUser
    }
  }
})

export const { updateUser } = userSlice.actions

export default userSlice.reducer
