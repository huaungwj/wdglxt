<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="toolbar">
        <div class="search-bar">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入文件名搜索"
            clearable
            style="width: 300px"
          />
          <el-select
            v-model="searchForm.categoryId"
            placeholder="选择分类"
            clearable
            style="width: 200px; margin-left: 12px"
          >
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
          <el-button type="primary" @click="loadDocs" style="margin-left: 12px">
            搜索
          </el-button>
        </div>
        
        <el-button type="primary" @click="uploadDialogVisible = true">
          上传文档
        </el-button>
      </div>

      <!-- 文档列表表格 -->
      <el-table :data="docs" stripe style="margin-top: 20px" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="fileNo" label="文件编号" width="150" />
        <el-table-column prop="name" label="文件名" min-width="200" />
        <el-table-column prop="creatorId" label="创建人" width="120">
          <template #default="{ row }">
            <span>{{ getUserName(row.creatorId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '已删除' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryId" label="分类" width="120">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="visitCount" label="访问次数" width="100" />
        <el-table-column prop="tags" label="标签" width="120" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handlePreview(row)">
              预览
            </el-button>
            <el-button link type="primary" @click="handleDownload(row)">
              下载
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传文档"
      width="500px"
    >
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="选择文件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="分类">
          <el-select v-model="uploadForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="标签">
          <el-input v-model="uploadForm.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUpload">
          确认上传
        </el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="文档预览"
      width="80%"
      :fullscreen="previewFullscreen"
    >
      <div class="preview-container">
        <img
          v-if="isImage(currentPreviewDoc)"
          :src="getPreviewUrl(currentPreviewDoc)"
          class="preview-image"
        />
        <iframe
          v-else-if="isPdf(currentPreviewDoc)"
          :src="getPreviewUrl(currentPreviewDoc)"
          class="preview-iframe"
        />
        <div v-else class="preview-unsupported">
          <el-result icon="warning" title="不支持预览此文件类型">
            <template #extra>
              <el-button type="primary" @click="handleDownload(currentPreviewDoc)">
                下载文件
              </el-button>
            </template>
          </el-result>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="previewFullscreen = !previewFullscreen">
          {{ previewFullscreen ? '退出全屏' : '全屏' }}
        </el-button>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, genFileId } from 'element-plus'
import { docAPI } from '../api/doc'
import { categoryAPI } from '../api/category'
import { userAPI } from '../api/user'

const docs = ref([])
const categories = ref([])
const users = ref([])
const loading = ref(false)
const uploading = ref(false)
const uploadDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const previewFullscreen = ref(false)
const currentPreviewDoc = ref(null)
const uploadRef = ref(null)

const searchForm = reactive({
  name: '',
  categoryId: ''
})

const uploadForm = reactive({
  categoryId: '',
  tags: ''
})

// 加载文档列表
const loadDocs = async () => {
  loading.value = true
  try {
    const res = await docAPI.list()
    docs.value = res || []
  } catch (error) {
    ElMessage.error('加载文档列表失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await categoryAPI.list()
    categories.value = res || []
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const res = await userAPI.list()
    users.value = res || []
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

// 获取用户名
const getUserName = (userId) => {
  const user = users.value.find(u => u.id === userId)
  return user ? user.realName || user.username : '未知'
}

// 获取分类名
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '-'
}

// 上传文件
const handleUpload = async () => {
  const uploadFiles = uploadRef.value.uploadFiles
  if (!uploadFiles || uploadFiles.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }
  
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', uploadFiles[0].raw)
    formData.append('categoryId', uploadForm.categoryId || '')
    formData.append('tags', uploadForm.tags || '')
    
    await docAPI.upload(formData)
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    uploadRef.value.clearFiles()
    uploadForm.categoryId = ''
    uploadForm.tags = ''
    loadDocs()
  } catch (error) {
    ElMessage.error(error.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

// 处理文件超出限制
const handleExceed = (files) => {
  uploadRef.value.clearFiles()
  const file = files[0]
  file.uid = genFileId()
  uploadRef.value.handleStart(file)
}

// 预览文件
const handlePreview = (row) => {
  currentPreviewDoc.value = row
  previewDialogVisible.value = true
}

// 判断是否为图片
const isImage = (doc) => {
  if (!doc || !doc.type) return false
  return doc.type.startsWith('image/')
}

// 判断是否为PDF
const isPdf = (doc) => {
  if (!doc || !doc.type) return false
  return doc.type === 'application/pdf'
}

// 获取预览URL
const getPreviewUrl = (doc) => {
  if (!doc) return ''
  return `http://localhost:8080${docAPI.preview(doc.id)}`
}

// 下载文件
const handleDownload = async (row) => {
  try {
    const res = await docAPI.download(row.id)
    const url = window.URL.createObjectURL(new Blob([res]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', row.originalName || row.name)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

// 删除文档
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该文档吗？', '提示', {
      type: 'warning'
    })
    
    await docAPI.delete(row.id)
    ElMessage.success('删除成功')
    loadDocs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadDocs()
  loadCategories()
  loadUsers()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  display: flex;
  align-items: center;
}

.preview-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  max-width: 100%;
  max-height: 600px;
}

.preview-iframe {
  width: 100%;
  height: 600px;
  border: none;
}

.preview-unsupported {
  text-align: center;
  padding: 60px 0;
}
</style>
