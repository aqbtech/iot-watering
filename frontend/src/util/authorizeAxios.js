import axios from 'axios'
import { toast } from 'react-toastify'
import { logoutUserAPI } from '../redux/Slices/userSlice'
import { refreshTokenAPI } from '../apis/deviceApi'
import { BASE_URL } from '../util/constant'

/**
 * Không thể import { store } from '~/redux/store' theo cách thông thường như các file jsx component
 * Giải pháp: Inject store: là kỹ thuật khi cần sử dụng biến redux store ở các file ngoài phạm vi react component như file authorizeAxios hiện tại
 * Hiểu đơn giản: khi ứng dụng bắt đầu chạy lên, code sẽ chạy vào main.jsx đầu tiên, từ bên đó chúng ta gọi hàm injectStore ngay lập tức để gán biến mainStore vào biến axiosReduxStore cục bộ trong file này.
 * https://redux.js.org/faq/code-structure#how-can-i-use-the-redux-store-in-non-component-files
 */
let axiosReduxStore
export const injectStore = (mainStore) => {
  axiosReduxStore = mainStore
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
  (error) => {
    const currentToken = axiosReduxStore.getState().user.currentUser?.token
    if (error.response.status === 401 && currentToken) {
      try {
        const refreshToken = currentToken // Sử dụng token hiện tại như refresh token
        const body = { refreshToken: refreshToken }

        // Gọi đến endpoint /refresh để lấy token mới
        const res = authorizedAxios.post('/auth/refresh', body)
        const newAccessToken = res.data.result.token

        if (newAccessToken) {
          //cập nhật lại redux
          const currentUser = axiosReduxStore.getState().user.currentUser
          axiosReduxStore.dispatch(
            updateCurrentUser({ ...currentUser, token: newAccessToken })
          )

          // Lưu token mới vào cookie và cập nhật lại header cho request gốc
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`

          // Gọi lại request gốc với token mới
          return authorizedAxios(originalRequest)
        } else {
          throw new Error('Không lấy được token mới.')
        }
      } catch (error) {
        axiosReduxStore.dispatch(logoutUserAPI())
        toast.error('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại.')
        return Promise.reject(refreshError)
      }
    }

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
