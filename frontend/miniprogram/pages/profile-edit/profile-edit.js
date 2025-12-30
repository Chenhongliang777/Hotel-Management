const app = getApp()

Page({
  data: {
    userInfo: {},
    form: {
      nickname: '',
      realName: '',
      phone: '',
      email: '',
      idCard: '',
      gender: 0
    },
    genderOptions: ['未知', '男', '女'],
    genderIndex: 0,
    loading: false
  },

  onLoad() {
    this.loadUserInfo()
  },

  async loadUserInfo() {
    try {
      const res = await app.request({ url: '/guest/current' })
      const userInfo = res.data || {}
      this.setData({
        userInfo,
        'form.nickname': userInfo.nickname || '',
        'form.realName': userInfo.realName || '',
        'form.phone': userInfo.phone || '',
        'form.email': userInfo.email || '',
        'form.idCard': userInfo.idCard || '',
        'form.gender': userInfo.gender || 0,
        genderIndex: userInfo.gender || 0
      })
    } catch (err) {
      console.error(err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  onGenderChange(e) {
    const index = parseInt(e.detail.value)
    this.setData({
      genderIndex: index,
      'form.gender': index
    })
  },

  async save() {
    const { form } = this.data
    
    if (!form.phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    if (!/^1[3-9]\d{9}$/.test(form.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      wx.showToast({ title: '请输入正确的邮箱', icon: 'none' })
      return
    }

    this.setData({ loading: true })
    try {
      await app.request({
        url: '/guest',
        method: 'PUT',
        data: {
          nickname: form.nickname,
          realName: form.realName,
          phone: form.phone,
          email: form.email,
          idCard: form.idCard,
          gender: form.gender
        }
      })
      
      // 更新本地用户信息
      const updatedInfo = {
        ...app.globalData.userInfo,
        ...form
      }
      app.login(app.globalData.token, updatedInfo)
      
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (err) {
      console.error(err)
    } finally {
      this.setData({ loading: false })
    }
  }
})

