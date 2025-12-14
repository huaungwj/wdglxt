<template>
  <!-- 登录页面：全屏显示，不显示布局 -->
  <router-view v-if="$route.path === '/login'" />
  
  <!-- 其他页面：显示完整布局 -->
  <el-container v-else style="height: 100vh">
    <el-aside width="216px" class="app-aside">
      <div class="logo">文档管理</div>
      <el-menu :default-active="$route.path" router background-color="#ffffff" text-color="#303133" active-text-color="#409eff">
        <el-menu-item index="/">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-sub-menu index="system" v-if="permissions.includes('menu:system')">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/users" v-if="permissions.includes('menu:user')">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/depts" v-if="permissions.includes('menu:dept')">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
          <el-menu-item index="/roles" v-if="permissions.includes('menu:role')">
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/menus" v-if="permissions.includes('menu:menu')">
            <el-icon><Menu /></el-icon>
            <span>菜单管理</span>
          </el-menu-item>
          <!-- <el-menu-item index="/dicts" v-if="permissions.includes('menu:dict')">
            <el-icon><Collection /></el-icon>
            <span>字典管理</span>
          </el-menu-item> -->
          <el-menu-item index="/permissions" v-if="permissions.includes('menu:permission')">
            <el-icon><Lock /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
          <el-menu-item index="/posts" v-if="permissions.includes('menu:post')">
            <el-icon><Briefcase /></el-icon>
            <span>岗位管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/categories" v-if="permissions.includes('menu:category')">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/docs" v-if="permissions.includes('menu:document')">
          <el-icon><Document /></el-icon>
          <span>文档信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="app-header" style="display: flex; align-items: center; justify-content: space-between; padding: 0 20px">
        <div class="title">{{ pageTitle }}</div>
        <div class="meta" style="display:flex; align-items:center; gap:10px">
          <el-avatar size="small">{{ userRealName.charAt(0) || '用' }}</el-avatar>
          <span>{{ userRealName || '用户' }}</span>
          <el-dropdown @command="handleCommand">
            <el-icon style="cursor: pointer; font-size: 18px;"><ArrowDown /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main style="background: #f0f2f5; padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { House, Folder, Document, Setting, User, OfficeBuilding, UserFilled, Menu, Collection, ArrowDown, Lock, Briefcase } from '@element-plus/icons-vue'
import { useUserStore } from './stores/user'
import { usePermission } from './composables/usePermission'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { permissions, loadPermissions } = usePermission()

const userRealName = computed(() => userStore.realName || localStorage.getItem('realName') || '用户')

// 页面加载时获取权限
onMounted(async () => {
    await loadPermissions()
})

const pageTitle = computed(() => {
  const map = { 
    '/': '首页', 
    '/categories': '分类管理', 
    '/docs': '文档信息',
    '/docs/upload': '文件上传',
    '/docs/preview': '文档预览',
    '/users': '用户管理',
    '/depts': '部门管理',
    '/roles': '角色管理',
    '/menus': '菜单管理',
    '/dicts': '字典管理',
    '/permissions': '权限管理',
    '/posts': '岗位管理',
    '/profile': '个人信息'
  }
  // 处理动态路由
  if (route.path.startsWith('/docs/preview/')) {
    return '文档预览'
  }
  return map[route.path] || '文档管理系统'
})

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }).then(() => {
      userStore.clearUser()
      const { clearPermissions } = usePermission()
      clearPermissions()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  color: #303133;
  border-bottom: 1px solid #dcdfe6;
}
.el-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
