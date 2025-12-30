const app = getApp()

Page({
  data: {
    form: {
      username: '',
      password: '',
      confirmPassword: '',
      nickname: '',
      phone: '',
      email: ''
    },
    loading: false,
    showLogo: true
  },

  onLogoError() {
    this.setData({ showLogo: false })
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  async register() {
    const { form } = this.data
    
    // 验证必填项
    if (!form.username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' })
      return
    }
    if (!form.password) {
      wx.showToast({ title: '请输入密码', icon: 'none' })
      return
    }
    if (form.password.length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' })
      return
    }
    if (form.password !== form.confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' })
      return
    }
    if (!form.phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    // 简单的手机号验证
    if (!/^1[3-9]\d{9}$/.test(form.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }

    this.setData({ loading: true })
    try {
      const res = await app.request({
        url: '/auth/guest/register',
        method: 'POST',
        data: {
          username: form.username,
          password: form.password,
          nickname: form.nickname || form.username,
          phone: form.phone,
          email: form.email || ''
        }
      })
      wx.showToast({ title: '注册成功', icon: 'success' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (err) {
      console.error(err)
    } finally {
      this.setData({ loading: false })
    }
  },

  goLogin() {
    wx.navigateBack()
  }
})

