<template>
  <div class="home-page">
    <!-- 头部导航 -->
    <el-header class="header">
      <div class="guest-container">
        <div class="header-content">
          <div class="logo">
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
            <template v-if="userStore.isLoggedIn">
              <span class="welcome-text">欢迎，{{ userStore.realName || userStore.username }}</span>
              <el-button type="text" @click="handleLogout">退出</el-button>
            </template>
            <template v-else>
              <el-button type="text" @click="$router.push('/login')">登录</el-button>
              <el-button type="primary" @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </div>
    </el-header>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="guest-container">
        <div class="page-header">
          <h1>欢迎来到悦居民宿</h1>
          <p>为您提供舒适的住宿体验</p>
        </div>
        <el-card class="search-card">
          <el-form :model="searchForm" inline>
            <el-form-item label="入住日期">
              <el-date-picker
                v-model="searchForm.checkInDate"
                type="date"
                placeholder="选择入住日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledCheckInDate"
              />
            </el-form-item>
            <el-form-item label="退房日期">
              <el-date-picker
                v-model="searchForm.checkOutDate"
                type="date"
                placeholder="选择退房日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledCheckOutDate"
              />
            </el-form-item>
            <el-form-item label="入住人数">
              <el-input-number
                v-model="searchForm.guestCount"
                :min="1"
                :max="10"
                placeholder="人数"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索房源
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>

    <!-- 推荐房源 -->
    <div class="rooms-section">
      <div class="guest-container">
        <h2 class="section-title">推荐房源</h2>
        <div v-loading="loading" class="rooms-grid">
          <div
            v-for="roomType in roomTypes"
            :key="roomType.id"
            class="room-card"
            @click="goToRoomDetail(roomType.id)"
          >
              <img
                :src="roomType.image"
                :alt="roomType.name"
                class="room-image"
                @error="handleImageError"
              />
            <div class="room-info">
              <div class="room-name">{{ roomType.name }}</div>
              <div class="room-desc">{{ roomType.description || '温馨舒适的住宿环境' }}</div>
              <div class="room-features">
                <span class="feature-tag" v-for="feature in roomType.features?.split(',')" :key="feature">
                  {{ feature }}
                </span>
              </div>
              <div class="room-price">
                <div class="price">
                  ¥{{ roomType.price }}
                  <span class="unit">/晚</span>
                </div>
                <el-button type="primary" @click.stop="goToBooking(roomType.id)">立即预订</el-button>
              </div>
            </div>
          </div>
        </div>
        <div v-if="!loading && roomTypes.length === 0" class="empty-state">
          <div class="empty-icon">🏠</div>
          <div class="empty-text">暂无可用房源</div>
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
import { getAvailableRoomTypes, searchRoomTypes } from '@/api/room'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const loading = ref(false)
const roomTypes = ref([])
const searchForm = ref({
  checkInDate: '',
  checkOutDate: '',
  guestCount: 1
})

const disabledCheckInDate = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

const disabledCheckOutDate = (date) => {
  if (!searchForm.value.checkInDate) {
    return date < new Date(new Date().setHours(0, 0, 0, 0))
  }
  return date <= new Date(searchForm.value.checkInDate)
}

const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/400x200?text=Room+Image'
}

const loadRoomTypes = async () => {
  loading.value = true
  try {
    const res = await getAvailableRoomTypes()
    // 处理图片和价格数据
    roomTypes.value = (res.data || []).map(roomType => {
      // 解析图片列表
      let images = []
      if (roomType.images) {
        try {
          images = typeof roomType.images === 'string'
            ? JSON.parse(roomType.images)
            : roomType.images
        } catch (e) {
          images = []
        }
      }
      // 取第一张图片，如果没有则使用占位图
      roomType.image = images && images.length > 0
        ? images[0]
        : 'https://via.placeholder.com/400x200?text=Room+Image'

      // 使用basePrice作为价格
      roomType.price = roomType.basePrice || 0

      // 解析设施列表
      if (roomType.facilities) {
        try {
          const facilities = typeof roomType.facilities === 'string'
            ? JSON.parse(roomType.facilities)
            : roomType.facilities
          roomType.features = Array.isArray(facilities) ? facilities.join(',') : roomType.facilities
        } catch (e) {
          roomType.features = roomType.facilities
        }
      }

      return roomType
    })
  } catch (error) {
    console.error('加载房源失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (!searchForm.value.checkInDate || !searchForm.value.checkOutDate) {
    ElMessage.warning('请选择入住和退房日期')
    return
  }
  if (new Date(searchForm.value.checkOutDate) <= new Date(searchForm.value.checkInDate)) {
    ElMessage.warning('退房日期必须晚于入住日期')
    return
  }
  router.push({
    path: '/rooms',
    query: {
      checkInDate: searchForm.value.checkInDate,
      checkOutDate: searchForm.value.checkOutDate,
      guestCount: searchForm.value.guestCount
    }
  })
}

const goToRoomDetail = (id) => {
  router.push(`/room/${id}`)
}

const goToBooking = (roomTypeId) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/booking/${roomTypeId}`)
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadRoomTypes()
})
</script>

<style scoped lang="scss">
.home-page {
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

.search-section {
  background: #f5f7fa;
  padding: 40px 0;

  .search-card {
    margin-top: -20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.rooms-section {
  flex: 1;
  padding: 40px 0;

  .section-title {
    font-size: 28px;
    margin-bottom: 30px;
    color: #333;
  }

  .rooms-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 30px;
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

