<template>
  <div class="page-container">
    <el-card class="app-card section" shadow="never" style="margin-bottom: 16px">
      <div class="toolbar">
        <el-button type="primary" @click="openAdd" :icon="Plus">新增岗位</el-button>
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
        <el-table-column prop="postCode" label="岗位编码" width="150" />
        <el-table-column prop="postName" label="岗位名称" min-width="200" />
        <el-table-column prop="postSort" label="岗位顺序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.mode==='add'?'新增岗位':'编辑岗位'" width="600px">
      <el-form :model="dialog.form" label-width="120px">
        <el-form-item label="岗位编码" required>
          <el-input v-model="dialog.form.postCode" placeholder="请输入岗位编码，如：ceo" clearable />
        </el-form-item>
        <el-form-item label="岗位名称" required>
          <el-input v-model="dialog.form.postName" placeholder="请输入岗位名称" clearable />
        </el-form-item>
        <el-form-item label="岗位顺序">
          <el-input-number v-model="dialog.form.postSort" :min="0" />
        </el-form-item>
        <el-form-item label="岗位状态">
          <el-radio-group v-model="dialog.form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { postAPI } from '../api/post'

const list = ref([])
const loading = ref(false)
const dialog = ref({ 
  visible: false, 
  mode: 'add', 
  form: { 
    id: null, 
    postCode: '', 
    postName: '',
    postSort: 0,
    status: '0',
    remark: ''
  } 
})

async function load() {
  loading.value = true
  try {
    const res = await postAPI.list()
    list.value = res.data || []
  } catch (e) {
    ElMessage.error('加载岗位列表失败')
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
      postCode: '', 
      postName: '',
      postSort: 0,
      status: '0',
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
      postCode: row.postCode || '', 
      postName: row.postName || '',
      postSort: row.postSort || 0,
      status: row.status || '0',
      remark: row.remark || ''
    } 
  }
}

async function save() {
  const f = dialog.value.form
  if (!f.postCode || !f.postCode.trim()) {
    ElMessage.warning('请输入岗位编码')
    return
  }
  if (!f.postName || !f.postName.trim()) {
    ElMessage.warning('请输入岗位名称')
    return
  }
  try {
    if (dialog.value.mode === 'add') {
      await postAPI.create(f)
    } else {
      await postAPI.update(f.id, f)
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
    await ElMessageBox.confirm(`确定要删除岗位「${row.postName}」吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await postAPI.delete(row.id)
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

