import request from '@/utils/request'

export function createOrder(data) {
  return request.post('/order', data)
}

export function getMyOrders() {
  return request.get('/order/my')
}

export function getOrder(id) {
  return request.get(`/order/${id}`)
}

export function cancelOrder(id) {
  return request.put(`/order/${id}/cancel`)
}

