import axios from 'axios'
import { BASE_URL } from './constant'
import { toast } from 'react-toastify'


export const axiosPublic = axios.create({
  baseURL: BASE_URL,
  timeout: 1000 * 60 * 10,
  withCredentials: false
})


axiosPublic.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    let errorMessage = error?.message
    toast.error(errorMessage)

    return Promise.reject(error)
  }
)
