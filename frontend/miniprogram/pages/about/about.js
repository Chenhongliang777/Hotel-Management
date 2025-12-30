Page({
  data: {
    appInfo: {
      name: '悦居民宿',
      version: '1.0.0',
      description: '悦居民宿致力于为您提供温馨舒适的住宿体验，让每一次旅行都成为美好的回忆。',
      features: [
        '精选优质房源，环境优美',
        '24小时客服服务，随时为您解答',
        '安全便捷的在线预订系统',
        '完善的售后服务保障'
      ],
      company: {
        name: '悦居民宿管理有限公司',
        established: '2020年',
        location: '北京市朝阳区'
      }
    }
  },

  onLoad() {
    wx.setNavigationBarTitle({ title: '关于我们' })
  },

  // 查看用户协议
  viewAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '欢迎使用悦居民宿服务。在使用我们的服务前，请仔细阅读用户协议。',
      showCancel: false,
      confirmText: '我知道了'
    })
  },

  // 查看隐私政策
  viewPrivacy() {
    wx.showModal({
      title: '隐私政策',
      content: '我们非常重视您的隐私保护。我们会严格按照相关法律法规保护您的个人信息安全。',
      showCancel: false,
      confirmText: '我知道了'
    })
  }
})

