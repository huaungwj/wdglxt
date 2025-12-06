import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Categories from '../views/Categories.vue'
import Docs from '../views/Docs.vue'
import DocList from '../views/DocList.vue'
import DocUpload from '../views/DocUpload.vue'
import DocPreview from '../views/DocPreview.vue'
import UserManage from '../views/UserManage.vue'
import DeptManage from '../views/DeptManage.vue'
import RoleManage from '../views/RoleManage.vue'
import MenuManage from '../views/MenuManage.vue'
import DictManage from '../views/DictManage.vue'
import PermissionManage from '../views/PermissionManage.vue'
import PostManage from '../views/PostManage.vue'
import UserProfile from '../views/UserProfile.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/', component: Home, meta: { requiresAuth: true } },
  { path: '/categories', component: Categories, meta: { requiresAuth: true } },
  { path: '/docs', component: DocList, meta: { requiresAuth: true } },
  { path: '/docs/upload', component: DocUpload, meta: { requiresAuth: true } },
  { path: '/docs/preview/:id', component: DocPreview, meta: { requiresAuth: true } },
  { path: '/users', component: UserManage, meta: { requiresAuth: true } },
  { path: '/depts', component: DeptManage, meta: { requiresAuth: true } },
  { path: '/roles', component: RoleManage, meta: { requiresAuth: true } },
  { path: '/menus', component: MenuManage, meta: { requiresAuth: true } },
  { path: '/dicts', component: DictManage, meta: { requiresAuth: true } },
  { path: '/permissions', component: PermissionManage, meta: { requiresAuth: true } },
  { path: '/posts', component: PostManage, meta: { requiresAuth: true } },
  { path: '/profile', component: UserProfile, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：检查登录状态
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 如果访问登录页，直接允许
  if (to.path === '/login') {
    // 如果已登录，跳转到首页
    if (token) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 需要认证的页面，检查token
  if (to.meta.requiresAuth) {
    if (!token) {
      // 没有token，跳转到登录页
      next('/login')
    } else {
      // 有token，允许访问
      next()
    }
  } else {
    // 不需要认证的页面，直接允许
    next()
  }
})

export default router
