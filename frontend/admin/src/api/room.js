import request from '@/utils/request'

export function getRoomTypePage(params) {
  return request.get('/roomType/page', { params })
}

export function getAvailableRoomTypes() {
  return request.get('/roomType/available')
}

export function searchRoomTypes(params) {
  return request.get('/roomType/search', { params })
}

export function getRoomType(id) {
  return request.get(`/roomType/${id}`)
}

export function addRoomType(data) {
  return request.post('/roomType', data)
}

export function updateRoomType(data) {
  return request.put('/roomType', data)
}

export function deleteRoomType(id) {
  return request.delete(`/roomType/${id}`)
}

export function getRoomPage(params) {
  return request.get('/room/page', { params })
}

export function getAllRooms() {
  return request.get('/room/list')
}

export function getRoomsByType(roomTypeId) {
  return request.get(`/room/type/${roomTypeId}`)
}

export function getRoom(id) {
  return request.get(`/room/${id}`)
}

export function addRoom(data) {
  return request.post('/room', data)
}

export function updateRoom(data) {
  return request.put('/room', data)
}

export function deleteRoom(id) {
  return request.delete(`/room/${id}`)
}

export function updateRoomStatus(id, status) {
  return request.put(`/room/${id}/status`, { status })
}

export function updateCleanStatus(id, cleanStatus) {
  return request.put(`/room/${id}/clean-status`, { cleanStatus })
}

export function getRoomStatistics() {
  return request.get('/room/statistics')
}
