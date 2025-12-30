Page({
  data: {
    contactInfo: {
      phone: '400-888-8888',
      wechat: 'yueju_homestay',
      qq: '123456789',
      email: 'service@yueju.com',
      address: '北京市朝阳区某某街道123号',
      workTime: '周一至周日 9:00-21:00'
    }
  },

  onLoad() {
    wx.setNavigationBarTitle({ title: '联系客服' })
  },

  // 拨打电话
  makePhoneCall() {
    wx.makePhoneCall({
      phoneNumber: this.data.contactInfo.phone,
      fail: (err) => {
        wx.showToast({ title: '拨打电话失败', icon: 'none' })
      }
    })
  },

  // 复制微信号
  copyWechat() {
    wx.setClipboardData({
      data: this.data.contactInfo.wechat,
      success: () => {
        wx.showToast({ title: '微信号已复制', icon: 'success' })
      }
    })
  },

  // 复制QQ号
  copyQQ() {
    wx.setClipboardData({
      data: this.data.contactInfo.qq,
      success: () => {
        wx.showToast({ title: 'QQ号已复制', icon: 'success' })
      }
    })
  },

  // 复制邮箱
  copyEmail() {
    wx.setClipboardData({
      data: this.data.contactInfo.email,
      success: () => {
        wx.showToast({ title: '邮箱已复制', icon: 'success' })
      }
    })
  },

  // 复制地址
  copyAddress() {
    wx.setClipboardData({
      data: this.data.contactInfo.address,
      success: () => {
        wx.showToast({ title: '地址已复制', icon: 'success' })
      }
    })
  },

  // 查看地图
  viewMap() {
    wx.openLocation({
      latitude: 39.9042, // 示例坐标，实际应该使用真实地址的坐标
      longitude: 116.4074,
      name: '悦居民宿',
      address: this.data.contactInfo.address,
      fail: (err) => {
        wx.showToast({ title: '打开地图失败', icon: 'none' })
      }
    })
  }
})

