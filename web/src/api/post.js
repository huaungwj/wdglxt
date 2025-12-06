import request from './request'

export const postAPI = {
    list() {
        return request.get('/api/posts')
    },
    getById(id) {
        return request.get(`/api/posts/${id}`)
    },
    create(data) {
        return request.post('/api/posts', data)
    },
    update(id, data) {
        return request.put(`/api/posts/${id}`, data)
    },
    delete(id) {
        return request.delete(`/api/posts/${id}`)
    }
}

