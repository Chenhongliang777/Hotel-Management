const app = getApp()

Page({
  data: {
    orderId: '',
    order: null,
    roomType: null,
    today: '',
    checkInDate: '',
    checkOutDate: '',
    minCheckOut: '',
    originalTotalPrice: 0,
    newTotalPrice: 0,
    priceDiff: 0,
    absPriceDiff: 0,
    depositRate: 0.3,
    submitting: false
  },

  onLoad(options) {
    this.setData({
      orderId: options.orderId,
      today: this.formatDate(new Date())
    })
    this.loadOrder()
    this.loadConfig()
  },

  formatDate(date) {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  },

  async loadOrder() {
    try {
      const res = await app.request({ url: `/order/${this.data.orderId}` })
      const order = res.data
      this.setData({
        order,
        checkInDate: order.checkInDate,
        checkOutDate: order.checkOutDate,
        originalTotalPrice: parseFloat(order.totalPrice),
        minCheckOut: this.formatDate(new Date(new Date(order.checkInDate).getTime() + 86400000))
      })
      this.loadRoomType(order.roomTypeId)
      this.calculatePrice()
    } catch (err) {
      console.error(err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  async loadRoomType(roomTypeId) {
    try {
      const res = await app.request({ url: `/roomType/${roomTypeId}` })
      this.setData({ roomType: res.data })
      this.calculatePrice()
    } catch (err) {
      console.error(err)
    }
  },

  async loadConfig() {
    try {
      const res = await app.request({ url: '/config/deposit_rate' })
      if (res.data) {
        this.setData({ depositRate: parseFloat(res.data) })
        this.calculatePrice()
      }
    } catch (err) {}
  },

  onCheckInChange(e) {
    const checkInDate = e.detail.value
    const minCheckOut = this.formatDate(new Date(new Date(checkInDate).getTime() + 86400000))
    let checkOutDate = this.data.checkOutDate
    if (checkOutDate <= checkInDate) {
      checkOutDate = minCheckOut
    }
    this.setData({ checkInDate, minCheckOut, checkOutDate })
    this.calculatePrice()
  },

  onCheckOutChange(e) {
    this.setData({ checkOutDate: e.detail.value })
    this.calculatePrice()
  },

  calculatePrice() {
    const { checkInDate, checkOutDate, roomType, depositRate } = this.data
    if (!checkInDate || !checkOutDate || !roomType) return

    const start = new Date(checkInDate)
    const end = new Date(checkOutDate)
    const nights = Math.max(1, Math.ceil((end - start) / 86400000))
    const newTotalPrice = parseFloat((roomType.basePrice * nights).toFixed(2))
    const priceDiff = parseFloat((newTotalPrice - this.data.originalTotalPrice).toFixed(2))
    const absPriceDiff = Math.abs(priceDiff)

    this.setData({ newTotalPrice, priceDiff, absPriceDiff })
  },

  async submitModify() {
    const { order, checkInDate, checkOutDate, newTotalPrice, priceDiff, depositRate } = this.data

    if (!checkInDate || !checkOutDate) {
      wx.showToast({ title: '请选择日期', icon: 'none' })
      return
    }

    const start = new Date(checkInDate)
    const end = new Date(checkOutDate)
    const nights = Math.max(1, Math.ceil((end - start) / 86400000))

    this.setData({ submitting: true })
    try {
      // 更新订单信息
      await app.request({
        url: '/order',
        method: 'PUT',
        data: {
          id: order.id,
          checkInDate,
          checkOutDate,
          nights,
          totalPrice: newTotalPrice,
          deposit: parseFloat((newTotalPrice * depositRate).toFixed(2))
        }
      })

      wx.showToast({ title: '改订成功', icon: 'success' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (err) {
      console.error(err)
    } finally {
      this.setData({ submitting: false })
    }
  }
})

