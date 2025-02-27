import authorizedAxios from '../util/authorizeAxios'
import { BASE_URL } from '../util/constant'

export const registerUserAPI = async (data) => {
  const response = await authorizedAxios.post(
    `${API_ROOT}/v1/users/register`,
    data
  )
  toast.success('Account created successfully!')
  return response.data
}
