import axios from 'axios'
import { toast } from 'react-toastify'

let authorizedAxios = axios.create({})

authorizedAxios.defaults.timeout = 1000 * 60 * 10 //10 min
authorizedAxios.defaults.withCredentials = true // đính kèm cookies

// đánh chặn khi gửi requested
authorizedAxios.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// đánh chặn khi nhận response
authorizedAxios.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    //bắt lỗi tập trung
    let errorMessage = error?.message
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    }
    if (error.response?.status !== 410) {
      toast.error(errorMessage)
    }

    return Promise.reject(error)
  }
)

export default authorizedAxios
