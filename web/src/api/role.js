import request from './request'

export const roleAPI = {
    list() {
        return request.get('/api/roles')
    },
    getById(id) {
        return request.get(`/api/roles/${id}`)
    },
    create(data) {
        return request.post('/api/roles', data)
    },
    update(id, data) {
        return request.put(`/api/roles/${id}`, data)
    },
    delete(id) {
        return request.delete(`/api/roles/${id}`)
    },
    // 获取角色的菜单ID列表
    getMenus(roleId) {
        return request.get(`/api/roles/${roleId}/menus`)
    },
    // 分配角色菜单权限
    assignMenus(roleId, menuIds) {
        return request.post(`/api/roles/${roleId}/menus`, { roleId, menuIds })
    },
    // 获取角色的部门ID列表（自定义数据权限）
    getDepts(roleId) {
        return request.get(`/api/roles/${roleId}/depts`)
    },
    // 分配角色部门权限（自定义数据权限）
    assignDepts(roleId, deptIds) {
        return request.post(`/api/roles/${roleId}/depts`, { roleId, deptIds })
    }
}

