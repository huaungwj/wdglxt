import request from './request'

/**
 * 文档管理API
 */
export const docAPI = {
    // 搜索文档列表
    search(params) {
        return request.get('/api/docs', { params })
    },

    // 上传单个文件（支持进度回调）
    upload(formData, onUploadProgress) {
        return request.post('/api/docs/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: onUploadProgress
        })
    },

    // 批量上传文件
    batchUpload(formData) {
        return request.post('/api/docs/batch-upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: (progressEvent) => {
                // 上传进度回调
                const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
                return percentCompleted
            }
        })
    },

    // 下载文件
    download(id) {
        return request.get(`/api/docs/download/${id}`, {
            responseType: 'blob'
        })
    },

    // 预览文件
    preview(id) {
        return `/api/docs/preview/${id}`
    },

    // 获取文档详情
    getById(id) {
        return request.get(`/api/docs/${id}`)
    },

    // 删除文档
    delete(id) {
        return request.delete(`/api/docs/${id}`)
    },

    // 更新标签
    updateTags(id, tags) {
        return request.put(`/api/docs/${id}/tags`, { tags })
    },

    // 更新分类
    updateCategory(id, categoryId) {
        return request.put(`/api/docs/${id}/category`, { categoryId })
    },

    // 获取访问记录
    getVisitLogs(id) {
        return request.get(`/api/docs/${id}/visit-logs`)
    },

    // 获取日期文件夹树形结构
    getDateFolders() {
        return request.get('/api/docs/date-folders')
    }
}
