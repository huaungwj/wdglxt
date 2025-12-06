import request from './request'

export const menuAPI = {
    list() {
        return request.get('/api/menus')
    },
    tree() {
        return request.get('/api/menus/tree')
    },
    getById(id) {
        return request.get(`/api/menus/${id}`)
    },
    create(data) {
        return request.post('/api/menus', data)
    },
    update(id, data) {
        return request.put(`/api/menus/${id}`, data)
    },
    delete(id) {
        return request.delete(`/api/menus/${id}`)
    }
}

