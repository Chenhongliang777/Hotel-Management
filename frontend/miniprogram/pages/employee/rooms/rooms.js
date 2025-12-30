const app = getApp()

Page({
  data: {
    rooms: [],
    loading: false,
    statusFilter: '',
    roomTypeFilter: '',
    roomTypeFilterIndex: 0,
    roomTypes: [],
    roomTypeOptions: ['全部房型']
  },

  onLoad() {
    this.loadRoomTypes()
    this.loadRooms()
  },

  onShow() {
    this.loadRooms()
  },

  async loadRoomTypes() {
    try {
      const res = await app.request({ url: '/roomType/available' })
      const roomTypes = res.data || []
      const roomTypeOptions = ['全部房型'].concat(roomTypes.map(t => t.name))
      this.setData({ 
        roomTypes: roomTypes,
        roomTypeOptions: roomTypeOptions
      })
      this.updateRoomTypeFilterIndex()
    } catch (err) {
      console.error('加载房型失败', err)
    }
  },

  async loadRooms() {
    this.setData({ loading: true })
    try {
      const requestData = {
        page: 1,
        size: 200
      }
      
      // 只添加有值的过滤参数
      if (this.data.roomTypeFilter) {
        requestData.roomTypeId = this.data.roomTypeFilter
      }
      if (this.data.statusFilter) {
        requestData.status = this.data.statusFilter
      }
      
      const res = await app.request({
        url: '/room/page',
        method: 'GET',
        data: requestData
      })
      const rooms = (res.data && res.data.records) || res.data || []
      // 确保所有数据字段都正确映射
      const processedRooms = rooms.map(room => {
        return {
          ...room,
          // 确保有默认值
          floor: room.floor || null,
          cleanStatus: room.cleanStatus || 'clean',
          status: room.status || 'available',
          // 确保 roomType 对象存在
          roomType: room.roomType || null
        }
      })
      this.setData({ 
        rooms: processedRooms
      })
    } catch (err) {
      console.error('加载房间失败', err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  onStatusFilterChange(e) {
    this.setData({ statusFilter: e.detail.value || '' })
    this.loadRooms()
  },

  onRoomTypeFilterChange(e) {
    const index = parseInt(e.detail.value)
    if (index === 0) {
      this.setData({ roomTypeFilter: '', roomTypeFilterIndex: 0 })
    } else {
      const selectedType = this.data.roomTypes[index - 1]
      this.setData({ 
        roomTypeFilter: selectedType ? selectedType.id : '',
        roomTypeFilterIndex: index
      })
    }
    this.loadRooms()
  },

  updateRoomTypeFilterIndex() {
    const { roomTypeFilter, roomTypes } = this.data
    if (roomTypeFilter === '') {
      this.setData({ roomTypeFilterIndex: 0 })
    } else {
      const index = roomTypes.findIndex(t => t.id == roomTypeFilter)
      this.setData({ roomTypeFilterIndex: index >= 0 ? index + 1 : 0 })
    }
  },

  async updateRoomStatus(e) {
    const roomId = e.currentTarget.dataset.id
    const currentStatus = e.currentTarget.dataset.status
    const room = this.data.rooms.find(r => r.id === roomId)
    
    if (!room) return

    const statusOptions = ['available', 'occupied', 'maintenance', 'reserved']
    const statusNames = ['可用', '已入住', '维修中', '已预订']
    const currentIndex = statusOptions.indexOf(currentStatus)

    wx.showActionSheet({
      itemList: statusNames,
      success: async (res) => {
        const newStatus = statusOptions[res.tapIndex]
        if (newStatus === currentStatus) return

        try {
          await app.request({
            url: `/room/${roomId}/status`,
            method: 'PUT',
            data: { status: newStatus }
          })
          wx.showToast({ title: '更新成功', icon: 'success' })
          this.loadRooms()
        } catch (err) {
          wx.showToast({ title: err.message || '更新失败', icon: 'none' })
        }
      }
    })
  },

  async updateCleanStatus(e) {
    const roomId = e.currentTarget.dataset.id
    const currentStatus = e.currentTarget.dataset.cleanstatus
    const room = this.data.rooms.find(r => r.id === roomId)
    
    if (!room) return

    const statusOptions = ['clean', 'dirty', 'cleaning']
    const statusNames = ['已清洁', '待清洁', '清洁中']
    const currentIndex = statusOptions.indexOf(currentStatus)

    wx.showActionSheet({
      itemList: statusNames,
      success: async (res) => {
        const newStatus = statusOptions[res.tapIndex]
        if (newStatus === currentStatus) return

        try {
          await app.request({
            url: `/room/${roomId}/clean-status`,
            method: 'PUT',
            data: { cleanStatus: newStatus }
          })
          wx.showToast({ title: '更新成功', icon: 'success' })
          this.loadRooms()
        } catch (err) {
          wx.showToast({ title: err.message || '更新失败', icon: 'none' })
        }
      }
    })
  },

  getStatusText(status) {
    const statusMap = {
      'available': '可用',
      'occupied': '已入住',
      'maintenance': '维修中',
      'reserved': '已预订'
    }
    return statusMap[status] || status
  },

  getStatusColor(status) {
    const colorMap = {
      'available': '#48bb78',
      'occupied': '#667eea',
      'maintenance': '#ed8936',
      'reserved': '#9f7aea'
    }
    return colorMap[status] || '#999'
  },

  getCleanStatusText(status) {
    const statusMap = {
      'clean': '已清洁',
      'dirty': '待清洁',
      'cleaning': '清洁中'
    }
    return statusMap[status] || status
  },

  getCleanStatusColor(status) {
    const colorMap = {
      'clean': '#48bb78',
      'dirty': '#f56565',
      'cleaning': '#ed8936'
    }
    return colorMap[status] || '#999'
  },

  getRoomTypeName(roomTypeFilter) {
    if (roomTypeFilter === '') {
      return '全部房型'
    }
    const roomType = this.data.roomTypes.find(t => t.id == roomTypeFilter)
    return roomType ? roomType.name : '全部房型'
  }
})

