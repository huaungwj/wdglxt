import request from './request'

export const categoryAPI = {
    // 获取分类树
    list() {
        return request.get('/api/categories/tree')
    },

    // 创建分类
    create(data) {
        return request.post('/api/categories', data)
    },

    // 更新分类
    update(id, data) {
        return request.put(`/api/categories/${id}`, data)
    },

    // 删除分类
    delete(id) {
        return request.delete(`/api/categories/${id}`)
    }
}

// 保持向后兼容
export const fetchCategoryTree = () => request.get('/api/categories/tree')
export const createCategory = (data) => request.post('/api/categories', data)
export const updateCategory = (id, data) => request.put(`/api/categories/${id}`, data)
export const deleteCategory = (id) => request.delete(`/api/categories/${id}`)
