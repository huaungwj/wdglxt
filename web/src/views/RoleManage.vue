<template>
  <div class="page-container">
    <el-card class="app-card section" shadow="never" style="margin-bottom: 16px">
      <div class="toolbar">
        <el-button type="primary" @click="openAdd" :icon="Plus">新增角色</el-button>
        <div class="spacer"></div>
      </div>
    </el-card>
    <el-card class="app-card section" shadow="never">
      <el-table 
        :data="list" 
        class="table-elegant" 
        style="width:100%" 
        stripe
        border
        size="large"
        v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="角色名称" min-width="150" />
        <el-table-column prop="code" label="权限字符" width="150" />
        <el-table-column prop="roleSort" label="角色顺序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dataScope" label="数据权限" width="150" align="center">
          <template #default="{ row }">
            <span>{{ getDataScopeText(row.dataScope) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="openPermission(row)">权限</el-button>
            <el-button type="danger" link size="small" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 角色编辑对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.mode==='add'?'新增角色':'编辑角色'" width="600px">
      <el-form :model="dialog.form" label-width="120px">
        <el-form-item label="角色名称" required>
          <el-input v-model="dialog.form.name" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="权限字符" required>
          <el-input v-model="dialog.form.code" placeholder="如：admin" clearable />
          <div style="color: #999; font-size: 12px; margin-top: 4px">
            用于 @PreAuthorize(`@ss.hasRole('admin')`) 等注解
          </div>
        </el-form-item>
        <el-form-item label="角色顺序">
          <el-input-number v-model="dialog.form.roleSort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dialog.form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数据权限范围">
          <el-select v-model="dialog.form.dataScope" placeholder="请选择数据权限范围" style="width: 100%">
            <el-option label="全部数据权限" value="1" />
            <el-option label="自定义数据权限" value="2" />
            <el-option label="本部门数据权限" value="3" />
            <el-option label="本部门及以下数据权限" value="4" />
            <el-option label="仅本人数据权限" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dialog.form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog v-model="permissionDialog.visible" title="分配权限" width="800px">
      <el-tabs v-model="permissionDialog.activeTab">
        <el-tab-pane label="菜单权限" name="menu">
          <el-tree
            ref="menuTreeRef"
            :data="menuTree"
            :props="{ children: 'children', label: 'name' }"
            show-checkbox
            node-key="id"
            :default-expand-all="true"
            :default-checked-keys="permissionDialog.checkedMenuIds"
            style="max-height: 400px; overflow-y: auto"
          />
        </el-tab-pane>
        <el-tab-pane label="数据权限" name="dept" v-if="permissionDialog.role?.dataScope === '2'">
          <el-tree
            ref="deptTreeRef"
            :data="deptTree"
            :props="{ children: 'children', label: 'name' }"
            show-checkbox
            node-key="id"
            :default-expand-all="true"
            :default-checked-keys="permissionDialog.checkedDeptIds"
            style="max-height: 400px; overflow-y: auto"
          />
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="permissionDialog.visible=false">取消</el-button>
        <el-button type="primary" @click="savePermission">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { roleAPI } from '../api/role'
import { menuAPI } from '../api/menu'
import { deptAPI } from '../api/dept'

const list = ref([])
const loading = ref(false)
const menuTree = ref([])
const deptTree = ref([])
const menuTreeRef = ref(null)
const deptTreeRef = ref(null)

const dialog = ref({ 
  visible: false, 
  mode: 'add', 
  form: { 
    id: null, 
    name: '', 
    code: '', 
    roleSort: 0,
    status: '0',
    dataScope: '1',
    remark: ''
  } 
})

const permissionDialog = ref({
  visible: false,
  activeTab: 'menu',
  role: null,
  checkedMenuIds: [],
  checkedDeptIds: []
})

function getDataScopeText(scope) {
  const map = {
    '1': '全部数据权限',
    '2': '自定义数据权限',
    '3': '本部门数据权限',
    '4': '本部门及以下数据权限',
    '5': '仅本人数据权限'
  }
  return map[scope] || '未知'
}

async function load() {
  loading.value = true
  try {
    const res = await roleAPI.list()
    list.value = res.data || []
  } catch (e) {
    ElMessage.error('加载角色列表失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadMenuTree() {
  try {
    const res = await menuAPI.tree()
    menuTree.value = res.data || []
  } catch (e) {
    console.error('加载菜单树失败', e)
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

function openAdd() {
  dialog.value = { 
    visible: true, 
    mode: 'add', 
    form: { 
      name: '', 
      code: '', 
      roleSort: 0,
      status: '0',
      dataScope: '1',
      remark: ''
    } 
  }
}

function openEdit(row) {
  dialog.value = { 
    visible: true, 
    mode: 'edit', 
    form: { 
      id: row.id, 
      name: row.name, 
      code: row.code || '', 
      roleSort: row.roleSort || 0,
      status: row.status || '0',
      dataScope: row.dataScope || '1',
      remark: row.remark || ''
    } 
  }
}

async function openPermission(row) {
  permissionDialog.value.role = row
  permissionDialog.value.activeTab = 'menu'
  permissionDialog.value.visible = true
  
  // 加载菜单权限
  try {
    const menuRes = await roleAPI.getMenus(row.id)
    permissionDialog.value.checkedMenuIds = menuRes.data || []
  } catch (e) {
    console.error('加载菜单权限失败', e)
    permissionDialog.value.checkedMenuIds = []
  }
  
  // 如果数据权限是自定义，加载部门权限
  if (row.dataScope === '2') {
    try {
      const deptRes = await roleAPI.getDepts(row.id)
      permissionDialog.value.checkedDeptIds = deptRes.data || []
    } catch (e) {
      console.error('加载部门权限失败', e)
      permissionDialog.value.checkedDeptIds = []
    }
  }
}

async function save() {
  const f = dialog.value.form
  if (!f.name || !f.name.trim()) {
    ElMessage.warning('请输入角色名称')
    return
  }
  if (!f.code || !f.code.trim()) {
    ElMessage.warning('请输入权限字符')
    return
  }
  try {
    if (dialog.value.mode === 'add') {
      await roleAPI.create(f)
    } else {
      await roleAPI.update(f.id, f)
    }
    dialog.value.visible = false
    ElMessage.success(dialog.value.mode === 'add' ? '新增成功' : '编辑成功')
    await load()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || e.message || '操作失败')
  }
}

async function savePermission() {
  const role = permissionDialog.value.role
  if (!role) return
  
  try {
    // 保存菜单权限
    const checkedMenuIds = menuTreeRef.value.getCheckedKeys()
    await roleAPI.assignMenus(role.id, checkedMenuIds)
    
    // 如果数据权限是自定义，保存部门权限
    if (role.dataScope === '2') {
      const checkedDeptIds = deptTreeRef.value?.getCheckedKeys() || []
      await roleAPI.assignDepts(role.id, checkedDeptIds)
    }
    
    permissionDialog.value.visible = false
    ElMessage.success('权限分配成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || e.message || '操作失败')
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除角色「${row.name}」吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await roleAPI.delete(row.id)
    ElMessage.success('删除成功')
    await load()
  } catch (e) {
    if (e !== 'cancel' && e.response?.data?.msg) {
      ElMessage.error(e.response.data.msg)
    }
  }
}

onMounted(async () => {
  await load()
  await loadMenuTree()
  await loadDeptTree()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}
</style>

