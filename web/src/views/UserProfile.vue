<template>
  <div class="page-container">
    <el-card class="app-card section" shadow="never">
      <template #header>
        <div style="display: flex; align-items: center; gap: 10px">
          <el-icon style="font-size: 20px"><User /></el-icon>
          <span style="font-size: 18px; font-weight: 600">个人信息</span>
        </div>
      </template>

      <div v-loading="loading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户编号">
            {{ userInfo.id || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名称">
            {{ userInfo.username || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="用户昵称">
            {{ userInfo.realName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号码">
            {{ userInfo.phone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ userInfo.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ getGenderText(userInfo.gender) }}
          </el-descriptions-item>
          <el-descriptions-item label="归属部门">
            {{ userInfo.deptName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="岗位">
            {{ userInfo.postName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
              {{ userInfo.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="数据权限">
            {{ userInfo.dataScopeText || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ userInfo.createdAt || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ userInfo.remark || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 角色信息 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <span style="font-weight: 600">角色信息</span>
          </template>
          <div v-if="userInfo.roles && userInfo.roles.length > 0">
            <el-tag
              v-for="role in userInfo.roles"
              :key="role.id"
              style="margin-right: 10px; margin-bottom: 10px"
              size="large"
            >
              {{ role.name }} ({{ role.code }})
            </el-tag>
          </div>
          <el-empty v-else description="暂无角色" :image-size="80" />
        </el-card>

        <!-- 权限信息 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <span style="font-weight: 600">权限信息</span>
          </template>
          <div v-if="userInfo.permissions && userInfo.permissions.length > 0" class="permissions-container">
            <el-tag
              v-for="permission in userInfo.permissions"
              :key="permission"
              style="margin-right: 10px; margin-bottom: 10px"
              type="info"
            >
              {{ permission }}
            </el-tag>
          </div>
          <el-empty v-else description="暂无权限" :image-size="80" />
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userAPI } from '../api/user'

const loading = ref(false)
const userInfo = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  gender: '2',
  deptName: '',
  postName: '',
  roles: [],
  permissions: [],
  dataScope: '',
  dataScopeText: '',
  status: 1,
  remark: '',
  createdAt: ''
})

function getGenderText(gender) {
  const map = {
    '0': '男',
    '1': '女',
    '2': '未知'
  }
  return map[gender] || '未知'
}

async function loadUserInfo() {
  loading.value = true
  try {
    const res = await userAPI.getCurrent()
    if (res.data) {
      userInfo.value = res.data
    }
  } catch (e) {
    ElMessage.error('加载个人信息失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

.permissions-container {
  max-height: 400px;
  overflow-y: auto;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  width: 150px;
}
</style>

