import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { guestLogin, getCurrentGuest } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value.username || '')
  const realName = computed(() => userInfo.value.realName || '')
  const phone = computed(() => userInfo.value.phone || '')

  async function doLogin(username, password) {
    const res = await guestLogin(username, password)
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
    return res
  }

  async function fetchUserInfo() {
    const res = await getCurrentGuest()
    userInfo.value = res.data
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    realName,
    phone,
    doLogin,
    fetchUserInfo,
    logout
  }
})

