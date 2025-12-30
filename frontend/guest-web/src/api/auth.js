import request from '@/utils/request'

export function guestLogin(username, password) {
  return request.post('/auth/guest/login', { username, password })
}

export function guestRegister(data) {
  return request.post('/auth/guest/register', data)
}

export function getCurrentGuest() {
  return request.get('/guest/current')
}

export function updateGuest(data) {
  return request.put('/guest', data)
}

