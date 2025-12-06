import request from './request'

export const userAPI = {
    list(params) {
        return request.get('/api/users', { params })
    },
    getById(id) {
        return request.get(`/api/users/${id}`)
    },
    create(data) {
        return request.post('/api/users', data)
    },
    update(id, data) {
        return request.put(`/api/users/${id}`, data)
    },
    delete(id) {
        return request.delete(`/api/users/${id}`)
    },
    // 获取用户的角色ID列表
    getRoles(userId) {
        return request.get(`/api/users/${userId}/roles`)
    },
    // 分配用户角色
    assignRoles(userId, roleIds) {
        return request.post(`/api/users/${userId}/roles`, { userId, roleIds })
    },
    // 获取当前用户信息
    getCurrent() {
        return request.get('/api/users/current')
    }
}
