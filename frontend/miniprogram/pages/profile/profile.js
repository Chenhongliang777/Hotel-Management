const app = getApp()

Page({
  data: {
    isLogin: false,
    userInfo: {},
    avatarUrl: '',
    isEmployee: false // 是否是员工
  },

  onShow() {
    const userInfo = app.globalData.userInfo || {}
    const isEmployee = userInfo.role && userInfo.role !== 'guest'
    this.setData({
      isLogin: app.checkLogin(),
      userInfo: userInfo,
      avatarUrl: userInfo.avatar || 'https://dummyimage.com/150x150/667eea/ffffff.png&text=Avatar',
      isEmployee: isEmployee
    })
    // 如果已登录，刷新用户信息
    if (app.checkLogin()) {
      if (isEmployee) {
        this.loadEmployeeInfo()
      } else {
        this.loadUserInfo()
      }
    }
  },

  onAvatarError() {
    // 如果头像加载失败，使用网络占位图
    this.setData({
      avatarUrl: 'https://dummyimage.com/150x150/667eea/ffffff.png&text=Avatar'
    })
  },

  async loadUserInfo() {
    try {
      const res = await app.request({ url: '/guest/current' })
      const userData = res.data || {}
      app.login(app.globalData.token, userData)
      this.setData({ 
        userInfo: userData,
        avatarUrl: userData.avatar || 'https://dummyimage.com/150x150/667eea/ffffff.png&text=Avatar',
        isEmployee: false
      })
    } catch (err) {
      console.error(err)
    }
  },

  async loadEmployeeInfo() {
    try {
      const res = await app.request({ url: '/employee/current' })
      const userData = res.data || {}
      app.login(app.globalData.token, userData)
      this.setData({ 
        userInfo: userData,
        avatarUrl: userData.avatar || 'https://dummyimage.com/150x150/667eea/ffffff.png&text=Avatar',
        isEmployee: true
      })
    } catch (err) {
      console.error(err)
    }
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  goOrders() {
    wx.switchTab({ url: '/pages/orders/orders' })
  },

  goEditProfile() {
    wx.navigateTo({ url: '/pages/profile-edit/profile-edit' })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout()
          this.setData({ isLogin: false, userInfo: {} })
          wx.showToast({ title: '已退出登录', icon: 'success' })
        }
      }
    })
  },

  goContact() {
    wx.navigateTo({ url: '/pages/contact/contact' })
  },

  goAbout() {
    wx.navigateTo({ url: '/pages/about/about' })
  },

  goWorkbench() {
    wx.navigateTo({ url: '/pages/employee/workbench/workbench' })
  }
})
