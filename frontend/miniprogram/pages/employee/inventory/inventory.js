const app = getApp()

// 分类中文映射
const categoryMap = {
  'toiletry': '洗浴用品',
  'beverage': '饮料',
  'food': '食品',
  'supplies': '日用品',
  'other': '其他'
}

Page({
  data: {
    items: [],
    allItems: [], // 所有物品，用于下拉选择
    allItemsForPicker: [], // 用于picker的格式
    loading: false,
    keyword: '',
    category: '',
    categories: [],
    categoryOptions: ['全部'],
    categoryOptionsDisplay: ['全部'], // 用于显示的中文分类
    categoryIndex: 0,
    showLowStock: false,
    page: 1,
    size: 100, // 增加每页数量，确保能显示更多商品
    hasMore: true,
    showAddModal: false,
    showEditModal: false,
    showStockInModal: false,
    showStockOutModal: false,
    submitting: false,
    addForm: {
      name: '',
      category: '',
      categoryIndex: 0,
      unit: '',
      price: '',
      minStock: '',
      description: ''
    },
    editForm: {
      id: null,
      name: '',
      category: '',
      categoryIndex: 0,
      unit: '',
      price: '',
      minStock: '',
      description: ''
    },
    stockInForm: {
      itemIndex: -1,
      itemId: null,
      quantity: 1,
      remark: ''
    },
    stockOutForm: {
      itemIndex: -1,
      itemId: null,
      quantity: 1,
      remark: ''
    },
    categoryOptionsForAdd: [
      { label: '请选择类别', value: '' },
      { label: '洗浴用品', value: 'toiletry' },
      { label: '饮料', value: 'beverage' },
      { label: '食品', value: 'food' },
      { label: '日用品', value: 'supplies' },
      { label: '其他', value: 'other' }
    ]
  },

  onLoad() {
    this.loadItems()
    this.loadCategories()
    this.loadAllItems()
  },

  onShow() {
    this.loadItems()
    this.loadAllItems()
  },

  async loadAllItems() {
    try {
      const res = await app.request({ url: '/inventory/list' })
      let itemsData = res.data
      if (res.data && res.data.data) {
        itemsData = res.data.data
      } else if (Array.isArray(res.data)) {
        itemsData = res.data
      }
      
      const allItems = (itemsData || []).map(item => ({
        ...item,
        categoryDisplay: item.category ? (categoryMap[item.category] || item.category) : ''
      }))
      
      const allItemsForPicker = allItems.map(item => ({
        label: `${item.name}(库存:${item.quantity || 0})`,
        value: item.id,
        item: item
      }))
      
      this.setData({
        allItems: allItems,
        allItemsForPicker: allItemsForPicker
      })
    } catch (err) {
      console.error('加载所有物品失败', err)
    }
  },

  async loadItems() {
    this.setData({ loading: true })
    try {
      // 如果是低库存筛选，使用低库存接口
      if (this.data.showLowStock) {
        const res = await app.request({ url: '/inventory/low-stock' })
        // 处理返回数据：Result<List<InventoryItem>>
        let itemsData = res.data
        if (res.data && res.data.data) {
          itemsData = res.data.data
        } else if (Array.isArray(res.data)) {
          itemsData = res.data
        }
        const items = (itemsData || []).map(item => this.processItem(item))
        this.setData({
          items: items,
          hasMore: false
        })
        return
      }

      // 如果没有搜索关键词和分类筛选，使用list接口获取所有物品
      if (!this.data.keyword && !this.data.category) {
        const res = await app.request({ url: '/inventory/list' })
        // 处理返回数据：Result<List<InventoryItem>>
        let itemsData = res.data
        if (res.data && res.data.data) {
          itemsData = res.data.data
        } else if (Array.isArray(res.data)) {
          itemsData = res.data
        }
        const items = (itemsData || []).map(item => this.processItem(item))
        this.setData({
          items: items,
          hasMore: false
        })
        return
      }

      // 有搜索或筛选条件时，使用分页接口
      const res = await app.request({
        url: '/inventory/page',
        method: 'GET',
        data: {
          page: this.data.page,
          size: this.data.size,
          keyword: this.data.keyword || undefined,
          category: this.data.category || undefined
        }
      })
      
      // 处理分页数据结构：Result<PageResult<InventoryItem>>
      // app.request返回的是Result对象，所以res.data是Result对象，res.data.data是PageResult对象
      let pageData = res.data
      if (res.data && res.data.data) {
        pageData = res.data.data
      } else if (res.data && res.data.records) {
        // 如果res.data直接是PageResult对象
        pageData = res.data
      }
      
      const newItems = ((pageData && pageData.records) || pageData || []).map(item => this.processItem(item))
      const totalItems = pageData && pageData.total ? pageData.total : newItems.length
      const currentTotal = this.data.page === 1 ? newItems.length : this.data.items.length + newItems.length
      
      this.setData({
        items: this.data.page === 1 ? newItems : [...this.data.items, ...newItems],
        hasMore: newItems.length >= this.data.size && currentTotal < totalItems
      })
    } catch (err) {
      console.error('加载库存失败', err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  processItem(item) {
    const quantity = item.quantity || 0
    const minStock = item.minStock || 0
    let stockStatus = { text: '正常', color: '#48bb78' }
    
    if (quantity <= 0) {
      stockStatus = { text: '缺货', color: '#f56565' }
    } else if (minStock > 0 && quantity <= minStock) {
      stockStatus = { text: '低库存', color: '#ed8936' }
    }
    
    // 转换分类为中文
    const categoryDisplay = item.category ? (categoryMap[item.category] || item.category) : ''
    
    return {
      ...item,
      stockStatus: stockStatus,
      categoryDisplay: categoryDisplay
    }
  },

  async loadCategories() {
    try {
      const res = await app.request({ url: '/inventory/list' })
      // 处理返回数据：Result<List<InventoryItem>>
      let itemsData = res.data
      if (res.data && res.data.data) {
        itemsData = res.data.data
      } else if (Array.isArray(res.data)) {
        itemsData = res.data
      }
      
      if (itemsData && Array.isArray(itemsData)) {
        const categories = [...new Set(itemsData.map(item => item.category).filter(Boolean))]
        const categoryOptions = ['全部', ...categories]
        // 生成中文显示选项
        const categoryOptionsDisplay = ['全部', ...categories.map(cat => categoryMap[cat] || cat)]
        // 如果当前选择的分类在新的选项中，保持选择；否则重置为"全部"
        let categoryIndex = 0
        if (this.data.category) {
          const index = categoryOptions.indexOf(this.data.category)
          categoryIndex = index >= 0 ? index : 0
        }
        this.setData({ 
          categories,
          categoryOptions: categoryOptions,
          categoryOptionsDisplay: categoryOptionsDisplay,
          categoryIndex: categoryIndex
        })
      }
    } catch (err) {
      console.error('加载分类失败', err)
    }
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    this.setData({ page: 1 })
    this.loadItems()
  },

  onCategoryChange(e) {
    const index = parseInt(e.detail.value)
    const selectedCategory = this.data.categoryOptions[index] || '全部'
    this.setData({ 
      category: selectedCategory === '全部' ? '' : selectedCategory,
      categoryIndex: index,
      page: 1
    })
    this.loadItems()
  },

  toggleLowStock() {
    this.setData({ 
      showLowStock: !this.data.showLowStock,
      page: 1
    })
    this.loadItems()
  },

  // 编辑物品
  handleEdit(e) {
    const itemId = e.currentTarget.dataset.id
    const item = this.data.items.find(i => i.id === itemId)
    if (!item) return

    // 找到类别在选项中的索引
    let categoryIndex = 0
    for (let i = 0; i < this.data.categoryOptionsForAdd.length; i++) {
      if (this.data.categoryOptionsForAdd[i].value === item.category) {
        categoryIndex = i
        break
      }
    }

    // 填充编辑表单
    this.setData({
      showEditModal: true,
      editForm: {
        id: item.id,
        name: item.name || '',
        category: item.category || '',
        categoryIndex: categoryIndex,
        unit: item.unit || '',
        price: item.price ? item.price.toString() : '',
        minStock: item.minStock ? item.minStock.toString() : '',
        description: item.description || ''
      }
    })
  },

  // 隐藏编辑对话框
  hideEditDialog() {
    this.setData({ showEditModal: false })
  },

  // 删除物品
  async handleDelete(e) {
    const itemId = e.currentTarget.dataset.id
    const item = this.data.items.find(i => i.id === itemId)
    if (!item) return

    wx.showModal({
      title: '确认删除',
      content: `确定要删除物品"${item.name}"吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await app.request({
              url: `/inventory/${itemId}`,
              method: 'DELETE'
            })
            wx.showToast({ title: '删除成功', icon: 'success' })
            this.loadItems()
            this.loadAllItems()
          } catch (err) {
            wx.showToast({ title: err.message || '删除失败', icon: 'none' })
          }
        }
      }
    })
  },

  // 显示入库模态框
  showStockInModal() {
    this.setData({
      showStockInModal: true,
      stockInForm: {
        itemIndex: -1,
        itemId: null,
        quantity: 1,
        remark: ''
      }
    })
  },

  // 隐藏入库模态框
  hideStockInModal() {
    this.setData({ showStockInModal: false })
  },

  // 显示出库模态框
  showStockOutModal() {
    this.setData({
      showStockOutModal: true,
      stockOutForm: {
        itemIndex: -1,
        itemId: null,
        quantity: 1,
        remark: ''
      }
    })
  },

  // 隐藏出库模态框
  hideStockOutModal() {
    this.setData({ showStockOutModal: false })
  },

  // 入库物品选择
  onStockInItemChange(e) {
    const index = parseInt(e.detail.value)
    const selectedItem = this.data.allItemsForPicker[index]
    this.setData({
      'stockInForm.itemIndex': index,
      'stockInForm.itemId': selectedItem ? selectedItem.value : null
    })
  },

  // 出库物品选择
  onStockOutItemChange(e) {
    const index = parseInt(e.detail.value)
    const selectedItem = this.data.allItemsForPicker[index]
    this.setData({
      'stockOutForm.itemIndex': index,
      'stockOutForm.itemId': selectedItem ? selectedItem.value : null
    })
  },

  // 数量输入
  onQuantityInput(e) {
    const type = e.currentTarget.dataset.type
    const value = parseInt(e.detail.value) || 1
    const quantity = value > 0 ? value : 1
    if (type === 'in') {
      this.setData({ 'stockInForm.quantity': quantity })
    } else {
      this.setData({ 'stockOutForm.quantity': quantity })
    }
  },

  // 增加数量
  increaseQuantity(e) {
    const type = e.currentTarget.dataset.type
    if (type === 'in') {
      const quantity = (this.data.stockInForm.quantity || 1) + 1
      this.setData({ 'stockInForm.quantity': quantity })
    } else {
      const quantity = (this.data.stockOutForm.quantity || 1) + 1
      this.setData({ 'stockOutForm.quantity': quantity })
    }
  },

  // 减少数量
  decreaseQuantity(e) {
    const type = e.currentTarget.dataset.type
    if (type === 'in') {
      const quantity = Math.max(1, (this.data.stockInForm.quantity || 1) - 1)
      this.setData({ 'stockInForm.quantity': quantity })
    } else {
      const quantity = Math.max(1, (this.data.stockOutForm.quantity || 1) - 1)
      this.setData({ 'stockOutForm.quantity': quantity })
    }
  },

  // 入库备注输入
  onStockInRemarkInput(e) {
    this.setData({ 'stockInForm.remark': e.detail.value })
  },

  // 出库备注输入
  onStockOutRemarkInput(e) {
    this.setData({ 'stockOutForm.remark': e.detail.value })
  },

  // 提交入库
  async handleStockInSubmit() {
    const { itemId, quantity, remark } = this.data.stockInForm
    
    if (!itemId) {
      wx.showToast({ title: '请选择物品', icon: 'none' })
      return
    }

    if (!quantity || quantity <= 0) {
      wx.showToast({ title: '请输入有效的数量', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      await app.request({
        url: '/inventory/stock-in',
        method: 'POST',
        data: {
          itemId: itemId,
          quantity: quantity,
          remark: remark || '小程序入库'
        }
      })
      wx.showToast({ title: '入库成功', icon: 'success' })
      this.hideStockInModal()
      this.loadItems()
      this.loadAllItems()
    } catch (err) {
      wx.showToast({ title: err.message || '入库失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  },

  // 提交出库
  async handleStockOutSubmit() {
    const { itemId, quantity, remark } = this.data.stockOutForm
    
    if (!itemId) {
      wx.showToast({ title: '请选择物品', icon: 'none' })
      return
    }

    if (!quantity || quantity <= 0) {
      wx.showToast({ title: '请输入有效的数量', icon: 'none' })
      return
    }

    // 检查库存
    const selectedItem = this.data.allItems.find(item => item.id === itemId)
    if (!selectedItem || (selectedItem.quantity || 0) < quantity) {
      wx.showToast({ title: '库存不足', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      await app.request({
        url: '/inventory/stock-out',
        method: 'POST',
        data: {
          itemId: itemId,
          quantity: quantity,
          remark: remark || '小程序出库'
        }
      })
      wx.showToast({ title: '出库成功', icon: 'success' })
      this.hideStockOutModal()
      this.loadItems()
      this.loadAllItems()
    } catch (err) {
      wx.showToast({ title: err.message || '出库失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading && !this.data.showLowStock) {
      this.setData({ page: this.data.page + 1 })
      this.loadItems()
    }
  },

  showAddDialog() {
    this.setData({
      showAddModal: true,
      addForm: {
        name: '',
        category: '',
        categoryIndex: 0,
        unit: '',
        price: '',
        minStock: '',
        description: ''
      }
    })
  },

  hideAddDialog() {
    this.setData({ showAddModal: false })
  },

  stopPropagation() {
    // 阻止事件冒泡
  },

  onAddFormInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    this.setData({
      [`addForm.${field}`]: value
    })
  },

  onCategoryPickerChange(e) {
    const index = parseInt(e.detail.value)
    const selectedCategory = this.data.categoryOptionsForAdd[index]
    this.setData({
      'addForm.categoryIndex': index,
      'addForm.category': selectedCategory.value
    })
  },

  // 编辑表单类别选择
  onEditCategoryPickerChange(e) {
    const index = parseInt(e.detail.value)
    const selectedCategory = this.data.categoryOptionsForAdd[index]
    this.setData({
      'editForm.categoryIndex': index,
      'editForm.category': selectedCategory.value
    })
  },

  // 编辑表单输入
  onEditFormInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    this.setData({
      [`editForm.${field}`]: value
    })
  },

  // 编辑价格调整
  increaseEditPrice() {
    const price = parseFloat(this.data.editForm.price || 0) + 0.01
    this.setData({ 'editForm.price': price.toFixed(2) })
  },

  decreaseEditPrice() {
    const price = Math.max(0, parseFloat(this.data.editForm.price || 0) - 0.01)
    this.setData({ 'editForm.price': price.toFixed(2) })
  },

  // 编辑预警值调整
  increaseEditMinStock() {
    const minStock = parseInt(this.data.editForm.minStock || 0) + 1
    this.setData({ 'editForm.minStock': minStock.toString() })
  },

  decreaseEditMinStock() {
    const minStock = Math.max(0, parseInt(this.data.editForm.minStock || 0) - 1)
    this.setData({ 'editForm.minStock': minStock.toString() })
  },

  // 价格调整
  increasePrice() {
    const price = parseFloat(this.data.addForm.price || 0) + 0.01
    this.setData({ 'addForm.price': price.toFixed(2) })
  },

  decreasePrice() {
    const price = Math.max(0, parseFloat(this.data.addForm.price || 0) - 0.01)
    this.setData({ 'addForm.price': price.toFixed(2) })
  },

  // 预警值调整
  increaseMinStock() {
    const minStock = parseInt(this.data.addForm.minStock || 0) + 1
    this.setData({ 'addForm.minStock': minStock.toString() })
  },

  decreaseMinStock() {
    const minStock = Math.max(0, parseInt(this.data.addForm.minStock || 0) - 1)
    this.setData({ 'addForm.minStock': minStock.toString() })
  },

  async handleAddItem() {
    const form = this.data.addForm

    // 验证必填项
    if (!form.name || !form.name.trim()) {
      wx.showToast({ title: '请输入物品名称', icon: 'none' })
      return
    }

    if (!form.category) {
      wx.showToast({ title: '请选择类别', icon: 'none' })
      return
    }

    this.setData({ submitting: true })

    try {
      // 构建请求数据
      const itemData = {
        name: form.name.trim(),
        category: form.category,
        unit: form.unit ? form.unit.trim() : '',
        price: form.price ? parseFloat(form.price) : null,
        minStock: form.minStock ? parseInt(form.minStock) : null,
        description: form.description ? form.description.trim() : '',
        quantity: 0 // 新物品初始库存为0
      }

      // 如果价格或最低库存为空，不传这些字段
      if (!itemData.price) {
        delete itemData.price
      }
      if (!itemData.minStock) {
        delete itemData.minStock
      }

      await app.request({
        url: '/inventory',
        method: 'POST',
        data: itemData
      })

      wx.showToast({ title: '添加成功', icon: 'success' })
      this.hideAddDialog()
      this.loadItems()
      this.loadCategories() // 重新加载分类列表
      this.loadAllItems() // 重新加载所有物品
    } catch (err) {
      console.error('添加物品失败', err)
      wx.showToast({ title: err.message || '添加失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  },

  // 提交编辑
  async handleEditItem() {
    const form = this.data.editForm

    // 验证必填项
    if (!form.name || !form.name.trim()) {
      wx.showToast({ title: '请输入物品名称', icon: 'none' })
      return
    }

    if (!form.category) {
      wx.showToast({ title: '请选择类别', icon: 'none' })
      return
    }

    this.setData({ submitting: true })

    try {
      // 构建请求数据
      const itemData = {
        id: form.id,
        name: form.name.trim(),
        category: form.category,
        unit: form.unit ? form.unit.trim() : '',
        price: form.price ? parseFloat(form.price) : null,
        minStock: form.minStock ? parseInt(form.minStock) : null,
        description: form.description ? form.description.trim() : ''
      }

      // 如果价格或最低库存为空，不传这些字段
      if (!itemData.price) {
        delete itemData.price
      }
      if (!itemData.minStock) {
        delete itemData.minStock
      }

      await app.request({
        url: '/inventory',
        method: 'PUT',
        data: itemData
      })

      wx.showToast({ title: '更新成功', icon: 'success' })
      this.hideEditDialog()
      this.loadItems()
      this.loadCategories() // 重新加载分类列表
      this.loadAllItems() // 重新加载所有物品
    } catch (err) {
      console.error('更新物品失败', err)
      wx.showToast({ title: err.message || '更新失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  }
})

