const app = getApp()

Page({
  data: {
    orders: [],
    loading: false,
    isLogin: false,
    statusMap: {
      pending: '待确认',
      confirmed: '已确认',
      checked_in: '已入住',
      checked_out: '已退房',
      cancelled: '已取消'
    }
  },

  onShow() {
    this.setData({ isLogin: app.checkLogin() })
    if (app.checkLogin()) this.loadOrders()
  },

  async loadOrders() {
    this.setData({ loading: true })
    try {
      const res = await app.request({ url: '/order/my' })
      this.setData({ orders: res.data })
    } catch (err) {
      console.error(err)
    } finally {
      this.setData({ loading: false })
    }
  },

  goDetail(e) {
    wx.navigateTo({ url: `/pages/order-detail/order-detail?id=${e.currentTarget.dataset.id}` })
  },

  async cancelOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定要取消此订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await app.request({ url: `/order/${id}/cancel`, method: 'PUT' })
            wx.showToast({ title: '取消成功', icon: 'success' })
            this.loadOrders()
          } catch (err) {}
        }
      }
    })
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
