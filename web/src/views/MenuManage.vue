<template>
  <div class="page-container">
    <el-card class="app-card section" shadow="never" style="margin-bottom: 16px">
      <div class="toolbar">
        <el-button type="primary" @click="openAdd" :icon="Plus">新增菜单</el-button>
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
        <el-table-column label="菜单名称" min-width="200" prop="name" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 0 ? 'info' : row.type === 1 ? 'success' : 'warning'">
              {{ row.type === 0 ? '目录' : row.type === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" width="200" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="permCode" label="权限字符" width="150" />
        <el-table-column prop="sort" label="排序" width="100" align="center" />
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

    <el-dialog v-model="dialog.visible" :title="dialog.mode==='add'?'新增菜单':'编辑菜单'" width="700px">
      <el-form :model="dialog.form" label-width="120px">
        <el-form-item label="菜单类型" required>
          <el-radio-group v-model="dialog.form.type" @change="onTypeChange">
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="上级菜单">
          <el-tree-select 
            v-model="dialog.form.parentId" 
            :data="tree" 
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly 
            clearable 
            placeholder="请选择上级菜单（可为空）" 
            style="width: 100%" 
          />
        </el-form-item>
        <el-form-item label="菜单名称" required>
          <el-input v-model="dialog.form.name" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        
        <!-- 目录和菜单的字段 -->
        <template v-if="dialog.form.type === 0 || dialog.form.type === 1">
          <el-form-item label="菜单图标">
            <el-input v-model="dialog.form.icon" placeholder="请输入图标名称，如：Setting" clearable />
          </el-form-item>
          <el-form-item label="显示排序">
            <el-input-number v-model="dialog.form.sort" :min="0" />
          </el-form-item>
          <el-form-item label="是否外链">
            <el-radio-group v-model="dialog.form.isFrame">
              <el-radio :label="0">是</el-radio>
              <el-radio :label="1">否</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="路由地址" v-if="dialog.form.type === 1">
            <el-input v-model="dialog.form.path" placeholder="请输入路由地址，如：/users" clearable />
          </el-form-item>
          <el-form-item label="组件路径" v-if="dialog.form.type === 1">
            <el-input v-model="dialog.form.component" placeholder="请输入组件路径，如：views/UserManage" clearable />
          </el-form-item>
          <el-form-item label="显示状态">
            <el-radio-group v-model="dialog.form.visible">
              <el-radio label="0">显示</el-radio>
              <el-radio label="1">隐藏</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="菜单状态">
            <el-radio-group v-model="dialog.form.status">
              <el-radio label="0">正常</el-radio>
              <el-radio label="1">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
        
        <!-- 按钮的字段 -->
        <template v-if="dialog.form.type === 2">
          <el-form-item label="显示顺序">
            <el-input-number v-model="dialog.form.sort" :min="0" />
          </el-form-item>
          <el-form-item label="权限字符" required>
            <el-input v-model="dialog.form.permCode" placeholder="如：file:upload" clearable />
            <div style="color: #999; font-size: 12px; margin-top: 4px">
              用于权限控制，如：file:upload, file:download
            </div>
          </el-form-item>
          <el-form-item label="菜单状态">
            <el-radio-group v-model="dialog.form.status">
              <el-radio label="0">正常</el-radio>
              <el-radio label="1">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
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
import { menuAPI } from '../api/menu'

const tree = ref([])
const loading = ref(false)
const dialog = ref({ 
  visible: false, 
  mode: 'add', 
  form: { 
    id: null, 
    name: '', 
    parentId: null,
    type: 1,
    path: '',
    component: '',
    icon: '',
    permCode: '',
    sort: 0,
    isFrame: 1,
    visible: '0',
    status: '0'
  } 
})

function onTypeChange() {
  // 切换类型时重置相关字段
  if (dialog.value.form.type === 2) {
    // 按钮类型不需要路径和组件
    dialog.value.form.path = ''
    dialog.value.form.component = ''
    dialog.value.form.icon = ''
    dialog.value.form.isFrame = 1
    dialog.value.form.visible = '0'
  }
}


async function load() {
  loading.value = true
  try {
    const res = await menuAPI.tree()
    tree.value = res.data || []
  } catch (e) {
    ElMessage.error('加载菜单列表失败')
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
      type: 1,
      path: '',
      component: '',
      icon: '',
      permCode: '',
      sort: 0,
      isFrame: 1,
      visible: '0',
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
      type: row.type !== undefined ? row.type : 1,
      path: row.path || '',
      component: row.component || '',
      icon: row.icon || '',
      permCode: row.permCode || '',
      sort: row.sort || 0,
      isFrame: row.isFrame !== undefined ? row.isFrame : 1,
      visible: row.visible || '0',
      status: row.status || '0'
    } 
  }
}

async function save() {
  const f = dialog.value.form
  if (!f.name || !f.name.trim()) {
    ElMessage.warning('请输入菜单名称')
    return
  }
  if (f.type === 2 && !f.permCode) {
    ElMessage.warning('按钮类型必须填写权限字符')
    return
  }
  try {
    const data = {
      name: f.name,
      parentId: f.parentId,
      type: f.type,
      path: f.path,
      component: f.component,
      icon: f.icon,
      permCode: f.permCode,
      sort: f.sort,
      isFrame: f.isFrame,
      visible: f.visible,
      status: f.status
    }
    if (dialog.value.mode === 'add') {
      await menuAPI.create(data)
    } else {
      await menuAPI.update(f.id, data)
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
    await ElMessageBox.confirm(`确定要删除菜单「${row.name}」吗？如有子菜单将无法删除。`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await menuAPI.delete(row.id)
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

