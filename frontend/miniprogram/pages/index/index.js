const app = getApp()

Page({
  data: {
    today: '',
    checkInDate: '',
    checkOutDate: '',
    minCheckOut: '',
    guestOptions: [1, 2, 3, 4, 5, 6],
    guestIndex: 1,
    roomTypes: []
  },

  onLoad() {
    const today = this.formatDate(new Date())
    const tomorrow = this.formatDate(new Date(Date.now() + 86400000))
    this.setData({
      today,
      checkInDate: today,
      checkOutDate: tomorrow,
      minCheckOut: tomorrow
    })
    this.loadRoomTypes()
  },

  onShow() {
    this.loadRoomTypes()
  },

  formatDate(date) {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  },

  onCheckInChange(e) {
    const checkInDate = e.detail.value
    const minCheckOut = this.formatDate(new Date(new Date(checkInDate).getTime() + 86400000))
    let checkOutDate = this.data.checkOutDate
    if (checkOutDate <= checkInDate) {
      checkOutDate = minCheckOut
    }
    this.setData({ checkInDate, minCheckOut, checkOutDate })
  },

  onCheckOutChange(e) {
    this.setData({ checkOutDate: e.detail.value })
  },

  onGuestChange(e) {
    this.setData({ guestIndex: e.detail.value })
  },

  async loadRoomTypes() {
    try {
      const res = await app.request({ url: '/roomType/available' })
      // 解析图片列表
      const roomTypes = res.data.map(roomType => {
        if (roomType.images) {
          try {
            roomType.images = typeof roomType.images === 'string' 
              ? JSON.parse(roomType.images) 
              : roomType.images
          } catch (e) {
            roomType.images = []
          }
        } else {
          roomType.images = []
        }
        // 如果没有图片，使用网络占位图
        if (!roomType.images || roomType.images.length === 0) {
          roomType.images = ['https://dummyimage.com/400x300/667eea/ffffff.png&text=Room']
        }
        return roomType
      })
      this.setData({ roomTypes })
    } catch (err) {
      console.error(err)
    }
  },

  onImageError(e) {
    const index = e.currentTarget.dataset.index
    const roomTypes = this.data.roomTypes
    if (roomTypes[index]) {
      roomTypes[index].images[0] = 'https://dummyimage.com/400x300/667eea/ffffff.png&text=Room'
      this.setData({ roomTypes })
    }
  },

  searchRooms() {
    const { checkInDate, checkOutDate, guestIndex, guestOptions } = this.data
    
    // 验证日期
    if (!checkInDate || !checkOutDate) {
      wx.showToast({ title: '请选择入住和退房日期', icon: 'none' })
      return
    }
    
    if (new Date(checkInDate) >= new Date(checkOutDate)) {
      wx.showToast({ title: '退房日期必须晚于入住日期', icon: 'none' })
      return
    }
    
    const guestCount = guestOptions[guestIndex]
    wx.navigateTo({
      url: `/pages/rooms/rooms?checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&guestCount=${guestCount}`
    })
  },

  goRoomDetail(e) {
    const id = e.currentTarget.dataset.id
    const { checkInDate, checkOutDate } = this.data
    wx.navigateTo({
      url: `/pages/room-detail/room-detail?id=${id}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}`
    })
  }
})
