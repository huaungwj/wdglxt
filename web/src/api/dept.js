import request from './request'

export const deptAPI = {
    list() {
        return request.get('/api/depts')
    },
    tree() {
        return request.get('/api/depts/tree')
    },
    getById(id) {
        return request.get(`/api/depts/${id}`)
    },
    create(data) {
        return request.post('/api/depts', data)
    },
    update(id, data) {
        return request.put(`/api/depts/${id}`, data)
    },
    delete(id) {
        return request.delete(`/api/depts/${id}`)
    }
}

