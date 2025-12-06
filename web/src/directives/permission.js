import { usePermission } from '../composables/usePermission'
import { watchEffect } from 'vue'

/**
 * 权限指令
 * 用法：v-permission="'file:upload'" 或 v-permission="['file:upload', 'file:download']"
 * 
 * 如果用户没有权限，元素将被隐藏
 * 支持响应式更新：当权限加载完成后，会自动重新检查并显示/隐藏元素
 */
export default {
    mounted(el, binding) {
        const { permissions, loadPermissions } = usePermission()
        
        // 确保权限已加载
        loadPermissions()
        
        // 使用 watchEffect 响应式地监听权限变化
        // 直接访问 permissions.value 确保 watchEffect 能够追踪到变化
        const stopWatcher = watchEffect(() => {
            // 直接读取 permissions.value，这样 watchEffect 可以追踪到变化
            const permissionList = permissions.value
            let hasAccess = false
            
            if (Array.isArray(binding.value)) {
                // 如果是数组，检查是否有任意一个权限
                hasAccess = binding.value.some(code => permissionList.includes(code))
            } else if (typeof binding.value === 'string') {
                // 如果是字符串，检查是否有该权限
                hasAccess = permissionList.includes(binding.value)
            }
            
            // 根据权限显示或隐藏元素
            if (!hasAccess) {
                el.style.display = 'none'
            } else {
                el.style.display = ''
            }
        })
        
        // 将停止函数保存到元素上，以便在卸载时清理
        el._permissionWatcher = stopWatcher
    },
    updated(el, binding) {
        // updated 钩子仍然需要，因为 binding.value 可能改变
        // 但 watchEffect 已经会处理权限变化，这里只需要处理 binding.value 变化的情况
        // 实际上 watchEffect 也会追踪 binding.value，所以这里可以简化
        // 为了保持一致性，我们仍然检查一次
        const { permissions } = usePermission()
        const permissionList = permissions.value
        
        let hasAccess = false
        
        if (Array.isArray(binding.value)) {
            hasAccess = binding.value.some(code => permissionList.includes(code))
        } else if (typeof binding.value === 'string') {
            hasAccess = permissionList.includes(binding.value)
        }
        
        if (!hasAccess) {
            el.style.display = 'none'
        } else {
            el.style.display = ''
        }
    },
    unmounted(el) {
        // 清理 watcher
        if (el._permissionWatcher) {
            el._permissionWatcher()
            delete el._permissionWatcher
        }
    }
}

