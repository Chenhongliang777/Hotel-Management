const app = getApp()

Page({
  data: {
    roomTypeId: '',
    checkInDate: '',
    checkOutDate: '',
    roomType: {},
    nights: 1,
    totalPrice: 0,
    depositRate: 0.3,
    deposit: 0,
    grandTotal: 0,  // 应付总计 = 房费 + 保证金
    guestOptions: [1, 2, 3, 4, 5, 6],
    guestIndex: 1,
    form: { guestName: '', guestPhone: '', guestIdCard: '', remark: '' },
    submitting: false
  },

  onLoad(options) {
    this.setData({
      roomTypeId: options.roomTypeId,
      checkInDate: options.checkInDate,
      checkOutDate: options.checkOutDate,
      guestIndex: Math.max(0, (parseInt(options.guestCount) || 2) - 1)
    })
    this.loadRoomType()
    this.loadConfig()
  },

  async loadRoomType() {
    try {
      const res = await app.request({ url: `/roomType/${this.data.roomTypeId}` })
      const roomType = res.data
      const nights = this.calculateNights()
      const totalPrice = (roomType.basePrice * nights).toFixed(2)
      const deposit = (totalPrice * this.data.depositRate).toFixed(2)
      const grandTotal = (parseFloat(totalPrice) + parseFloat(deposit)).toFixed(2)
      this.setData({ roomType, nights, totalPrice, deposit, grandTotal })
    } catch (err) {
      console.error(err)
    }
  },

  async loadConfig() {
    try {
      const res = await app.request({ url: '/config/deposit_rate' })
      if (res.data) {
        const depositRate = parseFloat(res.data)
        const deposit = (this.data.totalPrice * depositRate).toFixed(2)
        const grandTotal = (parseFloat(this.data.totalPrice) + parseFloat(deposit)).toFixed(2)
        this.setData({ depositRate, deposit, grandTotal })
      }
    } catch (err) {}
  },

  calculateNights() {
    const { checkInDate, checkOutDate } = this.data
    const start = new Date(checkInDate)
    const end = new Date(checkOutDate)
    return Math.max(1, Math.ceil((end - start) / 86400000))
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  onGuestChange(e) {
    this.setData({ guestIndex: e.detail.value })
  },

  async submitOrder() {
    const { form, guestOptions, guestIndex, roomTypeId, checkInDate, checkOutDate } = this.data
    if (!form.guestName) { wx.showToast({ title: '请输入姓名', icon: 'none' }); return }
    if (!form.guestPhone) { wx.showToast({ title: '请输入手机号', icon: 'none' }); return }

    this.setData({ submitting: true })
    
    const orderData = {
      roomTypeId,
      checkInDate,
      checkOutDate,
      guestCount: guestOptions[guestIndex],
      guestName: form.guestName,
      guestPhone: form.guestPhone,
      guestIdCard: form.guestIdCard,
      remark: form.remark
    }
    
    // 记录订单提交日志
    console.log('[订单提交] 开始提交订单', {
      roomTypeId,
      checkInDate,
      checkOutDate,
      guestName: form.guestName,
      guestPhone: form.guestPhone,
      guestCount: guestOptions[guestIndex],
      timestamp: new Date().toISOString()
    })
    
    try {
      const res = await app.request({
        url: '/order',
        method: 'POST',
        data: orderData
      })
      
      // 记录成功日志
      console.log('[订单提交] 订单创建成功', {
        orderId: res.data.id,
        orderNo: res.data.orderNo,
        totalPrice: res.data.totalPrice,
        deposit: res.data.deposit,
        timestamp: new Date().toISOString()
      })
      
      wx.showToast({ title: '预订成功', icon: 'success' })
      setTimeout(() => {
        wx.redirectTo({ url: `/pages/order-detail/order-detail?id=${res.data.id}` })
      }, 1500)
    } catch (err) {
      // 详细记录错误信息
      const errorInfo = {
        error: err.message || '未知错误',
        code: err.code || err.statusCode || 'N/A',
        data: err.data || err,
        orderData: orderData,
        timestamp: new Date().toISOString()
      }
      console.error('[订单提交] 订单创建失败', errorInfo)
      
      // 显示用户友好的错误提示
      const errorMessage = err.message || (err.data && err.data.message) || '订单创建失败，请稍后重试'
      wx.showToast({ 
        title: errorMessage, 
        icon: 'none',
        duration: 3000
      })
      
      // 将错误信息保存到本地存储，方便后续排查
      try {
        const errorLogs = wx.getStorageSync('order_error_logs') || []
        errorLogs.unshift({
          ...errorInfo,
          id: Date.now()
        })
        // 只保留最近50条错误日志
        if (errorLogs.length > 50) {
          errorLogs.splice(50)
        }
        wx.setStorageSync('order_error_logs', errorLogs)
      } catch (storageErr) {
        console.error('[订单提交] 保存错误日志失败', storageErr)
      }
    } finally {
      this.setData({ submitting: false })
    }
  }
})
