import request from '@/utils/request'

export function getEmployeePage(params) {
  return request.get('/employee/page', { params })
}

export function getEmployee(id) {
  return request.get(`/employee/${id}`)
}

export function addEmployee(data) {
  return request.post('/employee', data)
}

export function updateEmployee(data) {
  return request.put('/employee', data)
}

export function deleteEmployee(id) {
  return request.delete(`/employee/${id}`)
}

export function resetPassword(id) {
  return request.put(`/employee/${id}/reset-password`)
}

export function updatePassword(data) {
  return request.put('/employee/password', data)
}
