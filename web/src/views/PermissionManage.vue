<template>
  <div class="page-container">
    <el-card class="app-card section" shadow="never">
      <el-row :gutter="20">
        <!-- 左侧：角色列表 -->
        <el-col :span="8">
          <div class="section-title">角色列表</div>
          <el-table 
            :data="roles" 
            stripe
            border
            highlight-current-row
            @current-change="handleRoleChange"
            v-loading="roleLoading">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="name" label="角色名称" />
          </el-table>
        </el-col>
        
        <!-- 右侧：菜单权限树 -->
        <el-col :span="16">
          <div class="section-title">
            <span>菜单权限分配</span>
            <el-button 
              v-if="currentRole" 
              type="primary" 
              size="small" 
              @click="savePermissions"
              style="margin-left: 10px">
              保存权限
            </el-button>
          </div>
          <el-card v-if="currentRole" shadow="never" v-loading="menuLoading">
            <div style="margin-bottom: 10px">
              <strong>当前角色：{{ currentRole.name }}</strong>
            </div>
            <el-tree
              ref="menuTreeRef"
              :data="menuTree"
              :props="{ children: 'children', label: 'name' }"
              show-checkbox
              node-key="id"
              :default-expand-all="true"
            />
          </el-card>
          <el-empty v-else description="请先选择一个角色" />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { roleAPI } from '../api/role'
import { menuAPI } from '../api/menu'

const roles = ref([])
const roleLoading = ref(false)
const currentRole = ref(null)
const menuTree = ref([])
const checkedMenuIds = ref([])
const menuTreeRef = ref(null)
const menuLoading = ref(false)

async function loadRoles() {
  roleLoading.value = true
  try {
    const res = await roleAPI.list()
    roles.value = res.data || []
  } catch (e) {
    ElMessage.error('加载角色列表失败')
    console.error(e)
  } finally {
    roleLoading.value = false
  }
}

async function loadMenus() {
  try {
    // 直接使用后端返回的树形结构
    const res = await menuAPI.tree()
    menuTree.value = res.data || []
  } catch (e) {
    ElMessage.error('加载菜单列表失败')
    console.error(e)
  }
}

async function loadRoleMenus(roleId) {
  menuLoading.value = true
  try {
    const res = await roleAPI.getMenus(roleId)
    checkedMenuIds.value = res.data || []
    // 等待 DOM 更新后设置选中状态
    await nextTick()
    if (menuTreeRef.value) {
      menuTreeRef.value.setCheckedKeys(checkedMenuIds.value)
    }
  } catch (e) {
    console.error('加载角色菜单失败', e)
    ElMessage.error('加载角色菜单权限失败')
    checkedMenuIds.value = []
    if (menuTreeRef.value) {
      menuTreeRef.value.setCheckedKeys([])
    }
  } finally {
    menuLoading.value = false
  }
}

async function handleRoleChange(role) {
  currentRole.value = role
  if (role) {
    await loadRoleMenus(role.id)
  } else {
    checkedMenuIds.value = []
    if (menuTreeRef.value) {
      menuTreeRef.value.setCheckedKeys([])
    }
  }
}

async function savePermissions() {
  if (!currentRole.value) {
    ElMessage.warning('请先选择一个角色')
    return
  }
  
  if (!menuTreeRef.value) {
    ElMessage.warning('菜单树未加载')
    return
  }
  
  try {
    // 获取所有选中的节点（包括半选中的父节点）
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    // 合并所有选中的菜单ID（包括半选中的父节点，因为它们也需要保存）
    const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
    
    await roleAPI.assignMenus(currentRole.value.id, allCheckedKeys)
    ElMessage.success('权限保存成功')
    // 保存成功后，更新已选中的菜单ID列表
    checkedMenuIds.value = allCheckedKeys
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '保存权限失败')
    console.error(e)
  }
}

onMounted(() => {
  loadRoles()
  loadMenus()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>

