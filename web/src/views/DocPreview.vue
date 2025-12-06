<template>
  <div class="preview-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ docFile && docFile.name || '文档预览' }}</span>
          <div>
            <el-button @click="handleDownload">下载</el-button>
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>

      <!-- 文档信息 -->
      <div class="doc-info" v-if="docFile">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="文件编号">{{ docFile.fileNo }}</el-descriptions-item>
          <el-descriptions-item label="文件名">{{ docFile.originalName }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ formatFileSize(docFile.size) }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ docFile.creatorName }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(docFile.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="访问次数">{{ docFile.visitCount }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ docFile.categoryName || '未分类' }}</el-descriptions-item>
          <el-descriptions-item label="标签">{{ docFile.tags || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 预览区域 -->
      <div class="preview-content" v-loading="loading">
        <!-- 图片预览 -->
        <div v-if="isImage" class="image-preview">
          <img :src="imagePreviewUrl" :alt="docFile && docFile.name" style="max-width: 100%; max-height: 80vh" />
        </div>

        <!-- PDF预览 -->
        <div v-else-if="isPdf" class="pdf-preview">
          <iframe :src="pdfPreviewUrl" style="width: 100%; height: 80vh; border: none"></iframe>
        </div>

        <!-- 文本文件预览 -->
        <div v-else-if="isText" class="text-preview">
          <pre>{{ textContent }}</pre>
        </div>

        <!-- Office文档（直接下载） -->
        <div v-else-if="isOffice" class="office-preview">
          <el-result
            icon="document"
            title="Office文档"
            sub-title="Office文档（Word、Excel、PowerPoint）需要下载后使用相应的软件打开查看"
          >
            <template #extra>
              <el-button type="primary" @click="handleDownload">下载文件</el-button>
              <el-button @click="goBack">返回列表</el-button>
            </template>
          </el-result>
        </div>

        <!-- 视频预览 -->
        <div v-else-if="isVideo" class="video-preview">
          <video :src="videoPreviewUrl" controls style="width: 100%; max-height: 80vh"></video>
        </div>

        <!-- 音频预览 -->
        <div v-else-if="isAudio" class="audio-preview">
          <audio :src="audioPreviewUrl" controls style="width: 100%"></audio>
        </div>

        <!-- 不支持预览的文件类型 -->
        <div v-else class="unsupported-preview">
          <el-result
            icon="warning"
            title="不支持在线预览"
            sub-title="该文件类型不支持在线预览，请下载后查看"
          >
            <template #extra>
              <el-button type="primary" @click="handleDownload">下载文件</el-button>
            </template>
          </el-result>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { docAPI } from '../api/doc'

const route = useRoute()
const router = useRouter()

const docFile = ref(null)
const loading = ref(false)
const textContent = ref('')
const previewUrl = ref('')
const imagePreviewUrl = ref('')
const pdfPreviewUrl = ref('')
const videoPreviewUrl = ref('')
const audioPreviewUrl = ref('')

// 判断文件类型
const fileType = computed(() => {
  if (!docFile.value) return ''
  const fileName = docFile.value.originalName || docFile.value.name || ''
  return fileName.split('.').pop().toLowerCase()
})

const isImage = computed(() => {
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg'].includes(fileType.value)
})

const isPdf = computed(() => {
  return fileType.value === 'pdf'
})

const isText = computed(() => {
  return ['txt', 'md', 'json', 'xml', 'html', 'css', 'js', 'java', 'py', 'sql'].includes(fileType.value)
})

const isOffice = computed(() => {
  return ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'].includes(fileType.value)
})

const isVideo = computed(() => {
  return ['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm'].includes(fileType.value)
})

const isAudio = computed(() => {
  return ['mp3', 'wav', 'ogg', 'm4a'].includes(fileType.value)
})

// 加载文档详情
const loadDocFile = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('文档ID不存在')
    goBack()
    return
  }

  loading.value = true
  try {
    const res = await docAPI.getById(id)
    docFile.value = res.data

    // 使用后端返回的完整预览URL，如果没有则使用旧方式
    if (res.data.previewUrl) {
      previewUrl.value = res.data.previewUrl
    } else {
      // 兼容旧方式
      previewUrl.value = docAPI.preview(id)
    }

    // 对于需要认证的资源（图片、PDF、视频、音频），使用fetch获取blob URL
    if (isImage.value) {
      await loadImagePreview()
    } else if (isPdf.value) {
      await loadPdfPreview()
    } else if (isVideo.value) {
      await loadVideoPreview()
    } else if (isAudio.value) {
      await loadAudioPreview()
    } else if (isText.value) {
      // 如果是文本文件，加载内容
      await loadTextContent()
    }
    // Office文档不需要特殊处理，显示下载提示界面即可
  } catch (error) {
    ElMessage.error('加载文档失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载图片预览（通过fetch添加Authorization header）
const loadImagePreview = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(previewUrl.value, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const blob = await response.blob()
    imagePreviewUrl.value = window.URL.createObjectURL(blob)
  } catch (error) {
    console.error('加载图片预览失败', error)
    imagePreviewUrl.value = previewUrl.value
  }
}

// 加载PDF预览（通过fetch添加Authorization header）
const loadPdfPreview = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(previewUrl.value, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const blob = await response.blob()
    pdfPreviewUrl.value = window.URL.createObjectURL(blob)
  } catch (error) {
    console.error('加载PDF预览失败', error)
    pdfPreviewUrl.value = previewUrl.value
  }
}

// 加载视频预览（通过fetch添加Authorization header）
const loadVideoPreview = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(previewUrl.value, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const blob = await response.blob()
    videoPreviewUrl.value = window.URL.createObjectURL(blob)
  } catch (error) {
    console.error('加载视频预览失败', error)
    videoPreviewUrl.value = previewUrl.value
  }
}

// 加载音频预览（通过fetch添加Authorization header）
const loadAudioPreview = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(previewUrl.value, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const blob = await response.blob()
    audioPreviewUrl.value = window.URL.createObjectURL(blob)
  } catch (error) {
    console.error('加载音频预览失败', error)
    audioPreviewUrl.value = previewUrl.value
  }
}

// 加载文本内容
const loadTextContent = async () => {
  try {
    // 使用fetch加载文本内容，需要添加Authorization header
    const token = localStorage.getItem('token')
    const headers = {}
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    const response = await fetch(previewUrl.value, { headers })
    textContent.value = await response.text()
  } catch (error) {
    textContent.value = '无法加载文件内容'
    console.error(error)
  }
}

// 下载文件
const handleDownload = async () => {
  if (!docFile.value) return
  try {
    // 如果后端返回了完整的下载URL，直接使用
    if (docFile.value.downloadUrl) {
      const token = localStorage.getItem('token')
      const response = await fetch(docFile.value.downloadUrl, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = docFile.value.originalName || docFile.value.name
      a.click()
      window.URL.revokeObjectURL(url)
      ElMessage.success('下载成功')
    } else {
      // 兼容旧方式：通过API下载
      const blob = await docAPI.download(docFile.value.id)
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = docFile.value.originalName || docFile.value.name
      a.click()
      window.URL.revokeObjectURL(url)
      ElMessage.success('下载成功')
    }
  } catch (error) {
    ElMessage.error('下载失败')
    console.error(error)
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadDocFile()
})
</script>

<style scoped>
.preview-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.doc-info {
  margin-bottom: 20px;
}

.preview-content {
  min-height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-preview {
  text-align: center;
}

.pdf-preview {
  width: 100%;
}

.text-preview {
  width: 100%;
  max-height: 80vh;
  overflow: auto;
  background: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
}

.text-preview pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
}

.office-preview,
.video-preview,
.audio-preview {
  width: 100%;
  text-align: center;
}

.unsupported-preview {
  width: 100%;
}
</style>

