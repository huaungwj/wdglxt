import { ref, computed } from 'vue'
import { permissionAPI } from '../api/permission'

// 全局权限状态 - 使用数组而不是Set，确保响应式追踪
const permissions = ref([])
const loading = ref(false)
let permissionsLoaded = false

/**
 * 权限管理 composable
 * 用于检查用户权限、控制按钮和菜单显示
 */
export function usePermission() {
    // 加载用户权限
    const loadPermissions = async (force = false) => {
        // 如果强制加载，先清除状态
        if (force) {
            permissions.value = []
            permissionsLoaded = false
        }
        
        if (permissionsLoaded && permissions.value.length > 0) {
            return // 已经加载过，直接返回
        }
        
        loading.value = true
        try {
            const res = await permissionAPI.getCurrentUserPermissions()
            // 确保返回的是数组
            permissions.value = Array.isArray(res.data) ? res.data : []
            permissionsLoaded = true
            console.log('权限加载成功:', permissions.value)
        } catch (error) {
            console.error('加载权限失败', error)
            permissions.value = []
            permissionsLoaded = false
        } finally {
            loading.value = false
        }
    }

    // 检查是否有某个权限 - 使用computed确保响应式
    const hasPermission = (permissionCode) => {
        // 直接检查权限数组，这样 watchEffect 可以响应式地追踪
        // 如果权限未加载，loadPermissions 会在后台加载，但这里先返回 false
        if (!permissionsLoaded) {
            // 如果权限未加载，先加载（异步，不阻塞）
            loadPermissions()
            return false // 暂时返回false，等待权限加载完成
        }
        return permissions.value.includes(permissionCode)
    }

    // 检查是否有任意一个权限
    const hasAnyPermission = (permissionCodes) => {
        if (!Array.isArray(permissionCodes)) {
            return false
        }
        if (!permissionsLoaded) {
            loadPermissions()
            return false
        }
        return permissionCodes.some(code => permissions.value.includes(code))
    }

    // 检查是否有所有权限
    const hasAllPermissions = (permissionCodes) => {
        if (!Array.isArray(permissionCodes)) {
            return false
        }
        if (!permissionsLoaded) {
            loadPermissions()
            return false
        }
        return permissionCodes.every(code => permissions.value.includes(code))
    }

    // 清除权限（用于退出登录时）
    const clearPermissions = () => {
        permissions.value = []
        permissionsLoaded = false
    }

    return {
        permissions,
        loading,
        loadPermissions,
        hasPermission,
        hasAnyPermission,
        hasAllPermissions,
        clearPermissions
    }
}

