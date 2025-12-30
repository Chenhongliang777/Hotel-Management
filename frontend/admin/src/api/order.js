import request from '@/utils/request'

export function createOrder(data) {
  return request.post('/order', data)
}

export function getOrderPage(params) {
  return request.get('/order/page', { params })
}

export function getOrder(id) {
  return request.get(`/order/${id}`)
}

export function getOrderByNo(orderNo) {
  return request.get(`/order/no/${orderNo}`)
}

export function confirmOrder(id) {
  return request.put(`/order/${id}/confirm`)
}

export function checkIn(id, roomId) {
  return request.put(`/order/${id}/checkin`, { roomId })
}

export function checkOut(id) {
  return request.put(`/order/${id}/checkout`)
}

export function cancelOrder(id) {
  return request.put(`/order/${id}/cancel`)
}

export function updateOrder(data) {
  return request.put('/order', data)
}

export function getTodayCheckInOrders() {
  return request.get('/order/today-checkin')
}

export function getTodayCheckOutOrders() {
  return request.get('/order/today-checkout')
}

export function getOrderStatistics() {
  return request.get('/order/statistics')
}
