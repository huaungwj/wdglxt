<template>
  <div class="page-container">
    <el-card class="app-card section" shadow="never" style="margin-bottom: 16px">
      <div class="toolbar">
        <el-button type="primary" @click="openAdd" :icon="Plus">新增字典</el-button>
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
        <el-table-column prop="type" label="类型" width="150" />
        <el-table-column prop="code" label="编码" width="150" />
        <el-table-column prop="value" label="值" width="150" />
        <el-table-column prop="label" label="标签" min-width="200" />
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.mode==='add'?'新增字典':'编辑字典'" width="500px">
      <el-form :model="dialog.form" label-width="100px">
        <el-form-item label="类型" required>
          <el-input v-model="dialog.form.type" placeholder="请输入类型" clearable />
        </el-form-item>
        <el-form-item label="编码" required>
          <el-input v-model="dialog.form.code" placeholder="请输入编码" clearable />
        </el-form-item>
        <el-form-item label="值" required>
          <el-input v-model="dialog.form.value" placeholder="请输入值" clearable />
        </el-form-item>
        <el-form-item label="标签" required>
          <el-input v-model="dialog.form.label" placeholder="请输入标签" clearable />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialog.form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="dialog.form.description" type="textarea" placeholder="请输入描述" clearable />
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
import { dictAPI } from '../api/dict'

const list = ref([])
const loading = ref(false)
const dialog = ref({ 
  visible: false, 
  mode: 'add', 
  form: { 
    id: null, 
    type: '', 
    code: '', 
    value: '',
    label: '',
    sort: 0,
    description: ''
  } 
})

async function load() {
  loading.value = true
  try {
    const res = await dictAPI.list()
    list.value = res.data || []
  } catch (e) {
    ElMessage.error('加载字典列表失败')
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
      type: '', 
      code: '', 
      value: '',
      label: '',
      sort: 0,
      description: ''
    } 
  }
}

function openEdit(row) {
  dialog.value = { 
    visible: true, 
    mode: 'edit', 
    form: { 
      id: row.id, 
      type: row.type, 
      code: row.code, 
      value: row.value,
      label: row.label,
      sort: row.sort || 0,
      description: row.description || ''
    } 
  }
}

async function save() {
  const f = dialog.value.form
  if (!f.type || !f.type.trim()) {
    ElMessage.warning('请输入类型')
    return
  }
  if (!f.code || !f.code.trim()) {
    ElMessage.warning('请输入编码')
    return
  }
  if (!f.value || !f.value.trim()) {
    ElMessage.warning('请输入值')
    return
  }
  if (!f.label || !f.label.trim()) {
    ElMessage.warning('请输入标签')
    return
  }
  try {
    if (dialog.value.mode === 'add') {
      await dictAPI.create({ 
        type: f.type, 
        code: f.code, 
        value: f.value,
        label: f.label,
        sort: f.sort,
        description: f.description
      })
    } else {
      await dictAPI.update(f.id, { 
        type: f.type, 
        code: f.code, 
        value: f.value,
        label: f.label,
        sort: f.sort,
        description: f.description
      })
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
    await ElMessageBox.confirm(`确定要删除字典「${row.label}」吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await dictAPI.delete(row.id)
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
</style>

