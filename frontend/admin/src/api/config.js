import request from '@/utils/request'

export function getAllConfigs() {
  return request.get('/config/list')
}

export function getConfigMap() {
  return request.get('/config/map')
}

export function getConfig(key) {
  return request.get(`/config/${key}`)
}

export function updateConfig(data) {
  return request.put('/config', data)
}

export function batchUpdateConfig(data) {
  return request.put('/config/batch', data)
}
