import request from '@/utils/request'

export function getPermissionTree() {
  return request({
    url: '/permission/tree',
    method: 'get'
  })
}

export function getPermissionsByRoleId(roleId) {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'get'
  })
}

