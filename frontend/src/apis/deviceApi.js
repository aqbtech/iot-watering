import authorizedAxios from '../util/authorizeAxios'
import { BASE_URL } from '../util/constant'
import { toast } from 'react-toastify'
import { axiosPublic } from '../util/userAxios'

export const registerUserAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/user/v1/register`,
    data
  )
  toast.success('Account created successfully!')
  return response.data
}

export const refreshTokenAPI = async (token) => {
  const data = { refreshToken: token }
  const response = await axiosPublic.post('/auth/refresh', data)
  return response.data
}

export const getListDevicesAPI = async (page) => {
  const response = await authorizedAxios.get(
    `${BASE_URL}/device/v1/list-device?page=${page}&size=9`
  )
  return response.data.result
}

export const getHistoryAPI = async (deviceId, from, to, limit, page) => {
  const response = await authorizedAxios.get(
    `${BASE_URL}/device/v1/history?dvcId=${deviceId}&from=${from}&to=${to}&page=${page}&size=${limit}`
  )
  return response.data.result
}

export const getLatestDataAPI = async (deviceId) => {
  const response = await authorizedAxios.get(
    `${BASE_URL}/device/v1/latest-telemetry?dvcId=${deviceId}`
  )
  return response.data.result
}

export const triggerAPI = async (deviceId) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/device/v1/trigger?dvcId=${deviceId}`
  )
  return response
}

export const triggerFanAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/device/fan`, data
  )
  return response
}

export const triggerLightAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/device/light`, data
  )
  return response
}

export const triggerPumpAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/device/pump`, data
  )
  return response
}

export const triggerSirenAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/device/siren`, data
  )
  return response
}

export const setConfigAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/device/setConfig`, data
  )
  return response
}

export const addDeviceAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/device/add`,
    data
  )
  return response.data
}

export const getProfileAPI = async () => {
  const response = await authorizedAxios.get(
    `${BASE_URL}/user/v1/profile`
  )
  return response.data
}

export const updateProfileAPI = async (data) => {
  const response = await authorizedAxios.put(
    `${BASE_URL}/user/v1/profile`,
    data
  )
  return response.data
}

export const getStatusConfigAPI = async (deviceId) => {
  const response = await authorizedAxios.get(
    `${BASE_URL}/config/granted?deviceId=${deviceId}`
  )
  return response.data
}

export const setStatusConfigAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/config/granted`, data
  )
  return response.data
}