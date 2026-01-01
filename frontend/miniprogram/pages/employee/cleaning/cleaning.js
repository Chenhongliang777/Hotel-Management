const app = getApp()

Page({
  data: {
    type: '', // my, pending, 或空（全部）
    tasks: [],
    rooms: [],
    roomOptions: [],
    employees: [],
    employeeNames: [],
    loading: false,
    statusFilter: '',
    statusFilterIndex: 0,
    statusFilterText: '全部状态',
    statusOptions: ['全部状态', '待分配', '已分配', '进行中', '已完成', '已取消'],
    statusValues: ['', 'pending', 'assigned', 'in_progress', 'completed', 'cancelled']
  },

  onLoad(options) {
    if (options.type) {
      this.setData({ type: options.type })
      const title = options.type === 'my' ? '我的任务' : options.type === 'pending' ? '待分配任务' : '清洁任务'
      wx.setNavigationBarTitle({ title })
    }
    this.loadRooms()
    this.loadEmployees()
    this.loadTasks()
  },

  async loadEmployees() {
    try {
      const res = await app.request({ 
        url: '/employee/page',
        data: { page: 1, size: 100 }
      })
      const employees = (res.data && res.data.records) || res.data || []
      console.log('[清洁任务] 员工列表:', employees)
      // 显示所有员工供选择，优先使用realName
      const employeeNames = employees.map(e => e.realName || e.name || e.username || ('员工' + e.id))
      this.setData({ employees, employeeNames })
    } catch (err) {
      console.error('加载员工列表失败', err)
    }
  },

  async loadRooms() {
    try {
      const res = await app.request({ url: '/room/list' })
      const rooms = res.data || []
      const roomOptions = rooms.map(r => r.roomNumber)
      this.setData({ rooms, roomOptions })
    } catch (err) {
      console.error('加载房间列表失败', err)
    }
  },

  showAddTaskModal() {
    if (!this.data.rooms || this.data.rooms.length === 0) {
      wx.showToast({ title: '正在加载房间...', icon: 'none' })
      this.loadRooms()
      return
    }
    wx.showActionSheet({
      itemList: this.data.roomOptions,
      success: async (res) => {
        const room = this.data.rooms[res.tapIndex]
        if (!room) return
        
        try {
          await app.request({
            url: '/cleaning',
            method: 'POST',
            data: {
              roomId: room.id,
              roomNumber: room.roomNumber,
              taskType: '日常清洁',
              status: 'pending'
            }
          })
          wx.showToast({ title: '创建成功', icon: 'success' })
          this.loadTasks()
        } catch (err) {
          wx.showToast({ title: err.message || '创建失败', icon: 'none' })
        }
      }
    })
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
        const params = { page: 1, size: 100 }
        if (this.data.statusFilter) {
          params.status = this.data.statusFilter
        }
        res = await app.request({
          url: '/cleaning/page',
          method: 'GET',
          data: params
        })
      }
      
      console.log('[清洁任务] API响应:', res)
      
      // 兼容多种返回格式
      let tasksData = []
      if (res.data) {
        if (Array.isArray(res.data)) {
          tasksData = res.data
        } else if (res.data.records && Array.isArray(res.data.records)) {
          tasksData = res.data.records
        } else if (res.data.data && Array.isArray(res.data.data)) {
          tasksData = res.data.data
        }
      }
      
      console.log('[清洁任务] 解析后的任务数据:', tasksData)
      
      // 预处理任务数据，添加显示文本和颜色
      const processedTasks = tasksData.map(task => {
        const status = task.status || 'pending'
        return {
          ...task,
          statusText: this.getStatusText(status),
          statusColor: this.getStatusColor(status),
          createTime: this.formatDateTime(task.createTime),
          completeTime: this.formatDateTime(task.finishTime || task.completeTime)
        }
      })
      this.setData({ tasks: processedTasks })
    } catch (err) {
      console.error('加载任务失败', err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  onStatusFilterChange(e) {
    const index = parseInt(e.detail.value)
    const statusValue = this.data.statusValues[index] || ''
    const statusText = this.data.statusOptions[index] || '全部状态'
    this.setData({ 
      statusFilter: statusValue,
      statusFilterIndex: index,
      statusFilterText: statusText
    })
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

    if (!this.data.employees || this.data.employees.length === 0) {
      wx.showToast({ title: '正在加载员工...', icon: 'none' })
      this.loadEmployees()
      return
    }

    wx.showActionSheet({
      itemList: this.data.employeeNames,
      success: async (res) => {
        const employee = this.data.employees[res.tapIndex]
        if (!employee) return

        try {
          await app.request({
            url: `/cleaning/${taskId}/assign`,
            method: 'PUT',
            data: {
              assigneeId: employee.id,
              assigneeName: employee.realName || employee.name || employee.username || ('员工' + employee.id)
            }
          })
          wx.showToast({ title: '分配成功', icon: 'success' })
          this.loadTasks()
        } catch (err) {
          wx.showToast({ title: err.message || '分配失败', icon: 'none' })
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
  },

  formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return ''
    // 将ISO格式的T替换为空格
    return dateTimeStr.replace('T', ' ').substring(0, 19)
  }
})

