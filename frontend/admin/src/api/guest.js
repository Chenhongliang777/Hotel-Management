import request from '@/utils/request'

export function getGuestPage(params) {
  return request.get('/guest/page', { params })
}

export function getGuest(id) {
  return request.get(`/guest/${id}`)
}
