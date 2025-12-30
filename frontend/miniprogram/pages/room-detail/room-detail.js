const app = getApp()

Page({
  data: {
    id: '',
    checkInDate: '',
    checkOutDate: '',
    guestCount: 2,
    roomType: null,
    facilities: [],
    nights: 1,
    totalPrice: 0
  },

  onLoad(options) {
    this.setData({
      id: options.id,
      checkInDate: options.checkInDate || '',
      checkOutDate: options.checkOutDate || '',
      guestCount: options.guestCount || 2
    })
    this.loadRoomType()
  },

  async loadRoomType() {
    try {
      const res = await app.request({ url: `/roomType/${this.data.id}` })
      const roomType = res.data
      let facilities = []
      if (roomType.facilities) {
        try { facilities = JSON.parse(roomType.facilities) } catch (e) {}
      }
      
      // 解析图片列表
      let images = []
      if (roomType.images) {
        try {
          images = typeof roomType.images === 'string' 
            ? JSON.parse(roomType.images) 
            : roomType.images
        } catch (e) {
          images = []
        }
      }
      // 如果没有图片，使用网络占位图
      if (!images || images.length === 0) {
        images = ['https://dummyimage.com/400x300/667eea/ffffff.png&text=Room']
      }
      roomType.images = images
      
      const nights = this.calculateNights()
      const totalPrice = (roomType.basePrice * nights).toFixed(2)
      
      this.setData({ roomType, facilities, nights, totalPrice })
    } catch (err) {
      console.error(err)
    }
  },

  calculateNights() {
    const { checkInDate, checkOutDate } = this.data
    if (!checkInDate || !checkOutDate) return 1
    const start = new Date(checkInDate)
    const end = new Date(checkOutDate)
    return Math.max(1, Math.ceil((end - start) / 86400000))
  },

  onImageError(e) {
    const index = e.currentTarget.dataset.index
    const roomType = this.data.roomType
    if (roomType && roomType.images && roomType.images[index]) {
      roomType.images[index] = 'https://dummyimage.com/400x300/667eea/ffffff.png&text=Room'
      this.setData({ roomType })
    }
  },

  goBooking() {
    if (!app.checkLogin()) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    const { id, checkInDate, checkOutDate, guestCount } = this.data
    wx.navigateTo({
      url: `/pages/booking/booking?roomTypeId=${id}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&guestCount=${guestCount}`
    })
  }
})
