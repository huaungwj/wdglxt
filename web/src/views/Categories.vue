<template>
  <div class="page-container">
    <el-card class="app-card section" shadow="never" style="margin-bottom: 16px">
      <div class="toolbar">
        <el-button type="primary" @click="openAdd" :icon="Plus">新增分类</el-button>
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
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column label="分类名称" min-width="300" prop="name" show-overflow-tooltip />
      <el-table-column prop="fileCount" label="文件数量" width="150" align="center">
        <template #default="{ row }">
          <el-tag size="small" type="info" round>{{ row.fileCount || 0 }}</el-tag>
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

    <el-dialog v-model="dialog.visible" :title="dialog.mode==='add'?'新增分类':'编辑分类'" width="500px">
      <el-form :model="dialog.form" label-width="100px">
        <el-form-item label="分类名称" required>
          <el-input v-model="dialog.form.name" placeholder="请输入分类名称" clearable />
        </el-form-item>
        <el-form-item label="上级分类">
          <el-tree-select 
            v-model="dialog.form.parentId" 
            :data="tree" 
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly 
            clearable 
            placeholder="请选择上级分类（可为空）" 
            style="width: 100%" 
          />
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
import { Folder, Plus } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { fetchCategoryTree, createCategory, updateCategory, deleteCategory } from '../api/category'

const tree = ref([])
const dialog = ref({ visible:false, mode:'add', form:{ id:null, name:'', parentId:null } })

async function load(){
  try {
    const res = await fetchCategoryTree()
    console.log('原始响应:', res)
    // 后端返回格式是 { code: 0, msg: 'ok', data: [...] }
    // 拦截器已经返回了整个 data 对象，所以需要取 res.data
    const data = res.data
    console.log('提取的 data:', data)
    tree.value = Array.isArray(data) ? data : []
    console.log('最终的 tree.value:', tree.value)
    console.log('第一个节点:', tree.value[0])
    if (tree.value[0]) {
      console.log('第一个节点的 children:', tree.value[0].children)
    }
  } catch(e) {
    ElMessage.error('加载分类失败')
    console.error(e)
  }
}

function openAdd(){
  dialog.value = { visible:true, mode:'add', form:{ name:'', parentId:null } }
}
function openEdit(row){
  dialog.value = { visible:true, mode:'edit', form:{ id:row.id, name:row.name, parentId:row.parentId||null } }
}
async function save(){
  const f = dialog.value.form
  if (!f.name || !f.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    if (dialog.value.mode==='add') await createCategory({ name:f.name, parentId:f.parentId })
    else await updateCategory(f.id, { name:f.name, parentId:f.parentId })
    dialog.value.visible=false
    ElMessage.success(dialog.value.mode==='add' ? '新增成功' : '编辑成功')
    await load()
  } catch(e) {
    ElMessage.error(e.response?.data?.msg || e.message || '操作失败')
  }
}
async function onDelete(row){
  try{
    await ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？如有子分类或文件将无法删除。`,'删除确认',{type:'warning', confirmButtonText: '确定', cancelButtonText: '取消'})
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    await load()
  }catch(e){
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
