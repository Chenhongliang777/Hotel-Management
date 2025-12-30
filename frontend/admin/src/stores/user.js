import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getCurrentEmployee } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value.role || '')
  const username = computed(() => userInfo.value.username || '')
  const realName = computed(() => userInfo.value.realName || '')

  async function doLogin(username, password) {
    const res = await login(username, password)
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
    return res
  }

  async function fetchUserInfo() {
    const res = await getCurrentEmployee()
    userInfo.value = res.data
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  function hasRole(roles) {
    if (!roles || roles.length === 0) return true
    return roles.includes(role.value)
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    role,
    username,
    realName,
    doLogin,
    fetchUserInfo,
    logout,
    hasRole
  }
})
