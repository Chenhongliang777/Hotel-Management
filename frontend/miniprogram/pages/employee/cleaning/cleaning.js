const app = getApp()

Page({
  data: {
    type: '', // my, pending, 或空（全部）
    tasks: [],
    loading: false,
    statusFilter: ''
  },

  onLoad(options) {
    if (options.type) {
      this.setData({ type: options.type })
      const title = options.type === 'my' ? '我的任务' : options.type === 'pending' ? '待分配任务' : '清洁任务'
      wx.setNavigationBarTitle({ title })
    }
    this.loadTasks()
  },

  onShow() {
    this.loadTasks()
  },

  async loadTasks() {
    this.setData({ loading: true })
    try {
      let res
      if (this.data.type === 'my') {
        res = await app.request({ url: '/cleaning/my' })
      } else if (this.data.type === 'pending') {
        res = await app.request({ url: '/cleaning/pending' })
      } else {
        res = await app.request({
          url: '/cleaning/page',
          method: 'GET',
          data: {
            page: 1,
            size: 100,
            status: this.data.statusFilter || undefined
          }
        })
      }
      
      this.setData({ 
        tasks: (res.data && res.data.records) || res.data || []
      })
    } catch (err) {
      console.error('加载任务失败', err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  onStatusFilterChange(e) {
    this.setData({ statusFilter: e.detail.value || '' })
    this.loadTasks()
  },

  async handleStartTask(e) {
    const taskId = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认开始',
      content: '确定要开始这个任务吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await app.request({
              url: `/cleaning/${taskId}/start`,
              method: 'PUT'
            })
            wx.showToast({ title: '任务已开始', icon: 'success' })
            this.loadTasks()
          } catch (err) {
            wx.showToast({ title: err.message || '操作失败', icon: 'none' })
          }
        }
      }
    })
  },

  async handleCompleteTask(e) {
    const taskId = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认完成',
      content: '确定要完成这个任务吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await app.request({
              url: `/cleaning/${taskId}/complete`,
              method: 'PUT'
            })
            wx.showToast({ title: '任务已完成', icon: 'success' })
            this.loadTasks()
          } catch (err) {
            wx.showToast({ title: err.message || '操作失败', icon: 'none' })
          }
        }
      }
    })
  },

  async handleAssignTask(e) {
    const taskId = e.currentTarget.dataset.id
    const task = this.data.tasks.find(t => t.id === taskId)
    
    if (!task) return

    // 获取员工列表（这里简化处理，实际应该调用员工列表接口）
    wx.showModal({
      title: '分配任务',
      content: '请输入员工ID',
      editable: true,
      placeholderText: '请输入员工ID',
      success: async (modalRes) => {
        if (modalRes.confirm && modalRes.content) {
          try {
            // 这里需要获取员工信息，简化处理
            const assigneeId = parseInt(modalRes.content)
            if (isNaN(assigneeId)) {
              wx.showToast({ title: '请输入有效的员工ID', icon: 'none' })
              return
            }

            await app.request({
              url: `/cleaning/${taskId}/assign`,
              method: 'PUT',
              data: {
                assigneeId: assigneeId,
                assigneeName: '员工' + assigneeId // 实际应该从员工信息获取
              }
            })
            wx.showToast({ title: '分配成功', icon: 'success' })
            this.loadTasks()
          } catch (err) {
            wx.showToast({ title: err.message || '分配失败', icon: 'none' })
          }
        }
      }
    })
  },

  getStatusText(status) {
    const statusMap = {
      'pending': '待分配',
      'assigned': '已分配',
      'in_progress': '进行中',
      'completed': '已完成',
      'cancelled': '已取消'
    }
    return statusMap[status] || status
  },

  getStatusColor(status) {
    const colorMap = {
      'pending': '#ed8936',
      'assigned': '#667eea',
      'in_progress': '#38b2ac',
      'completed': '#48bb78',
      'cancelled': '#f56565'
    }
    return colorMap[status] || '#999'
  }
})

