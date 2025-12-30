<template>
  <div class="room-detail-page">
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

    <div class="room-detail-content" v-loading="loading">
      <div class="guest-container">
        <div v-if="roomType" class="detail-wrapper">
          <!-- 图片区域 -->
          <div class="image-section">
            <img
              :src="roomType.image"
              :alt="roomType.name"
              class="main-image"
              @error="handleImageError"
            />
          </div>

          <!-- 基本信息 -->
          <div class="info-section">
            <h1 class="room-name">{{ roomType.name }}</h1>
            <div class="room-price-large">
              <span class="price">¥{{ roomType.price }}</span>
              <span class="unit">/晚</span>
            </div>
            <div class="room-desc">{{ roomType.description || '温馨舒适的住宿环境' }}</div>

            <!-- 设施信息 -->
            <div class="features-section">
              <h3>设施服务</h3>
              <div class="features-list">
                <span
                  v-for="feature in roomType.features?.split(',')"
                  :key="feature"
                  class="feature-item"
                >
                  <el-icon><Check /></el-icon>
                  {{ feature }}
                </span>
              </div>
            </div>

            <!-- 房间信息 -->
            <div class="room-info-section">
              <h3>房间信息</h3>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="房间面积">
                  {{ roomType.area || '--' }} ㎡
                </el-descriptions-item>
                <el-descriptions-item label="最大入住人数">
                  {{ roomType.maxOccupancy || '--' }} 人
                </el-descriptions-item>
                <el-descriptions-item label="床型">
                  {{ roomType.bedType || '--' }}
                </el-descriptions-item>
                <el-descriptions-item label="房间数量">
                  {{ roomType.roomCount || '--' }} 间
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- 预订按钮 -->
            <div class="booking-section">
              <el-button
                type="primary"
                size="large"
                @click="goToBooking"
                style="width: 100%"
              >
                立即预订
              </el-button>
            </div>
          </div>
        </div>

        <div v-else-if="!loading" class="empty-state">
          <div class="empty-icon">🏠</div>
          <div class="empty-text">房源不存在</div>
          <el-button type="primary" @click="$router.push('/rooms')">返回房源列表</el-button>
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
import { getRoomType } from '@/api/room'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const loading = ref(false)
const roomType = ref(null)

const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/800x400?text=Room+Image'
}

const loadRoomType = async () => {
  loading.value = true
  try {
    const res = await getRoomType(route.params.id)
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
      : 'https://via.placeholder.com/800x400?text=Room+Image'

    // 使用basePrice作为价格
    data.price = data.basePrice || 0

    // 解析设施列表
    if (data.facilities) {
      try {
        const facilities = typeof data.facilities === 'string'
          ? JSON.parse(data.facilities)
          : data.facilities
        data.features = Array.isArray(facilities) ? facilities.join(',') : data.facilities
      } catch (e) {
        data.features = data.facilities
      }
    }

    // 设置其他字段
    data.maxOccupancy = data.maxGuests || 2
    data.roomCount = 1 // 可以根据实际需求设置

    roomType.value = data
  } catch (error) {
    console.error('加载房源详情失败:', error)
    ElMessage.error('加载房源详情失败')
  } finally {
    loading.value = false
  }
}

const goToBooking = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/booking/${route.params.id}`)
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadRoomType()
})
</script>

<style scoped lang="scss">
.room-detail-page {
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

.room-detail-content {
  flex: 1;
  padding: 40px 0;

  .detail-wrapper {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 40px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .image-section {
    .main-image {
      width: 100%;
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }

  .info-section {
    .room-name {
      font-size: 32px;
      margin: 0 0 20px 0;
      color: #333;
    }

    .room-price-large {
      margin-bottom: 20px;

      .price {
        font-size: 36px;
        font-weight: 600;
        color: #667eea;
      }

      .unit {
        font-size: 18px;
        color: #999;
        margin-left: 5px;
      }
    }

    .room-desc {
      font-size: 16px;
      color: #666;
      line-height: 1.8;
      margin-bottom: 30px;
    }

    .features-section {
      margin-bottom: 30px;

      h3 {
        font-size: 20px;
        margin-bottom: 15px;
        color: #333;
      }

      .features-list {
        display: flex;
        flex-wrap: wrap;
        gap: 15px;

        .feature-item {
          display: flex;
          align-items: center;
          gap: 5px;
          padding: 8px 16px;
          background: #f0f0f0;
          border-radius: 20px;
          font-size: 14px;
          color: #666;
        }
      }
    }

    .room-info-section {
      margin-bottom: 30px;

      h3 {
        font-size: 20px;
        margin-bottom: 15px;
        color: #333;
      }
    }

    .booking-section {
      margin-top: 40px;
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

