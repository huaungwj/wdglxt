import request from './request'

export const authAPI = {
    // 登录
    login(username, password) {
        return request.post('/api/auth/login', { username, password })
    }
}
