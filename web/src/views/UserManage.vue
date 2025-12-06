<template>
  <div class="page-container">
    <el-row :gutter="20">
      <!-- 左侧：部门树 -->
      <el-col :span="4">
        <el-card class="app-card section" shadow="never" style="height: calc(100vh - 120px)">
          <template #header>
            <span>部门</span>
          </template>
          <el-input
            v-model="deptFilterText"
            placeholder="请输入部门名称"
            clearable
            style="margin-bottom: 10px"
            prefix-icon="Search"
          />
          <el-tree
            ref="deptTreeRef"
            :data="deptTree"
            :props="{ children: 'children', label: 'name' }"
            :filter-node-method="filterDeptNode"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleDeptClick"
            style="overflow-y: auto"
          />
        </el-card>
      </el-col>

      <!-- 右侧：用户列表 -->
      <el-col :span="20">
        <!-- 搜索栏 -->
        <el-card class="app-card section" shadow="never" style="margin-bottom: 16px">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="用户名称">
              <el-input v-model="searchForm.username" placeholder="请输入用户名称" clearable style="width: 200px" />
            </el-form-item>
            <el-form-item label="手机号码">
              <el-input v-model="searchForm.phone" placeholder="请输入手机号码" clearable style="width: 200px" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="用户状态" clearable style="width: 150px">
                <el-option label="正常" value="1" />
                <el-option label="停用" value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="创建时间">
              <el-date-picker
                v-model="searchForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 240px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
              <el-button :icon="Refresh" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 操作按钮和表格 -->
        <el-card class="app-card section" shadow="never">
          <div style="margin-bottom: 16px">
            <el-button type="primary" :icon="Plus" @click="openAdd">新增</el-button>
            <el-button type="danger" :icon="Delete" :disabled="selectedRows.length === 0" @click="handleDelete">删除</el-button>
          </div>

          <el-table
            ref="tableRef"
            :data="list"
            class="table-elegant"
            style="width:100%"
            stripe
            border
            size="large"
            v-loading="loading"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column prop="id" label="用户编号" width="100" align="center" />
            <el-table-column prop="username" label="用户名称" width="150" />
            <el-table-column prop="realName" label="用户昵称" width="150" />
            <el-table-column prop="deptName" label="部门" width="150" />
            <el-table-column prop="phone" label="手机号码" width="150" />
            <el-table-column prop="status" label="状态" align="center">
              <template #default="{ row }">
                <el-switch
                  v-model="row.status"
                  :active-value="1"
                  :inactive-value="0"
                  active-text="正常"
                  inactive-text="停用"
                  :disabled="isAdmin(row)"
                  @change="(newStatus) => handleStatusChange(row, newStatus)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
              <template #default="{ row }">
                {{ formatDateTime(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center" fixed="right">
              <template #default="{ row }">
                <el-button 
                  type="primary" 
                  link 
                  size="small" 
                  :disabled="isAdmin(row)"
                  @click="openEdit(row)"
                >
                  修改
                </el-button>
                <el-button 
                  type="danger" 
                  link 
                  size="small" 
                  :disabled="isAdmin(row)"
                  @click="onDelete(row)"
                >
                  删除
                </el-button>
                <!-- <el-button type="info" link size="small">
                  更多
                  <el-icon><ArrowRight /></el-icon>
                </el-button> -->
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div style="margin-top: 16px; display: flex; justify-content: flex-end">
            <el-pagination
              :current-page="pagination.page"
              :page-size="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.mode === 'add' ? '添加用户' : '修改用户'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="dialog.form" label-width="120px" :rules="dialog.rules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="realName">
              <el-input v-model="dialog.form.realName" placeholder="请输入用户昵称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select
                v-model="dialog.form.deptId"
                :data="deptTree"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                check-strictly
                clearable
                placeholder="请选择归属部门"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="dialog.form.phone" placeholder="请输入手机号码" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="dialog.form.email" placeholder="请输入邮箱" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名称" prop="username">
              <el-input
                v-model="dialog.form.username"
                placeholder="请输入用户名称"
                clearable
                :disabled="dialog.mode === 'edit'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户密码" prop="password" :required="dialog.mode === 'add'">
              <el-input
                v-model="dialog.form.password"
                type="password"
                placeholder="请输入用户密码"
                clearable
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户性别" prop="gender">
              <el-select v-model="dialog.form.gender" placeholder="请选择性别" clearable style="width: 100%">
                <el-option label="男" value="0" />
                <el-option label="女" value="1" />
                <el-option label="未知" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="dialog.form.status">
                <el-radio :label="1">正常</el-radio>
                <el-radio :label="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="岗位" prop="postId">
              <el-select v-model="dialog.form.postId" placeholder="请选择岗位" clearable style="width: 100%">
                <el-option
                  v-for="post in posts"
                  :key="post.id"
                  :label="post.postName"
                  :value="post.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleIds">
              <el-select
                v-model="dialog.form.roleIds"
                placeholder="请选择角色"
                multiple
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="role in roles"
                  :key="role.id"
                  :label="role.name"
                  :value="role.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="dialog.form.remark"
                type="textarea"
                :rows="3"
                placeholder="请输入内容"
                resize="none"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Plus, Edit, Delete, Search, Refresh, Upload, Download, ArrowRight } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { userAPI } from '../api/user'
import { deptAPI } from '../api/dept'
import { roleAPI } from '../api/role'
import { postAPI } from '../api/post'

const list = ref([])
const deptTree = ref([])
const deptFilterText = ref('')
const deptTreeRef = ref(null)
const selectedRows = ref([])
const tableRef = ref(null)
const formRef = ref(null)
const loading = ref(false)
const posts = ref([])
const roles = ref([])

const searchForm = ref({
  username: '',
  phone: '',
  status: '',
  dateRange: null,
  deptId: null
})

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const dialog = ref({
  visible: false,
  mode: 'add',
  form: {
    id: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    gender: '2',
    deptId: null,
    postId: null,
    roleIds: [],
    status: 1,
    remark: ''
  },
  rules: {
    username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
    realName: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
    password: [
      { required: true, message: '请输入用户密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ]
  }
})

// 监听部门搜索
watch(deptFilterText, (val) => {
  deptTreeRef.value?.filter(val)
})

function filterDeptNode(value, data) {
  if (!value) return true
  return data.name.includes(value)
}

function handleDeptClick(data) {
  // 设置选中的部门ID
  searchForm.value.deptId = data.id
  pagination.value.page = 1
  load()
}

async function load() {
  loading.value = true
  try {
    const params = {
      username: searchForm.value.username || undefined,
      phone: searchForm.value.phone || undefined,
      status: searchForm.value.status ? parseInt(searchForm.value.status) : undefined,
      deptId: searchForm.value.deptId || undefined
    }
    
    // 处理创建时间范围
    if (searchForm.value.dateRange && Array.isArray(searchForm.value.dateRange) && searchForm.value.dateRange.length === 2) {
      const [startDate, endDate] = searchForm.value.dateRange
      if (startDate) {
        params.startDate = formatDate(startDate)
      }
      if (endDate) {
        params.endDate = formatDate(endDate)
      }
    }
    
    // 移除 undefined 值
    Object.keys(params).forEach(key => {
      if (params[key] === undefined) {
        delete params[key]
      }
    })
    const res = await userAPI.list(params)
    const users = res.data || []
    
    // 加载部门信息用于显示
    const deptRes = await deptAPI.list()
    const deptMap = new Map((deptRes.data || []).map(d => [d.id, d.name]))
    
    // 加载岗位信息
    const postRes = await postAPI.list()
    const postMap = new Map((postRes.data || []).map(p => [p.id, p.postName]))
    
    list.value = users.map(user => ({
      ...user,
      deptName: user.deptId ? deptMap.get(user.deptId) : '',
      postName: user.postId ? postMap.get(user.postId) : ''
    }))
    
    pagination.value.total = list.value.length
  } catch (e) {
    ElMessage.error('加载用户列表失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadDeptTree() {
  try {
    const res = await deptAPI.tree()
    deptTree.value = res.data || []
  } catch (e) {
    console.error('加载部门树失败', e)
  }
}

async function loadPosts() {
  try {
    const res = await postAPI.list()
    posts.value = res.data || []
  } catch (e) {
    console.error('加载岗位列表失败', e)
  }
}

async function loadRoles() {
  try {
    const res = await roleAPI.list()
    roles.value = res.data || []
  } catch (e) {
    console.error('加载角色列表失败', e)
  }
}

function handleSearch() {
  pagination.value.page = 1
  load()
}

function handleReset() {
  searchForm.value = {
    username: '',
    phone: '',
    status: '',
    dateRange: null,
    deptId: null
  }
  deptTreeRef.value?.setCurrentKey(null)
  handleSearch()
}

// 判断是否是系统管理员
function isAdmin(user) {
  return user && (user.username === 'admin' || user.id === 1)
}

function handleSelectionChange(selection) {
  selectedRows.value = selection
}

function handleModify() {
  if (selectedRows.value.length === 1) {
    openEdit(selectedRows.value[0])
  }
}

function handleDelete() {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  
  // 过滤掉系统管理员
  const deletableUsers = selectedRows.value.filter(row => !isAdmin(row))
  const adminUsers = selectedRows.value.filter(row => isAdmin(row))
  
  if (deletableUsers.length === 0) {
    ElMessage.warning('系统管理员不允许删除')
    return
  }
  
  if (adminUsers.length > 0) {
    ElMessage.warning(`已过滤 ${adminUsers.length} 个系统管理员，将删除 ${deletableUsers.length} 个用户`)
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${deletableUsers.length} 个用户吗？`,
    '删除确认',
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  ).then(async () => {
    try {
      for (const row of deletableUsers) {
        await userAPI.delete(row.id)
      }
      ElMessage.success('删除成功')
      await load()
      selectedRows.value = []
    } catch (e) {
      ElMessage.error(e.response?.data?.msg || '删除失败')
    }
  }).catch(() => {})
}

async function handleStatusChange(row, newStatus) {
  if (isAdmin(row)) {
    // 回滚状态
    row.status = newStatus === 1 ? 0 : 1
    ElMessage.warning('系统管理员不允许禁用')
    return
  }
  
  // 保存原始状态，用于失败时回滚
  const originalStatus = newStatus === 1 ? 0 : 1
  
  try {
    // 调用API更新状态
    await userAPI.update(row.id, { status: newStatus })
    ElMessage.success('状态更新成功')
  } catch (e) {
    // 更新失败，回滚状态
    row.status = originalStatus
    ElMessage.error(e.response?.data?.msg || '状态更新失败')
  }
}

function handleSizeChange(size) {
  pagination.value.size = size
  load()
}

function handlePageChange(page) {
  pagination.value.page = page
  load()
}

function formatDateTime(dateTime) {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
}

function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function openAdd() {
  dialog.value = {
    visible: true,
    mode: 'add',
    form: {
      id: null,
      username: '',
      password: '',
      realName: '',
      phone: '',
      email: '',
      gender: '2',
      deptId: null,
      postId: null,
      roleIds: [],
      status: 1,
      remark: ''
    },
    rules: {
      username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
      realName: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
      password: [
        { required: true, message: '请输入用户密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }
  }
}

async function openEdit(row) {
  if (isAdmin(row)) {
    ElMessage.warning('系统管理员不允许修改')
    return
  }
  
  try {
    // 获取用户详情
    const userRes = await userAPI.getById(row.id)
    const user = userRes.data
    
    // 获取用户角色
    const roleRes = await userAPI.getRoles(row.id)
    const roleIds = roleRes.data || []
    
    dialog.value = {
      visible: true,
      mode: 'edit',
      form: {
        id: user.id,
        username: user.username || '',
        password: '',
        realName: user.realName || '',
        phone: user.phone || '',
        email: user.email || '',
        gender: user.gender || '2',
        deptId: user.deptId || null,
        postId: user.postId || null,
        roleIds: roleIds,
        status: user.status !== undefined ? user.status : 1,
        remark: user.remark || ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
        password: [
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      }
    }
  } catch (e) {
    ElMessage.error('加载用户信息失败')
    console.error(e)
  }
}

async function save() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    const f = dialog.value.form
    try {
      const data = {
        username: f.username,
        realName: f.realName,
        phone: f.phone,
        email: f.email,
        gender: f.gender,
        deptId: f.deptId,
        postId: f.postId,
        status: f.status,
        remark: f.remark
      }
      
      if (dialog.value.mode === 'add') {
        if (!f.password) {
          ElMessage.warning('请输入用户密码')
          return
        }
        data.password = f.password
        await userAPI.create(data)
        // 创建成功后分配角色
        const userRes = await userAPI.list({ username: f.username })
        const newUser = userRes.data?.[0]
        if (newUser && f.roleIds && f.roleIds.length > 0) {
          await userAPI.assignRoles(newUser.id, f.roleIds)
        }
      } else {
        if (f.password && f.password.trim()) {
          data.password = f.password
        }
        await userAPI.update(f.id, data)
        // 更新角色
        if (f.roleIds) {
          await userAPI.assignRoles(f.id, f.roleIds)
        }
      }
      
      dialog.value.visible = false
      ElMessage.success(dialog.value.mode === 'add' ? '新增成功' : '编辑成功')
      await load()
    } catch (e) {
      ElMessage.error(e.response?.data?.msg || e.message || '操作失败')
    }
  })
}

async function onDelete(row) {
  if (isAdmin(row)) {
    ElMessage.warning('系统管理员不允许删除')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除用户「${row.realName || row.username}」吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await userAPI.delete(row.id)
    ElMessage.success('删除成功')
    await load()
  } catch (e) {
    if (e !== 'cancel' && e.response?.data?.msg) {
      ElMessage.error(e.response.data.msg)
    }
  }
}

onMounted(async () => {
  await loadDeptTree()
  await loadPosts()
  await loadRoles()
  await load()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

.search-form {
  margin-bottom: 0;
}

:deep(.el-tree-node__content) {
  height: 32px;
}

:deep(.el-card__body) {
  padding: 16px;
}
</style>
