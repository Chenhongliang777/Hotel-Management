const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    loading: false,
    showLogo: true,
    loginType: 'guest' // 'guest' 或 'employee'
  },

  onLogoError() {
    // 如果logo加载失败，隐藏图片
    this.setData({ showLogo: false })
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  switchLoginType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ loginType: type })
  },

  async login() {
    const { username, password, loginType } = this.data
    if (!username) { wx.showToast({ title: '请输入用户名', icon: 'none' }); return }
    if (!password) { wx.showToast({ title: '请输入密码', icon: 'none' }); return }

    this.setData({ loading: true })
    try {
      const url = loginType === 'employee' ? '/auth/employee/login' : '/auth/guest/login'
      const res = await app.request({
        url: url,
        method: 'POST',
        data: { username, password }
      })
      app.login(res.data.token, res.data.userInfo)
      wx.showToast({ title: '登录成功', icon: 'success' })
      
      // 根据登录类型跳转到不同页面
      setTimeout(() => {
        if (loginType === 'employee') {
          // 员工登录后跳转到工作台
          wx.redirectTo({ url: '/pages/employee/workbench/workbench' })
        } else {
          // 客户登录后返回上一页
          wx.navigateBack()
        }
      }, 1000)
    } catch (err) {
      console.error(err)
    } finally {
      this.setData({ loading: false })
    }
  },

  goRegister() {
    // 只有客户可以注册
    if (this.data.loginType === 'employee') {
      wx.showToast({ title: '员工账号请联系管理员', icon: 'none' })
      return
    }
    wx.navigateTo({ url: '/pages/register/register' })
  }
})
