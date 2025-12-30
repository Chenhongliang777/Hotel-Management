import request from '@/utils/request'

export function createPayment(data) {
  return request.post('/payment', data)
}

export function getPaymentPage(params) {
  return request.get('/payment/page', { params })
}

export function getOrderPayments(orderId) {
  return request.get(`/payment/order/${orderId}`)
}

export function createRefund(data) {
  return request.post('/payment/refund', data)
}
