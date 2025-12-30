import request from '@/utils/request'

export function login(username, password) {
  return request.post('/auth/employee/login', { username, password })
}

export function getCurrentEmployee() {
  return request.get('/employee/current')
}
