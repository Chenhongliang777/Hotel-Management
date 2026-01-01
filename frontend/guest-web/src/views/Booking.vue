<template>
  <div class="booking-page">
    <!-- 头部导航 -->
    <el-header class="header">
      <div class="guest-container">
        <div class="header-content">
          <div class="logo" @click="$router.push('/')">
            <h2>悦居民宿</h2>
          </div>
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            router
            class="header-menu"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/rooms">房源查询</el-menu-item>
            <el-menu-item index="/orders" v-if="userStore.isLoggedIn">我的订单</el-menu-item>
            <el-menu-item index="/profile" v-if="userStore.isLoggedIn">个人中心</el-menu-item>
          </el-menu>
          <div class="header-actions">
            <span class="welcome-text">欢迎，{{ userStore.realName || userStore.username }}</span>
            <el-button type="text" @click="handleLogout">退出</el-button>
          </div>
        </div>
      </div>
    </el-header>

    <div class="booking-content" v-loading="loading">
      <div class="guest-container">
        <el-steps :active="currentStep" align-center style="margin-bottom: 40px">
          <el-step title="选择日期" />
          <el-step title="填写信息" />
          <el-step title="确认订单" />
        </el-steps>

        <div class="booking-wrapper">
          <!-- 左侧：房源信息 -->
          <div class="room-info-card">
            <el-card>
              <template #header>
                <h3>预订信息</h3>
              </template>
              <div v-if="roomType">
                  <img
                    :src="roomType.image"
                    :alt="roomType.name"
                    class="room-image"
                    @error="handleImageError"
                  />
                <h4 class="room-name">{{ roomType.name }}</h4>
                <div class="room-desc">{{ roomType.description || '温馨舒适的住宿环境' }}</div>
                <div class="price-info">
                  <span class="price">¥{{ roomType.price }}</span>
                  <span class="unit">/晚</span>
                </div>
                <div class="deposit-info" v-if="nights > 0">
                  <div class="info-row">
                    <span>入住天数：</span>
                    <span>{{ nights }} 晚</span>
                  </div>
                  <div class="info-row">
                    <span>房费：</span>
                    <span class="highlight">¥{{ totalPrice.toFixed(2) }}</span>
                  </div>
                  <div class="info-row">
                    <span>保证金（{{ (depositRate * 100).toFixed(0) }}%）：</span>
                    <span class="highlight-red">¥{{ deposit.toFixed(2) }}</span>
                  </div>
                  <div class="info-row total">
                    <span>应付总计：</span>
                    <span class="highlight-total">¥{{ (totalPrice + deposit).toFixed(2) }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 右侧：预订表单 -->
          <div class="booking-form-card">
            <el-card>
              <template #header>
                <h3>填写预订信息</h3>
              </template>

              <!-- 步骤1：选择日期 -->
              <div v-if="currentStep === 0" class="step-content">
                <el-form :model="bookingForm" label-width="120px">
                  <el-form-item label="入住日期" required>
                    <el-date-picker
                      v-model="bookingForm.checkInDate"
                      type="date"
                      placeholder="选择入住日期"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      :disabled-date="disabledCheckInDate"
                      style="width: 100%"
                    />
                  </el-form-item>
                  <el-form-item label="退房日期" required>
                    <el-date-picker
                      v-model="bookingForm.checkOutDate"
                      type="date"
                      placeholder="选择退房日期"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      :disabled-date="disabledCheckOutDate"
                      style="width: 100%"
                    />
                  </el-form-item>
                  <el-form-item label="入住人数" required>
                    <el-input-number
                      v-model="bookingForm.guestCount"
                      :min="1"
                      :max="roomType?.maxOccupancy || 10"
                      style="width: 100%"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="nextStep" style="width: 100%">
                      下一步
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>

              <!-- 步骤2：填写信息 -->
              <div v-if="currentStep === 1" class="step-content">
                <el-form
                  ref="bookingFormRef"
                  :model="bookingForm"
                  :rules="bookingRules"
                  label-width="120px"
                >
                  <el-form-item label="联系人姓名" prop="guestName" required>
                    <el-input v-model="bookingForm.guestName" placeholder="请输入联系人姓名" />
                  </el-form-item>
                  <el-form-item label="联系电话" prop="guestPhone" required>
                    <el-input v-model="bookingForm.guestPhone" placeholder="请输入联系电话" />
                  </el-form-item>
                  <el-form-item label="身份证号" prop="guestIdCard" required>
                    <el-input 
                      v-model="bookingForm.guestIdCard" 
                      placeholder="请输入身份证号"
                      maxlength="18"
                    />
                  </el-form-item>
                  <el-form-item label="备注">
                    <el-input
                      v-model="bookingForm.remark"
                      type="textarea"
                      :rows="4"
                      placeholder="如有特殊需求，请在此填写"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button @click="prevStep">上一步</el-button>
                    <el-button type="primary" @click="nextStep" style="margin-left: 10px">
                      下一步
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>

              <!-- 步骤3：确认订单 -->
              <div v-if="currentStep === 2" class="step-content">
                <el-descriptions :column="1" border>
                  <el-descriptions-item label="房型">{{ roomType?.name }}</el-descriptions-item>
                  <el-descriptions-item label="入住日期">{{ bookingForm.checkInDate }}</el-descriptions-item>
                  <el-descriptions-item label="退房日期">{{ bookingForm.checkOutDate }}</el-descriptions-item>
                  <el-descriptions-item label="入住人数">{{ bookingForm.guestCount }} 人</el-descriptions-item>
                  <el-descriptions-item label="联系人">{{ bookingForm.guestName }}</el-descriptions-item>
                  <el-descriptions-item label="联系电话">{{ bookingForm.guestPhone }}</el-descriptions-item>
                  <el-descriptions-item label="身份证号">{{ bookingForm.guestIdCard }}</el-descriptions-item>
                  <el-descriptions-item label="备注" v-if="bookingForm.remark">
                    {{ bookingForm.remark }}
                  </el-descriptions-item>
                </el-descriptions>

                <div class="price-summary">
                  <div class="summary-item">
                    <span>单价：</span>
                    <span>¥{{ roomType?.price }}/晚</span>
                  </div>
                  <div class="summary-item">
                    <span>入住天数：</span>
                    <span>{{ nights }} 晚</span>
                  </div>
                  <div class="summary-item">
                    <span>总价：</span>
                    <span class="total-price">¥{{ totalPrice }}</span>
                  </div>
                  <div class="summary-item">
                    <span>保证金：</span>
                    <span class="deposit">¥{{ deposit }}</span>
                  </div>
                </div>

                <el-form-item style="margin-top: 30px">
                  <el-button @click="prevStep">上一步</el-button>
                  <el-button
                    type="primary"
                    :loading="submitting"
                    @click="submitBooking"
                    style="margin-left: 10px"
                  >
                    确认预订
                  </el-button>
                </el-form-item>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="guest-container">
        <p>&copy; 2025 悦居民宿. 保留所有权利.</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getRoomType, getDepositRate } from '@/api/room'
import { createOrder } from '@/api/order'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const loading = ref(false)
const submitting = ref(false)
const currentStep = ref(0)
const roomType = ref(null)
const bookingFormRef = ref(null)
const depositRate = ref(0.3)  // 默认30%，会从配置加载

const bookingForm = ref({
  roomTypeId: Number(route.params.roomTypeId),
  checkInDate: '',
  checkOutDate: '',
  guestCount: 1,
  guestName: userStore.realName || '',
  guestPhone: userStore.phone || '',
  guestIdCard: userStore.userInfo?.idCard || '',
  remark: ''
})

const validateIdCard = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入身份证号'))
  } else if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/.test(value)) {
    callback(new Error('请输入正确的身份证号'))
  } else {
    callback()
  }
}

const bookingRules = {
  guestName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  guestPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  guestIdCard: [
    { required: true, validator: validateIdCard, trigger: 'blur' }
  ]
}

const nights = computed(() => {
  if (!bookingForm.value.checkInDate || !bookingForm.value.checkOutDate) {
    return 0
  }
  const checkIn = dayjs(bookingForm.value.checkInDate)
  const checkOut = dayjs(bookingForm.value.checkOutDate)
  return checkOut.diff(checkIn, 'day')
})

const totalPrice = computed(() => {
  if (!roomType.value || nights.value === 0) {
    return 0
  }
  return roomType.value.price * nights.value
})

const deposit = computed(() => {
  return parseFloat((totalPrice.value * depositRate.value).toFixed(2))
})

const disabledCheckInDate = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

const disabledCheckOutDate = (date) => {
  if (!bookingForm.value.checkInDate) {
    return date < new Date(new Date().setHours(0, 0, 0, 0))
  }
  return date <= new Date(bookingForm.value.checkInDate)
}

const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/400x200?text=Room+Image'
}

const loadRoomType = async () => {
  loading.value = true
  try {
    const res = await getRoomType(route.params.roomTypeId)
    const data = res.data

    // 解析图片列表
    let images = []
    if (data.images) {
      try {
        images = typeof data.images === 'string'
          ? JSON.parse(data.images)
          : data.images
      } catch (e) {
        images = []
      }
    }
    // 取第一张图片，如果没有则使用占位图
    data.image = images && images.length > 0
      ? images[0]
      : 'https://via.placeholder.com/400x200?text=Room+Image'

    // 使用basePrice作为价格
    data.price = data.basePrice || 0

    // 设置其他字段
    data.maxOccupancy = data.maxGuests || 2

    roomType.value = data
  } catch (error) {
    console.error('加载房源信息失败:', error)
    ElMessage.error('加载房源信息失败')
    router.push('/rooms')
  } finally {
    loading.value = false
  }
}

const nextStep = async () => {
  if (currentStep.value === 0) {
    if (!bookingForm.value.checkInDate || !bookingForm.value.checkOutDate) {
      ElMessage.warning('请选择入住和退房日期')
      return
    }
    if (new Date(bookingForm.value.checkOutDate) <= new Date(bookingForm.value.checkInDate)) {
      ElMessage.warning('退房日期必须晚于入住日期')
      return
    }
    currentStep.value = 1
  } else if (currentStep.value === 1) {
    if (!bookingFormRef.value) return
    await bookingFormRef.value.validate((valid) => {
      if (valid) {
        currentStep.value = 2
      }
    })
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const submitBooking = async () => {
  submitting.value = true
  try {
    const orderData = {
      ...bookingForm.value,
      totalPrice: totalPrice.value,
      deposit: deposit.value
    }
    const res = await createOrder(orderData)
    ElMessage.success('预订成功！')
    router.push(`/order/${res.data.id}`)
  } catch (error) {
    console.error('预订失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
}

const loadDepositRate = async () => {
  try {
    const res = await getDepositRate()
    if (res.data) {
      depositRate.value = parseFloat(res.data)
    }
  } catch (error) {
    console.error('加载保证金比例失败:', error)
  }
}

onMounted(() => {
  loadRoomType()
  loadDepositRate()
})
</script>

<style scoped lang="scss">
.booking-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 70px;
  line-height: 70px;

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .logo {
      cursor: pointer;
      h2 {
        margin: 0;
        color: #667eea;
        font-size: 24px;
      }
    }

    .header-menu {
      flex: 1;
      border: none;
      margin-left: 40px;
    }

    .header-actions {
      display: flex;
      align-items: center;
      gap: 15px;

      .welcome-text {
        color: #666;
        font-size: 14px;
      }
    }
  }
}

.booking-content {
  flex: 1;
  padding: 40px 0;
  background: #f5f7fa;

  .booking-wrapper {
    display: grid;
    grid-template-columns: 400px 1fr;
    gap: 30px;

    @media (max-width: 1024px) {
      grid-template-columns: 1fr;
    }
  }

  .room-info-card {
    .room-image {
      width: 100%;
      height: 200px;
      object-fit: cover;
      border-radius: 8px;
      margin-bottom: 15px;
    }

    .room-name {
      font-size: 20px;
      margin: 0 0 10px 0;
      color: #333;
    }

    .room-desc {
      color: #666;
      font-size: 14px;
      margin-bottom: 15px;
    }

    .price-info {
      .price {
        font-size: 28px;
        font-weight: 600;
        color: #667eea;
      }

      .unit {
        font-size: 14px;
        color: #999;
        margin-left: 5px;
      }
    }

    .deposit-info {
      margin-top: 20px;
      padding-top: 15px;
      border-top: 1px solid #eee;

      .info-row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;
        font-size: 14px;
        color: #666;

        .highlight {
          color: #667eea;
          font-weight: 600;
        }

        .highlight-red {
          color: #f56c6c;
          font-weight: 600;
        }

        .highlight-total {
          color: #667eea;
          font-weight: 700;
          font-size: 18px;
        }

        &.total {
          margin-top: 15px;
          padding-top: 10px;
          border-top: 1px dashed #ddd;
          font-size: 16px;
          font-weight: 600;
        }
      }
    }
  }

  .booking-form-card {
    .step-content {
      min-height: 400px;
    }

    .price-summary {
      margin-top: 30px;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;

      .summary-item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 15px;
        font-size: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .total-price {
          font-size: 24px;
          font-weight: 600;
          color: #667eea;
        }

        .deposit {
          font-size: 20px;
          font-weight: 600;
          color: #f56c6c;
        }
      }
    }
  }
}

.footer {
  background: #2c3e50;
  color: white;
  text-align: center;
  line-height: 60px;
  margin-top: 60px;
}
</style>

