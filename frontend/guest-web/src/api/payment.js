import request from '@/utils/request'

// 创建支付记录
export function createPayment(data) {
  return request.post('/payment', data)
}

// 获取订单支付记录
export function getOrderPayments(orderId) {
  return request.get(`/payment/order/${orderId}`)
}

// 创建退款
export function createRefund(data) {
  return request.post('/payment/refund', data)
}

