import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 20000
})

// 请求拦截器：添加Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：处理响应和错误
request.interceptors.response.use(
  (res) => {
    const data = res.data
    // 后端统一返回格式 { code, msg, data }
    if (data.code !== undefined && data.code !== 0) {
      ElMessage.error(data.msg || '请求失败')
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
    return data
  },
  (err) => {
    // 处理401未授权错误（JWT失效、登录超时、缺少token等）
    if (err.response?.status === 401) {
      // 清除本地存储的token和用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('realName')
      
      const errorMsg = err.response?.data?.msg || '登录已过期，请重新登录'
      ElMessage.error(errorMsg)
      
      // 跳转到登录页
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
      return Promise.reject(err)
    }
    
    // 处理500服务器错误
    if (err.response?.status === 500) {
      const errorMsg = err.response?.data?.msg || '服务器内部错误，请稍后重试'
      ElMessage.error(errorMsg)
      return Promise.reject(err)
    }
    
    // 处理其他错误
    const msg = err.response?.data?.msg || err.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(err)
  }
)

export default request
