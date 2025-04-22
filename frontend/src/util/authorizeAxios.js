import axios from 'axios'
import { toast } from 'react-toastify'
import { updateCurrentUser } from '../redux/Slices/userSlice'
import { refreshTokenAPI } from '../apis/deviceApi'
import { BASE_URL } from '../util/constant'

let axiosReduxStore
export const injectStore = (mainStore) => {
  axiosReduxStore = mainStore
}

const redirectToLogin = () => {
  window.location.href = '/Login'
}

let authorizedAxios = axios.create({})

authorizedAxios.defaults.timeout = 1000 * 60 * 10 //10 min
authorizedAxios.defaults.withCredentials = false // đính kèm cookies

// đánh chặn khi gửi requested
authorizedAxios.interceptors.request.use(
  (config) => {
    const token = axiosReduxStore.getState().user.currentUser?.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
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
  async (error) => {
    const currentToken = axiosReduxStore.getState().user.currentUser?.token

    if (error.response?.status === 401 && currentToken) {
      try {
        const refreshToken = currentToken // Sử dụng token hiện tại như refresh token

        // Kiểm tra nếu request đã retry rồi thì không retry nữa để tránh vòng lặp vô hạn
        if (error.config._retry) {
          navigate('/login')
          return Promise.reject(error)
        }
        error.config._retry = true

        const response = await refreshTokenAPI(refreshToken)
        const newAccessToken = response.result.token
        if (newAccessToken) {
          // Cập nhật Redux
          const currentUser = axiosReduxStore.getState().user.currentUser
          axiosReduxStore.dispatch(updateCurrentUser({ ...currentUser, token: newAccessToken }))

          // Cập nhật token mới vào header và gọi lại request
          error.config.headers.Authorization = `Bearer ${newAccessToken}`
          return authorizedAxios(error.config)
        } else {
          axiosReduxStore.dispatch(updateCurrentUser(null))
          redirectToLogin()
          throw new Error('Token không hợp lệ vui lòng đăng nhập lại')
        }
      } catch (error) {
        return Promise.reject(error)
      }
    }

    // Xử lý lỗi chung
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