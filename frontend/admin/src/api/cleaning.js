import request from '@/utils/request'

export function createTask(data) {
  return request.post('/cleaning', data)
}

export function getTaskPage(params) {
  return request.get('/cleaning/page', { params })
}

export function getMyTasks() {
  return request.get('/cleaning/my')
}

export function getPendingTasks() {
  return request.get('/cleaning/pending')
}

export function assignTask(id, data) {
  return request.put(`/cleaning/${id}/assign`, data)
}

export function startTask(id) {
  return request.put(`/cleaning/${id}/start`)
}

export function completeTask(id) {
  return request.put(`/cleaning/${id}/complete`)
}
