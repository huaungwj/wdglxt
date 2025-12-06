<template>
  <div class="page-container">
    <el-card class="app-card section" shadow="never" style="margin-bottom: 16px">
      <div class="toolbar">
        <el-button type="primary" @click="openAdd" :icon="Plus">新增部门</el-button>
        <div class="spacer"></div>
      </div>
    </el-card>
    <el-card class="app-card section" shadow="never">
      <el-table 
        :data="tree" 
        class="table-elegant" 
        style="width:100%" 
        row-key="id" 
        stripe
        border
        size="large"
        default-expand-all
        :indent="24"
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
        v-loading="loading">
        <el-table-column label="部门名称" min-width="200" prop="name" show-overflow-tooltip />
        <el-table-column prop="leader" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="sort" label="显示顺序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.mode==='add'?'新增部门':'编辑部门'" width="600px">
      <el-form :model="dialog.form" label-width="120px">
        <el-form-item label="上级部门">
          <el-tree-select 
            v-model="dialog.form.parentId" 
            :data="tree" 
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly 
            clearable 
            placeholder="请选择上级部门（可为空）" 
            style="width: 100%" 
          />
        </el-form-item>
        <el-form-item label="部门名称" required>
          <el-input v-model="dialog.form.name" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="显示顺序">
          <el-input-number v-model="dialog.form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="dialog.form.leader" placeholder="请输入负责人" clearable />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="dialog.form.phone" placeholder="请输入联系电话" clearable />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="dialog.form.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="部门状态">
          <el-radio-group v-model="dialog.form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { deptAPI } from '../api/dept'

const tree = ref([])
const loading = ref(false)
const dialog = ref({ 
  visible: false, 
  mode: 'add', 
  form: { 
    id: null, 
    name: '', 
    parentId: null,
    sort: 0,
    leader: '',
    phone: '',
    email: '',
    status: '0'
  } 
})

async function load() {
  loading.value = true
  try {
    const res = await deptAPI.tree()
    tree.value = res.data || []
  } catch (e) {
    ElMessage.error('加载部门列表失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

function openAdd() {
  dialog.value = { 
    visible: true, 
    mode: 'add', 
    form: { 
      name: '', 
      parentId: null,
      sort: 0,
      leader: '',
      phone: '',
      email: '',
      status: '0'
    } 
  }
}

function openEdit(row) {
  dialog.value = { 
    visible: true, 
    mode: 'edit', 
    form: { 
      id: row.id, 
      name: row.name || '', 
      parentId: row.parentId || null,
      sort: row.sort || 0,
      leader: row.leader || '',
      phone: row.phone || '',
      email: row.email || '',
      status: row.status || '0'
    } 
  }
}

async function save() {
  const f = dialog.value.form
  if (!f.name || !f.name.trim()) {
    ElMessage.warning('请输入部门名称')
    return
  }
  try {
    if (dialog.value.mode === 'add') {
      await deptAPI.create(f)
    } else {
      await deptAPI.update(f.id, f)
    }
    dialog.value.visible = false
    ElMessage.success(dialog.value.mode === 'add' ? '新增成功' : '编辑成功')
    await load()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || e.message || '操作失败')
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除部门「${row.name}」吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await deptAPI.delete(row.id)
    ElMessage.success('删除成功')
    await load()
  } catch (e) {
    if (e !== 'cancel' && e.response?.data?.msg) {
      ElMessage.error(e.response.data.msg)
    }
  }
}

onMounted(load)
</script>

<style scoped>
.page-container {
  width: 100%;
}
:deep(.el-table__indent) {
  padding-left: 24px !important;
}
</style>

