<template>
  <div class="login-container">
    <!-- 背景图片 -->
    <div class="login-bg">
      <img src="../assets/images/login-bg.png" alt="背景" class="bg-image" />
    </div>
    
    <!-- 内容区域 -->
    <div class="login-content">
      <!-- 左侧Logo -->
      <div class="login-left">
        <div class="login-logo">
          <img src="../assets/images/logo-chatu.png" alt="Logo" />
        </div>
      </div>
      
      <!-- 右侧登录卡片 -->
      <div class="login-right">
        <div class="login-card">
          <div class="login-header">
            <h1>文档管理系统</h1>
            <p>Document Management System</p>
          </div>
          
          <el-form :model="loginForm" :rules="rules" ref="formRef" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleLogin"
                class="login-button"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '../api/auth'
import { useUserStore } from '../stores/user'
import { usePermission } from '../composables/usePermission'

const router = useRouter()
const userStore = useUserStore()
const { loadPermissions } = usePermission()
const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await authAPI.login(loginForm.username, loginForm.password)
        // 后端返回格式：{ code: 0, msg: 'ok', data: { token, username, realName } }
        if (res.data) {
          userStore.setUser(res.data.token, res.data.username, res.data.realName)
          // 登录成功后立即加载权限（强制重新加载）
          await loadPermissions(true)
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error('登录失败：响应数据格式错误')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 背景图片 */
.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 内容区域 */
.login-content {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 80px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 左侧Logo区域 */
.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-right: 60px;
}

.login-logo {
  text-align: center;
}

.login-logo img {
  max-width: 100%;
  height: auto;
  max-height: 500px;
}

/* 右侧登录卡片 */
.login-right {
  flex: 0 0 420px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.login-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 30px;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.login-tip {
  margin-top: 20px;
  text-align: center;
  color: #999;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-content {
    flex-direction: column;
    padding: 40px 20px;
  }
  
  .login-left {
    padding-right: 0;
    margin-bottom: 40px;
  }
  
  .login-logo img {
    max-height: 300px;
  }
  
  .login-right {
    flex: 1;
    width: 100%;
    max-width: 420px;
  }
}
</style>
