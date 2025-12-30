const app = getApp()

Page({
  data: {
    checkInDate: '',
    checkOutDate: '',
    guestCount: 2,
    guestIndex: 0,
    guestOptions: [1, 2, 3, 4, 5, 6],
    roomTypes: [],
    loading: false,
    isEmpty: false,
    today: '',
    minCheckOut: '',
    sortOptions: ['默认', '价格从低到高', '价格从高到低'],
    sortIndex: 0
  },

  onLoad(options) {
    // 设置今天的日期
    const today = new Date()
    const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    
    const guestCount = parseInt(options.guestCount) || 2
    const guestIndex = Math.max(0, guestCount - 1)
    
    this.setData({
      checkInDate: options.checkInDate || '',
      checkOutDate: options.checkOutDate || '',
      guestCount: guestCount,
      guestIndex: guestIndex,
      today: todayStr,
      minCheckOut: todayStr
    })
    
    // 如果有日期参数，自动加载房型
    if (options.checkInDate && options.checkOutDate) {
      this.loadRoomTypes()
    }
  },
  
  // 计算最小退房日期
  getMinCheckOut(checkInDate) {
    if (!checkInDate) {
      return this.data.today
    }
    // 退房日期至少是入住日期的下一天
    const checkIn = new Date(checkInDate)
    checkIn.setDate(checkIn.getDate() + 1)
    const year = checkIn.getFullYear()
    const month = String(checkIn.getMonth() + 1).padStart(2, '0')
    const day = String(checkIn.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },
  
  onCheckInChange(e) {
    const checkInDate = e.detail.value
    const minCheckOut = this.getMinCheckOut(checkInDate)
    this.setData({ 
      checkInDate: checkInDate,
      checkOutDate: '', // 重置退房日期
      minCheckOut: minCheckOut
    })
  },
  
  onCheckOutChange(e) {
    const checkOutDate = e.detail.value
    this.setData({ checkOutDate: checkOutDate })
    // 如果入住日期已选择，自动搜索
    if (this.data.checkInDate) {
      this.loadRoomTypes()
    }
  },
  
  onGuestChange(e) {
    const guestIndex = parseInt(e.detail.value)
    const guestCount = this.data.guestOptions[guestIndex]
    this.setData({ 
      guestIndex: guestIndex,
      guestCount: guestCount
    })
    // 如果日期已选择，自动搜索
    if (this.data.checkInDate && this.data.checkOutDate) {
      this.loadRoomTypes()
    }
  },

  onSortChange(e) {
    const sortIndex = parseInt(e.detail.value)
    this.setData({ sortIndex })
    // 对当前列表进行排序
    const sortedRoomTypes = this.sortRoomTypes([...this.data.roomTypes])
    this.setData({ roomTypes: sortedRoomTypes })
  },

  sortRoomTypes(roomTypes) {
    const { sortIndex } = this.data
    if (sortIndex === 0) {
      // 默认排序，保持原顺序
      return roomTypes
    } else if (sortIndex === 1) {
      // 价格从低到高
      return roomTypes.sort((a, b) => parseFloat(a.basePrice) - parseFloat(b.basePrice))
    } else if (sortIndex === 2) {
      // 价格从高到低
      return roomTypes.sort((a, b) => parseFloat(b.basePrice) - parseFloat(a.basePrice))
    }
    return roomTypes
  },

  async loadRoomTypes() {
    const { checkInDate, checkOutDate, guestCount } = this.data
    
    // 验证日期
    if (!checkInDate || !checkOutDate) {
      wx.showToast({ title: '请选择入住和退房日期', icon: 'none' })
      return
    }
    
    if (new Date(checkInDate) >= new Date(checkOutDate)) {
      wx.showToast({ title: '退房日期必须晚于入住日期', icon: 'none' })
      return
    }
    
    this.setData({ loading: true, isEmpty: false })
    
    try {
      const res = await app.request({
        url: '/roomType/search',
        method: 'GET',
        data: { 
          checkInDate, 
          checkOutDate, 
          guestCount: parseInt(guestCount) 
        }
      })
      
      // 解析图片列表
      let roomTypes = (res.data || []).map(roomType => {
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
      
      // 根据排序偏好排序
      roomTypes = this.sortRoomTypes(roomTypes)
      
      this.setData({ 
        roomTypes,
        isEmpty: roomTypes.length === 0,
        loading: false
      })
      
      if (roomTypes.length === 0) {
        wx.showToast({ title: '暂无可用房型', icon: 'none' })
      }
    } catch (err) {
      console.error('搜索房型失败:', err)
      this.setData({ loading: false, isEmpty: true })
      wx.showToast({ 
        title: err.message || '搜索失败，请稍后重试', 
        icon: 'none',
        duration: 2000
      })
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

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    const { checkInDate, checkOutDate, guestCount } = this.data
    wx.navigateTo({
      url: `/pages/room-detail/room-detail?id=${id}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&guestCount=${guestCount}`
    })
  }
})
