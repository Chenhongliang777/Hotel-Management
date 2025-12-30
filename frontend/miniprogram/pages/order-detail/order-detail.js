const app = getApp()

Page({
  data: {
    order: null,
    payments: [],
    statusMap: { 
      pending: '待确认', 
      confirmed: '已确认', 
      checked_in: '已入住', 
      checked_out: '已退房', 
      cancelled: '已取消' 
    },
    paymentTypeMap: {
      deposit: '保证金',
      balance: '补差价',
      refund: '退款'
    },
    unpaidAmount: 0,
    totalAmount: 0,
    nextPayAmount: 0,
    isModified: false,
    showPayModal: false,
    payAmount: 0,
    paying: false
  },

  onLoad(options) {
    this.loadOrder(options.id)
  },

  onShow() {
    if (this.data.order) {
      this.loadOrder(this.data.order.id)
    }
  },

  async loadOrder(id) {
    try {
      const res = await app.request({ url: `/order/${id}` })
      const order = res.data
      // 应付总计 = 房费 + 额外消费
      const totalAmount = parseFloat((parseFloat(order.totalPrice || 0) + parseFloat(order.extraCharges || 0)).toFixed(2))
      // 待付金额 = 应付总计 - 已付金额
      const unpaidAmount = parseFloat((totalAmount - parseFloat(order.paidAmount || 0)).toFixed(2))
      // 判断是否已支付保证金
      const paidAmount = parseFloat(order.paidAmount || 0)
      const deposit = parseFloat(order.deposit || 0)
      const hasPaidDeposit = paidAmount >= deposit
      // 计算本次应支付金额：如果还没支付保证金，支付保证金；否则支付剩余金额
      const nextPayAmount = !hasPaidDeposit ? deposit : unpaidAmount
      // 判断是否已改订过（通过检查 remark 中是否包含改订标记）
      const isModified = order.remark && order.remark.includes('[已改订]')
      this.setData({ order, unpaidAmount, totalAmount, nextPayAmount, isModified })
      this.loadPayments(id)
    } catch (err) {
      console.error(err)
    }
  },

  async loadPayments(orderId) {
    try {
      const res = await app.request({ url: `/payment/order/${orderId}` })
      this.setData({ payments: res.data || [] })
    } catch (err) {
      console.error(err)
    }
  },

  async cancelOrder() {
    wx.showModal({
      title: '提示',
      content: '确定要取消此订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await app.request({ url: `/order/${this.data.order.id}/cancel`, method: 'PUT' })
            wx.showToast({ title: '取消成功', icon: 'success' })
            this.loadOrder(this.data.order.id)
          } catch (err) {}
        }
      }
    })
  },

  showPay() {
    const { order, unpaidAmount } = this.data
    if (unpaidAmount <= 0) {
      wx.showToast({ title: '订单已付清', icon: 'none' })
      return
    }
    // 判断是否已支付保证金：如果已付金额小于保证金，说明还没支付保证金
    const paidAmount = parseFloat(order.paidAmount || 0)
    const deposit = parseFloat(order.deposit || 0)
    const hasPaidDeposit = paidAmount >= deposit
    
    // 如果还没支付保证金，支付保证金；否则支付剩余金额
    const payAmount = !hasPaidDeposit ? deposit : unpaidAmount
    this.setData({ 
      showPayModal: true, 
      payAmount: payAmount.toFixed(2) 
    })
  },

  hidePayModal() {
    this.setData({ showPayModal: false })
  },

  async confirmPay() {
    const { order, payAmount } = this.data
    if (!app.checkLogin()) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }

    this.setData({ paying: true })
    try {
      // 判断是否已支付保证金
      const paidAmount = parseFloat(order.paidAmount || 0)
      const deposit = parseFloat(order.deposit || 0)
      const hasPaidDeposit = paidAmount >= deposit
      
      // 根据是否已支付保证金来判断支付类型
      const paymentType = !hasPaidDeposit ? 'deposit' : 'balance'
      
      await app.request({
        url: '/payment',
        method: 'POST',
        data: {
          orderId: order.id,
          amount: parseFloat(payAmount),
          paymentType: paymentType,
          paymentMethod: 'wechat',
          remark: paymentType === 'deposit' ? '支付保证金' : '补差价'
        }
      })
      
      wx.showToast({ title: '支付成功', icon: 'success' })
      this.setData({ showPayModal: false })
      this.loadOrder(order.id)
    } catch (err) {
      console.error(err)
    } finally {
      this.setData({ paying: false })
    }
  },

  formatDate(dateStr) {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    const h = String(date.getHours()).padStart(2, '0')
    const min = String(date.getMinutes()).padStart(2, '0')
    return `${y}-${m}-${d} ${h}:${min}`
  },

  stopPropagation() {
    // 阻止事件冒泡
  },

  goModify() {
    const { isModified } = this.data
    if (isModified) {
      wx.showToast({ title: '订单已改订过，不能重复改订', icon: 'none' })
      return
    }
    wx.navigateTo({ url: `/pages/order-modify/order-modify?orderId=${this.data.order.id}` })
  }
})
