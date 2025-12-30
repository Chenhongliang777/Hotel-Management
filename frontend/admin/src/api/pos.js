import request from '@/utils/request'

export function createPosRecord(data) {
  return request.post('/pos', data)
}

export function getPosPage(params) {
  return request.get('/pos/page', { params })
}

export function getOrderPosRecords(orderId) {
  return request.get(`/pos/order/${orderId}`)
}
