import request from '@/utils/request'

export function getItemPage(params) {
  return request.get('/inventory/page', { params })
}

export function getAllItems() {
  return request.get('/inventory/list')
}

export function getLowStockItems() {
  return request.get('/inventory/low-stock')
}

export function getItem(id) {
  return request.get(`/inventory/${id}`)
}

export function addItem(data) {
  return request.post('/inventory', data)
}

export function updateItem(data) {
  return request.put('/inventory', data)
}

export function deleteItem(id) {
  return request.delete(`/inventory/${id}`)
}

export function stockIn(data) {
  return request.post('/inventory/stock-in', data)
}

export function stockOut(data) {
  return request.post('/inventory/stock-out', data)
}

export function getRecordPage(params) {
  return request.get('/inventory/record/page', { params })
}
