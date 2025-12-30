const app = getApp()

Page({
  data: {
    loading: false,
    stats: {
      todayOrders: 0,
      todayCheckIn: 0,
      todayCheckOut: 0,
      currentGuests: 0,
      pendingOrders: 0,
      todayRevenue: 0,
      todayRevenueFormatted: '0.00',
      monthlyRevenue: 0,
      monthlyRevenueFormatted: '0.00',
      lowStockCount: 0
    },
    roomStats: [],
    revenueChart: [],
    dateRange: {
      start: '',
      end: ''
    }
  },

  onLoad() {
    this.initDateRange()
    this.loadDashboardStats()
    this.loadRoomStatusStats()
  },

  onShow() {
    this.loadDashboardStats()
    this.loadRoomStatusStats()
  },

  initDateRange() {
    const today = new Date()
    const endDate = today.toISOString().split('T')[0]
    const startDate = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    
    this.setData({
      'dateRange.start': startDate,
      'dateRange.end': endDate
    }, () => {
      // 初始化后加载营收图表
      this.loadRevenueChart()
    })
  },

  async loadDashboardStats() {
    this.setData({ loading: true })
    try {
      const res = await app.request({ url: '/statistics/dashboard' })
      if (res.data) {
        const todayRevenue = res.data.todayRevenue || 0
        const monthlyRevenue = res.data.monthlyRevenue || 0
        this.setData({
          stats: {
            todayOrders: res.data.todayOrders || 0,
            todayCheckIn: res.data.todayCheckIn || 0,
            todayCheckOut: res.data.todayCheckOut || 0,
            currentGuests: res.data.currentGuests || 0,
            pendingOrders: res.data.pendingOrders || 0,
            todayRevenue: todayRevenue,
            todayRevenueFormatted: this.formatCurrency(todayRevenue),
            monthlyRevenue: monthlyRevenue,
            monthlyRevenueFormatted: this.formatCurrency(monthlyRevenue),
            lowStockCount: res.data.lowStockCount || 0
          }
        })
      }
    } catch (err) {
      console.error('加载统计数据失败', err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  async loadRoomStatusStats() {
    try {
      const res = await app.request({ url: '/statistics/room-status' })
      if (res.data) {
        // 计算总房间数用于进度条显示
        const statsList = res.data || []
        const total = statsList.reduce((sum, stat) => sum + (stat.count || 0), 0)
        const roomStats = statsList.map(item => ({
          ...item,
          total: total
        }))
        this.setData({ roomStats })
      }
    } catch (err) {
      console.error('加载房态统计失败', err)
    }
  },

  async loadRevenueChart() {
    if (!this.data.dateRange.start || !this.data.dateRange.end) {
      wx.showToast({ title: '请选择日期范围', icon: 'none' })
      return
    }

    try {
      const res = await app.request({
        url: '/statistics/revenue-chart',
        method: 'GET',
        data: {
          startDate: this.data.dateRange.start,
          endDate: this.data.dateRange.end
        }
      })
      if (res.data) {
        // 格式化营收图表数据中的金额
        const chartData = (res.data || []).map(item => ({
          ...item,
          revenueFormatted: this.formatCurrency(item.revenue || 0)
        }))
        this.setData({ revenueChart: chartData })
      }
    } catch (err) {
      console.error('加载营收图表失败', err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  onStartDateChange(e) {
    this.setData({ 'dateRange.start': e.detail.value }, () => {
      if (this.data.dateRange.end) {
        this.loadRevenueChart()
      }
    })
  },

  onEndDateChange(e) {
    this.setData({ 'dateRange.end': e.detail.value }, () => {
      if (this.data.dateRange.start) {
        this.loadRevenueChart()
      }
    })
  },

  formatCurrency(amount) {
    if (!amount) return '0.00'
    return Number(amount).toFixed(2)
  }
})

