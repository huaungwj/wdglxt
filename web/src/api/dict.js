import request from './request'

export const dictAPI = {
    list() {
        return request.get('/api/dicts')
    },
    getById(id) {
        return request.get(`/api/dicts/${id}`)
    },
    getByType(type) {
        return request.get(`/api/dicts/type/${type}`)
    },
    create(data) {
        return request.post('/api/dicts', data)
    },
    update(id, data) {
        return request.put(`/api/dicts/${id}`, data)
    },
    delete(id) {
        return request.delete(`/api/dicts/${id}`)
    }
}

