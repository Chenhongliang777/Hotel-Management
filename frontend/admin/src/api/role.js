import request from '@/utils/request'

export function getRolePage(params) {
  return request({
    url: '/role/page',
    method: 'get',
    params
  })
}

export function getAllRoles() {
  return request({
    url: '/role/list',
    method: 'get'
  })
}

export function getRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

export function getRolePermissions(id) {
  return request({
    url: `/role/${id}/permissions`,
    method: 'get'
  })
}

export function assignPermissions(id, permissionIds) {
  return request({
    url: `/role/${id}/permissions`,
    method: 'post',
    data: { permissionIds }
  })
}

