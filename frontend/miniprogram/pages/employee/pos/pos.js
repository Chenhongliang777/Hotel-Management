const app = getApp()

Page({
  data: {
    orders: [],
    selectedOrder: null,
    selectedOrderIndex: -1,
    inventoryItems: [],
    selectedItem: null,
    selectedItemIndex: -1,
    quantity: 1,
    remark: '',
    totalPrice: '0.00',
    loading: false,
    submitting: false,
    records: [],
    showRecords: false
  },

  onLoad() {
    this.loadCheckedInOrders()
    this.loadInventoryItems()
  },

  onShow() {
    this.loadCheckedInOrders()
  },

  async loadCheckedInOrders() {
    try {
      const res = await app.request({
        url: '/order/page',
        method: 'GET',
        data: {
          page: 1,
          size: 100,
          status: 'checked_in'
        }
      })
      const ordersData = (res.data && res.data.records) || res.data || []
      const orders = ordersData.map(order => ({
        ...order,
        label: `${order.roomNumber || '未分配'} - ${order.guestName} (${order.orderNo})`
      }))
      this.setData({ orders })
    } catch (err) {
      console.error('加载订单失败', err)
    }
  },

  async loadInventoryItems() {
    try {
      const res = await app.request({ url: '/inventory/list' })
      let itemsData = res.data
      if (res.data && res.data.data) {
        itemsData = res.data.data
      } else if (Array.isArray(res.data)) {
        itemsData = res.data
      }
      const items = (itemsData || []).map(item => ({
        ...item,
        label: `${item.name} - ¥${item.price || 0} (库存:${item.quantity || 0})`
      }))
      this.setData({ inventoryItems: items })
    } catch (err) {
      console.error('加载库存物品失败', err)
    }
  },

  onOrderChange(e) {
    const index = parseInt(e.detail.value)
    const order = this.data.orders[index]
    this.setData({
      selectedOrderIndex: index,
      selectedOrder: order || null
    })
    if (order) {
      this.loadOrderRecords(order.id)
    }
  },

  onItemChange(e) {
    const index = parseInt(e.detail.value)
    const item = this.data.inventoryItems[index]
    this.setData({
      selectedItemIndex: index,
      selectedItem: item || null
    })
    this.updateTotalPrice()
  },

  onQuantityInput(e) {
    const value = parseInt(e.detail.value) || 1
    this.setData({ quantity: value > 0 ? value : 1 })
    this.updateTotalPrice()
  },

  increaseQuantity() {
    this.setData({ quantity: this.data.quantity + 1 })
    this.updateTotalPrice()
  },

  decreaseQuantity() {
    if (this.data.quantity > 1) {
      this.setData({ quantity: this.data.quantity - 1 })
      this.updateTotalPrice()
    }
  },

  updateTotalPrice() {
    const { selectedItem, quantity } = this.data
    if (selectedItem && selectedItem.price) {
      const total = (selectedItem.price * quantity).toFixed(2)
      this.setData({ totalPrice: total })
    } else {
      this.setData({ totalPrice: '0.00' })
    }
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return ''
    return dateTimeStr.replace('T', ' ').substring(0, 19)
  },

  async loadOrderRecords(orderId) {
    try {
      const res = await app.request({
        url: `/pos/order/${orderId}`
      })
      const records = (res.data || []).map(r => ({
        ...r,
        createTime: this.formatDateTime(r.createTime)
      }))
      this.setData({ records })
    } catch (err) {
      console.error('加载消费记录失败', err)
    }
  },

  toggleRecords() {
    this.setData({ showRecords: !this.data.showRecords })
  },

  async handleSubmit() {
    const { selectedOrder, selectedItem, quantity, remark } = this.data

    if (!selectedOrder) {
      wx.showToast({ title: '请选择订单', icon: 'none' })
      return
    }

    if (!selectedItem) {
      wx.showToast({ title: '请选择消费物品', icon: 'none' })
      return
    }

    if (quantity <= 0) {
      wx.showToast({ title: '请输入有效数量', icon: 'none' })
      return
    }

    if ((selectedItem.quantity || 0) < quantity) {
      wx.showToast({ title: '库存不足', icon: 'none' })
      return
    }

    this.setData({ submitting: true })

    try {
      const totalPrice = (selectedItem.price || 0) * quantity
      await app.request({
        url: '/pos',
        method: 'POST',
        data: {
          orderId: selectedOrder.id,
          orderNo: selectedOrder.orderNo,
          guestId: selectedOrder.guestId,
          roomId: selectedOrder.roomId,
          itemId: selectedItem.id,
          itemName: selectedItem.name,
          quantity: quantity,
          unitPrice: selectedItem.price || 0,
          totalPrice: totalPrice,
          category: selectedItem.category || '',
          remark: remark
        }
      })

      wx.showToast({ title: '录入成功', icon: 'success' })
      
      // 重置表单
      this.setData({
        selectedItem: null,
        selectedItemIndex: -1,
        quantity: 1,
        remark: '',
        totalPrice: '0.00'
      })

      // 刷新数据
      this.loadInventoryItems()
      if (selectedOrder) {
        this.loadOrderRecords(selectedOrder.id)
      }
    } catch (err) {
      wx.showToast({ title: err.message || '录入失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  },

  getTotalPrice() {
    const { selectedItem, quantity } = this.data
    if (!selectedItem || !selectedItem.price) return '0.00'
    return (selectedItem.price * quantity).toFixed(2)
  }
})
