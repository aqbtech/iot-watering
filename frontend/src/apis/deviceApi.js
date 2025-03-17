import authorizedAxios from '../util/authorizeAxios'
import { BASE_URL } from '../util/constant'
import { toast } from 'react-toastify'

export const registerUserAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${BASE_URL}/user/v1/register`,
    data
  )
  toast.success('Account created successfully!')
  return response.data
}

export const refreshTokenAPI = async (token) => {
  const data = { token: token }
  const response = await authorizedAxiosInstance.get(
    `${API_ROOT}/auth/refresh`,
    data
  )
  return response.data
}