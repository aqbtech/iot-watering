import { configureStore } from '@reduxjs/toolkit'
import { activeDashboardReducer } from './Slices/dashboardSlice'
import { userReducer } from './Slices/userSlice'
import { combineReducers } from '@reduxjs/toolkit'
import { persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'

const persistConfig = {
  key: 'root',
  storage: storage,
  whitelist: ['user'] //lưu vào localstorage để f5 ko bị mất
}

const reducers = combineReducers({
  activedashboard: activeDashboardReducer,
  user: userReducer
})

const persistedReducer = persistReducer(persistConfig, reducers)

export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({ serializableCheck: false })
})
