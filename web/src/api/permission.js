import request from './request'

export const permissionAPI = {
    // 获取当前用户的所有权限
    getCurrentUserPermissions() {
        return request.get('/api/permissions/current')
    },
    
    // 检查是否有某个权限
    checkPermission(permissionCode) {
        return request.get('/api/permissions/check', {
            params: { permissionCode }
        })
    }
}

