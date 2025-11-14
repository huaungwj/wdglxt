import request from './request'

export const fetchCategoryTree = () => request.get('/api/categories/tree')
export const createCategory = (data) => request.post('/api/categories', data)
export const updateCategory = (id, data) => request.put(`/api/categories/${id}`, data)
export const deleteCategory = (id) => request.delete(`/api/categories/${id}`)
