App({
  globalData: {
    baseUrl: 'http://localhost:8080/api',
    token: '',
    userInfo: null,
    searchParams: null  // 用于传递搜索参数到tabBar页面
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    if (token) {
      this.globalData.token = token
      this.globalData.userInfo = userInfo
    }
  },

  request(options) {
    const that = this
    const requestId = Date.now() + Math.random().toString(36).substr(2, 9)
    const requestInfo = {
      id: requestId,
      url: options.url,
      method: options.method || 'GET',
      data: options.data,
      timestamp: new Date().toISOString()
    }
    
    // 记录请求日志
    console.log(`[HTTP请求] [${requestId}]`, requestInfo)
    
    return new Promise((resolve, reject) => {
      wx.request({
        url: that.globalData.baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data || {},
        header: {
          'Content-Type': 'application/json',
          'Authorization': that.globalData.token ? 'Bearer ' + that.globalData.token : ''
        },
        success(res) {
          if (res.data.code === 200) {
            console.log(`[HTTP请求] [${requestId}] 成功`, {
              url: options.url,
              method: options.method || 'GET',
              response: res.data
            })
            resolve(res.data)
          } else if (res.data.code === 401) {
            console.warn(`[HTTP请求] [${requestId}] 未授权`, {
              url: options.url,
              message: res.data.message
            })
            wx.removeStorageSync('token')
            wx.removeStorageSync('userInfo')
            that.globalData.token = ''
            that.globalData.userInfo = null
            wx.navigateTo({ url: '/pages/login/login' })
            reject(res.data)
          } else {
            console.error(`[HTTP请求] [${requestId}] 业务错误`, {
              url: options.url,
              code: res.data.code,
              message: res.data.message,
              data: res.data
            })
            wx.showToast({ title: res.data.message || '请求失败', icon: 'none' })
            reject(res.data)
          }
        },
        fail(err) {
          const errorInfo = {
            requestId: requestId,
            url: options.url,
            method: options.method || 'GET',
            error: err.errMsg || err.message || '网络错误',
            timestamp: new Date().toISOString()
          }
          console.error(`[HTTP请求] [${requestId}] 网络错误`, errorInfo)
          
          // 记录网络错误日志
          try {
            const networkErrorLogs = wx.getStorageSync('network_error_logs') || []
            networkErrorLogs.unshift(errorInfo)
            if (networkErrorLogs.length > 100) {
              networkErrorLogs.splice(100)
            }
            wx.setStorageSync('network_error_logs', networkErrorLogs)
          } catch (storageErr) {
            console.error('[HTTP请求] 保存网络错误日志失败', storageErr)
          }
          
          wx.showToast({ title: '网络错误', icon: 'none' })
          reject(err)
        }
      })
    })
  },

  login(token, userInfo) {
    this.globalData.token = token
    this.globalData.userInfo = userInfo
    wx.setStorageSync('token', token)
    wx.setStorageSync('userInfo', userInfo)
  },

  logout() {
    this.globalData.token = ''
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
  },

  checkLogin() {
    return !!this.globalData.token
  }
})
