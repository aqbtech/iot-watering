import axios from 'axios'
import { toast } from 'react-toastify'
import { updateCurrentUser } from '../redux/Slices/userSlice'
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
  (error) => {
    const currentToken = axiosReduxStore.getState().user.currentUser?.token

    if (error.response?.status === 401 && currentToken) {
      const refreshToken = currentToken // Sử dụng token hiện tại như refresh token

      // Kiểm tra nếu request đã retry rồi thì không retry nữa để tránh vòng lặp vô hạn
      if (error.config._retry) {
        navigate('/login')
        return Promise.reject(error)
      }
      error.config._retry = true

      return refreshTokenAPI(refreshToken)
        .then((res) => {
          const newAccessToken = res.result.token
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
            return Promise.reject(error)
          }
        })
        .catch((err) => {
          axiosReduxStore.dispatch(updateCurrentUser(null))
          redirectToLogin()
          return Promise.reject(err)
        })
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
