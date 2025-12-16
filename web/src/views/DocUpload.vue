<template>
  <div class="upload-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>文件上传</span>
          <el-button type="primary" @click="goBack">返回列表</el-button>
        </div>
      </template>

      <!-- 分类选择 -->
      <div class="category-section">
        <el-form :model="uploadForm" label-width="100px">
          <el-form-item label="分类">
            <el-select
              v-model="uploadForm.categoryId"
              placeholder="请选择您要上传的文档分类"
              clearable
              style="width: 300px"
            >
              <el-option
                v-for="cat in categories"
                :key="cat.id"
                :label="cat.name"
                :value="cat.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="标签">
            <el-input
              v-model="uploadForm.tags"
              placeholder="请输入标签，多个标签用逗号分隔"
              style="width: 300px"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 文件上传区域 -->
      <div class="upload-area">
        <el-upload
          ref="uploadRef"
          class="upload-dragger"
          drag
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="fileList"
          multiple
          :limit="50"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处上传<br />
            或者，你可以单击此处选择一个文件
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持批量上传，最多50个文件
            </div>
          </template>
        </el-upload>
      </div>

      <!-- 上传文件列表 -->
      <div class="file-list-section" v-if="uploadFiles.length > 0">
        <div class="section-title">上传文件列表</div>
        <el-table 
          ref="uploadTableRef"
          :data="uploadFiles" 
          stripe 
          style="margin-top: 20px"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="文件信息" min-width="300">
            <template #default="{ row }">
              <div class="file-info">
                <el-icon class="file-icon">
                  <Document v-if="isDocument(row.file.name)" />
                  <Picture v-else-if="isImage(row.file.name)" />
                  <VideoPlay v-else-if="isVideo(row.file.name)" />
                  <Document v-else />
                </el-icon>
                <span class="file-name" :title="row.file.name">{{ row.file.name }}</span>
                <span class="file-size">({{ formatFileSize(row.file.size) }})</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="分类" width="150">
            <template #default="{ row }">
              <span v-if="getCategoryName(row.categoryId)">
                {{ getCategoryName(row.categoryId) }}
              </span>
              <span v-else style="color: #909399">未分类</span>
            </template>
          </el-table-column>
          <el-table-column label="上传进度" width="200">
            <template #default="{ row }">
              <el-progress
                :percentage="row.progress"
                :status="row.status === 'success' ? 'success' : row.status === 'error' ? 'exception' : ''"
                :stroke-width="8"
              />
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag
                :type="getStatusType(row.status)"
                size="small"
              >
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button
                link
                type="primary"
                size="small"
                @click="handleCategorySelect(row)"
                :disabled="row.status === 'uploading'"
              >
                分类
              </el-button>
              <el-button
                link
                type="primary"
                size="small"
                @click="handleReupload(row)"
                :disabled="row.status !== 'error'"
              >
                重新上传
              </el-button>
              <el-button
                link
                type="danger"
                size="small"
                @click="handleRemoveFile(row)"
                :disabled="row.status === 'uploading'"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 批量操作按钮 -->
        <div class="batch-actions">
          <el-button @click="handleSelectAll">全选</el-button>
          <el-button @click="handleCancelSelected">取消选择</el-button>
          <el-button type="primary" @click="handleBatchUpload" :loading="uploading">
            {{ uploading ? '上传中...' : '开始上传' }}
          </el-button>
          <el-button @click="handleClearAll">清空列表</el-button>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <div class="footer-actions">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :disabled="uploadFiles.length === 0">
          确认
        </el-button>
      </div>
    </el-card>

    <!-- 分类选择对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      title="选择分类"
      width="400px"
    >
      <el-tree-select
        v-model="selectedCategoryId"
        :data="categoryTree"
        :render-after-expand="false"
        placeholder="请选择分类"
        style="width: 100%"
        clearable
      />
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCategoryConfirm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Document, Picture, VideoPlay } from '@element-plus/icons-vue'
import { docAPI } from '../api/doc'
import { fetchCategoryTree } from '../api/category'

const router = useRouter()

// 上传表单
const uploadForm = ref({
  categoryId: null,
  tags: ''
})

// 文件列表（el-upload组件使用）
const fileList = ref([])

// 上传文件列表（带状态）
const uploadFiles = ref([])

// 分类列表（扁平化）
const categories = ref([])

// 分类树（用于TreeSelect）
const categoryTree = ref([])

// 上传中状态
const uploading = ref(false)

// 分类选择对话框
const categoryDialogVisible = ref(false)
const selectedCategoryId = ref(null)
const currentFileRow = ref(null)

// 上传组件引用
const uploadRef = ref(null)

// 表格引用
const uploadTableRef = ref(null)

// 选中的文件列表
const selectedFiles = ref([])

// 加载分类树
const loadCategories = async () => {
  try {
    const res = await fetchCategoryTree()
    const treeData = res.data || []
    
    // 转换为TreeSelect需要的格式（value, label, children）
    const convertToTreeSelectFormat = (nodes) => {
      return nodes.map(node => ({
        value: node.id,
        label: node.name,
        children: node.children && node.children.length > 0 
          ? convertToTreeSelectFormat(node.children) 
          : undefined
      }))
    }
    
    // 保存树形结构用于TreeSelect
    categoryTree.value = convertToTreeSelectFormat(treeData)
    
    // 扁平化分类树（用于查找分类名称）
    const flattenCategories = (nodes) => {
      let result = []
      nodes.forEach(node => {
        result.push({ id: node.id, name: node.name })
        if (node.children && node.children.length > 0) {
          result = result.concat(flattenCategories(node.children))
        }
      })
      return result
    }
    categories.value = flattenCategories(treeData)
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

// 文件变化处理
const handleFileChange = (file, fileListFromUpload) => {
  // 同步 el-upload 组件的文件列表
  fileList.value = fileListFromUpload
  
  // 检查是否已存在
  const fileUid = file.uid || file.raw?.uid
  const exists = uploadFiles.value.find(f => {
    const fUid = f.file?.uid || f.file?.raw?.uid
    return fUid === fileUid
  })
  
  if (!exists) {
    uploadFiles.value.push({
      file: file.raw || file,
      progress: 0,
      status: 'pending', // pending, uploading, success, error
      result: null,
      categoryId: uploadForm.value.categoryId || null // 初始化为表单中的分类
    })
  }
}

// 文件移除处理（从 el-upload 组件触发）
const handleFileRemove = (file) => {
  const fileUid = file.uid || file.raw?.uid
  // 从上传文件列表中移除
  uploadFiles.value = uploadFiles.value.filter(f => {
    const fUid = f.file?.uid || f.file?.raw?.uid
    return fUid !== fileUid
  })
}

// 移除文件（从表格的删除按钮触发）
const handleRemoveFile = (row) => {
  // 获取要删除的文件对象
  const fileToRemove = row.file
  
  if (!fileToRemove) {
    ElMessage.warning('无法获取文件信息')
    return
  }
  
  // 获取文件的唯一标识 - 优先使用 file.uid，如果没有则使用 file.raw.uid
  let fileUid = fileToRemove.uid
  if (!fileUid && fileToRemove.raw) {
    fileUid = fileToRemove.raw.uid
  }
  
  // 如果还是没有 uid，尝试从 row 本身获取
  if (!fileUid && row.uid) {
    fileUid = row.uid
  }
  
  if (!fileUid) {
    console.error('无法获取文件标识', { row, fileToRemove })
    ElMessage.warning('无法获取文件标识，删除失败')
    return
  }
  
  // 保存删除前的数量用于验证
  const beforeUploadFilesCount = uploadFiles.value.length
  const beforeFileListCount = fileList.value.length
  
  // 从上传文件列表中移除（使用严格匹配）
  uploadFiles.value = uploadFiles.value.filter(f => {
    // 获取当前文件的 uid
    const currentFile = f.file || f
    let currentUid = currentFile?.uid
    if (!currentUid && currentFile?.raw) {
      currentUid = currentFile.raw.uid
    }
    // 只有当 uid 存在且完全匹配时才删除
    // 如果 fileUid 或 currentUid 是 undefined，则保留文件（不删除）
    if (!fileUid || !currentUid) {
      return true // 保留文件
    }
    return currentUid !== fileUid
  })
  
  // 从 el-upload 的文件列表中移除
  fileList.value = fileList.value.filter(f => {
    let currentUid = f?.uid
    if (!currentUid && f?.raw) {
      currentUid = f.raw.uid
    }
    // 只有当 uid 存在且完全匹配时才删除
    // 如果 fileUid 或 currentUid 是 undefined，则保留文件（不删除）
    if (!fileUid || !currentUid) {
      return true // 保留文件
    }
    return currentUid !== fileUid
  })
  
  // 验证删除是否成功
  const deletedFromUploadFiles = beforeUploadFilesCount - uploadFiles.value.length
  const deletedFromFileList = beforeFileListCount - fileList.value.length
  
  if (deletedFromUploadFiles === 0) {
    console.warn('删除失败，未在 uploadFiles 中找到对应文件，uid:', fileUid)
  }
  
  if (deletedFromFileList === 0) {
    console.warn('删除失败，未在 fileList 中找到对应文件，uid:', fileUid)
  }
  
  // 如果 fileList 被意外清空，但 uploadFiles 还有文件，尝试恢复
  if (fileList.value.length === 0 && uploadFiles.value.length > 0) {
    console.warn('fileList 被清空，但 uploadFiles 还有文件，尝试恢复 fileList')
    // 从 uploadFiles 恢复 fileList，只取 file 对象
    fileList.value = uploadFiles.value
      .map(f => f.file)
      .filter(f => f != null)
  }
}

// 单个文件上传
const uploadSingleFile = async (fileRow) => {
  fileRow.status = 'uploading'
  fileRow.progress = 0

  try {
    const formData = new FormData()
    formData.append('file', fileRow.file)
    // 优先使用文件自己的分类，如果没有则使用表单中的分类
    const categoryId = fileRow.categoryId || uploadForm.value.categoryId
    if (categoryId) {
      formData.append('categoryId', categoryId)
    }
    if (uploadForm.value.tags) {
      formData.append('tags', uploadForm.value.tags)
    }

    // 使用axios上传，支持进度监控
    const response = await docAPI.upload(formData, (progressEvent) => {
      if (progressEvent.total) {
        fileRow.progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      }
    })

    // 后端返回格式：{ code: 0, msg: 'ok', data: {...} }
    fileRow.status = 'success'
    fileRow.progress = 100
    fileRow.result = response.data || response
    return response.data || response
  } catch (error) {
    fileRow.status = 'error'
    fileRow.progress = 0
    console.error('上传失败', error)
    throw error
  }
}

// 批量上传
const handleBatchUpload = async () => {
  // 如果选择了文件，只上传选中的文件；否则上传所有待上传的文件
  let filesToUpload = []
  if (selectedFiles.value.length > 0) {
    // 只上传选中的且状态为pending或error的文件
    filesToUpload = selectedFiles.value.filter(f => f.status === 'pending' || f.status === 'error')
  } else {
    // 上传所有待上传的文件
    filesToUpload = uploadFiles.value.filter(f => f.status === 'pending' || f.status === 'error')
  }
  
  if (filesToUpload.length === 0) {
    ElMessage.warning('没有待上传的文件')
    return
  }

  uploading.value = true

  try {
    // 逐个上传文件
    for (const fileRow of filesToUpload) {
      try {
        await uploadSingleFile(fileRow)
      } catch (error) {
        ElMessage.error(`文件 ${fileRow.file.name} 上传失败`)
      }
    }

    const successCount = uploadFiles.value.filter(f => f.status === 'success').length
    ElMessage.success(`上传完成！成功: ${successCount}/${uploadFiles.value.length}`)
    
    // 上传完成后清除选择
    handleCancelSelected()
  } catch (error) {
    ElMessage.error('批量上传失败')
  } finally {
    uploading.value = false
  }
}

// 重新上传
const handleReupload = async (row) => {
  row.status = 'pending'
  row.progress = 0
  try {
    await uploadSingleFile(row)
    ElMessage.success('重新上传成功')
  } catch (error) {
    ElMessage.error('重新上传失败')
  }
}

// 选择分类
const handleCategorySelect = (row) => {
  currentFileRow.value = row
  selectedCategoryId.value = row.categoryId || uploadForm.value.categoryId
  categoryDialogVisible.value = true
}

// 确认分类选择
const handleCategoryConfirm = () => {
  if (currentFileRow.value) {
    currentFileRow.value.categoryId = selectedCategoryId.value
  }
  categoryDialogVisible.value = false
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  if (!categoryId) return null
  const category = categories.value.find(cat => cat.id === categoryId)
  return category ? category.name : null
}

// 表格选择变化处理
const handleSelectionChange = (selection) => {
  selectedFiles.value = selection
}

// 全选
const handleSelectAll = () => {
  if (uploadTableRef.value) {
    // 只选择待上传和上传失败的文件
    uploadFiles.value.forEach(row => {
      if (row.status === 'pending' || row.status === 'error') {
        uploadTableRef.value.toggleRowSelection(row, true)
      }
    })
  }
}

// 取消选择
const handleCancelSelected = () => {
  if (uploadTableRef.value) {
    uploadTableRef.value.clearSelection()
  }
}

// 清空列表
const handleClearAll = () => {
  ElMessageBox.confirm('确定要清空所有文件吗？', '提示', {
    type: 'warning'
  }).then(() => {
    uploadFiles.value = []
    fileList.value = []
    uploadRef.value?.clearFiles()
  }).catch(() => {})
}

// 确认
const handleConfirm = () => {
  const successFiles = uploadFiles.value.filter(f => f.status === 'success')
  if (successFiles.length === 0) {
    ElMessage.warning('没有成功上传的文件')
    return
  }
  ElMessage.success(`已成功上传 ${successFiles.length} 个文件`)
  router.push('/docs')
}

// 返回
const goBack = () => {
  router.push('/docs')
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    'pending': 'info',
    'uploading': 'warning',
    'success': 'success',
    'error': 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'pending': '待上传',
    'uploading': '上传中',
    'success': '上传成功',
    'error': '上传失败'
  }
  return map[status] || '未知'
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 判断文件类型
const isDocument = (fileName) => {
  const ext = fileName.split('.').pop().toLowerCase()
  return ['doc', 'docx', 'pdf', 'txt', 'xls', 'xlsx', 'ppt', 'pptx'].includes(ext)
}

const isImage = (fileName) => {
  const ext = fileName.split('.').pop().toLowerCase()
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)
}

const isVideo = (fileName) => {
  const ext = fileName.split('.').pop().toLowerCase()
  return ['mp4', 'avi', 'mov', 'wmv', 'flv'].includes(ext)
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.upload-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.upload-area {
  margin-bottom: 30px;
}

.upload-dragger {
  width: 100%;
}

.file-list-section {
  margin-top: 30px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-icon {
  font-size: 24px;
  color: #409eff;
}

.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  color: #909399;
  font-size: 12px;
}

.batch-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.footer-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

