import request from '@/utils/request'

export function getAvailableRoomTypes() {
  return request.get('/roomType/available')
}

export function searchRoomTypes(params) {
  return request.get('/roomType/search', { params })
}

export function getRoomType(id) {
  return request.get(`/roomType/${id}`)
}

export function getRoomsByType(roomTypeId) {
  return request.get(`/room/type/${roomTypeId}`)
}

export function getDepositRate() {
  return request.get('/config/deposit_rate')
}

