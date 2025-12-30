import request from '@/utils/request'

export function getDashboardStats() {
  return request.get('/statistics/dashboard')
}

export function getRevenueChart(params) {
  return request.get('/statistics/revenue-chart', { params })
}

export function getRoomStatusStats() {
  return request.get('/statistics/room-status')
}
