const app = getApp()

Page({
  data: {
    type: '', // checkin, checkout, 或空（全部）
    orders: [],
    loading: false,
    statusFilter: '',
    keyword: ''
  },

  onLoad(options) {
    if (options.type) {
      this.setData({ type: options.type })
      const title = options.type === 'checkin' ? '今日入住' : options.type === 'checkout' ? '今日退房' : '订单管理'
      wx.setNavigationBarTitle({ title })
    }
    this.loadOrders()
  },

  onShow() {
    this.loadOrders()
  },

  async loadOrders() {
    this.setData({ loading: true })
    try {
      let res
      if (this.data.type === 'checkin') {
        res = await app.request({ url: '/order/today-checkin' })
      } else if (this.data.type === 'checkout') {
        res = await app.request({ url: '/order/today-checkout' })
      } else {
        res = await app.request({ 
          url: '/order/page',
          method: 'GET',
          data: {
            page: 1,
            size: 100,
            keyword: this.data.keyword,
            status: this.data.statusFilter
          }
        })
      }
      
      this.setData({ 
        orders: (res.data && res.data.records) || res.data || []
      })
    } catch (err) {
      console.error('加载订单失败', err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    this.loadOrders()
  },

  onStatusChange(e) {
    const index = parseInt(e.detail.value)
    const statusMap = ['', 'pending', 'confirmed', 'checked_in', 'checked_out', 'cancelled']
    this.setData({ statusFilter: statusMap[index] || '' })
    this.loadOrders()
  },

  goOrderDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
  },

  async handleCheckIn(e) {
    const orderId = e.currentTarget.dataset.id
    const order = this.data.orders.find(o => o.id === orderId)
    
    if (!order) return

    // 选择房间
    try {
      const roomsRes = await app.request({ 
        url: `/room/type/${order.roomTypeId}`,
        method: 'GET'
      })
      const availableRooms = (roomsRes.data || []).filter(r => r.status === 'available')
      
      if (availableRooms.length === 0) {
        wx.showToast({ title: '该房型暂无可用房间', icon: 'none' })
        return
      }

      const roomNames = availableRooms.map(r => r.roomNumber).join('、')
      wx.showModal({
        title: '选择房间',
        content: `可用房间：${roomNames}，请选择一间办理入住`,
        editable: true,
        placeholderText: '请输入房间号',
        success: async (modalRes) => {
          if (modalRes.confirm && modalRes.content) {
            const selectedRoom = availableRooms.find(r => r.roomNumber === modalRes.content)
            if (!selectedRoom) {
              wx.showToast({ title: '房间号不正确', icon: 'none' })
              return
            }

            try {
              await app.request({
                url: `/order/${orderId}/checkin`,
                method: 'PUT',
                data: { roomId: selectedRoom.id }
              })
              wx.showToast({ title: '办理入住成功', icon: 'success' })
              this.loadOrders()
            } catch (err) {
              wx.showToast({ title: err.message || '办理入住失败', icon: 'none' })
            }
          }
        }
      })
    } catch (err) {
      wx.showToast({ title: '获取房间信息失败', icon: 'none' })
    }
  },

  async handleCheckOut(e) {
    const orderId = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认退房',
      content: '确定要办理退房吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await app.request({
              url: `/order/${orderId}/checkout`,
              method: 'PUT'
            })
            wx.showToast({ title: '办理退房成功', icon: 'success' })
            this.loadOrders()
          } catch (err) {
            wx.showToast({ title: err.message || '办理退房失败', icon: 'none' })
          }
        }
      }
    })
  },

  getStatusText(status) {
    const statusMap = {
      'pending': '待确认',
      'confirmed': '已确认',
      'checked_in': '已入住',
      'checked_out': '已退房',
      'cancelled': '已取消'
    }
    return statusMap[status] || status
  },

  getStatusColor(status) {
    const colorMap = {
      'pending': '#ed8936',
      'confirmed': '#667eea',
      'checked_in': '#48bb78',
      'checked_out': '#999',
      'cancelled': '#f56565'
    }
    return colorMap[status] || '#999'
  }
})

