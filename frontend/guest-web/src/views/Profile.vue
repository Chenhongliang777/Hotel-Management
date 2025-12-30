<template>
  <div class="profile-page">
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

    <div class="profile-content">
      <div class="guest-container">
        <h2 class="page-title">个人中心</h2>

        <el-card class="profile-card">
          <el-tabs v-model="activeTab">
            <!-- 个人信息 -->
            <el-tab-pane label="个人信息" name="info">
              <div class="profile-info">
                <el-form
                  ref="profileFormRef"
                  :model="profileForm"
                  :rules="profileRules"
                  label-width="120px"
                  style="max-width: 600px"
                >
                  <el-form-item label="用户名">
                    <el-input v-model="profileForm.username" disabled />
                  </el-form-item>
                  <el-form-item label="真实姓名" prop="realName">
                    <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
                  </el-form-item>
                  <el-form-item label="手机号" prop="phone">
                    <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                  </el-form-item>
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                  </el-form-item>
                  <el-form-item label="身份证号" prop="idCard">
                    <el-input v-model="profileForm.idCard" placeholder="请输入身份证号" />
                  </el-form-item>
                  <el-form-item label="地址">
                    <el-input
                      v-model="profileForm.address"
                      type="textarea"
                      :rows="3"
                      placeholder="请输入地址"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" :loading="saving" @click="handleSave">
                      保存修改
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>

            <!-- 修改密码 -->
            <el-tab-pane label="修改密码" name="password">
              <div class="password-form">
                <el-form
                  ref="passwordFormRef"
                  :model="passwordForm"
                  :rules="passwordRules"
                  label-width="120px"
                  style="max-width: 600px"
                >
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input
                      v-model="passwordForm.oldPassword"
                      type="password"
                      placeholder="请输入原密码"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input
                      v-model="passwordForm.newPassword"
                      type="password"
                      placeholder="请输入新密码（至少6位）"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input
                      v-model="passwordForm.confirmPassword"
                      type="password"
                      placeholder="请再次输入新密码"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" :loading="changingPassword" @click="handleChangePassword">
                      修改密码
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
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
import { getCurrentGuest, updateGuest } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const activeTab = ref('info')
const saving = ref(false)
const changingPassword = ref(false)
const profileFormRef = ref(null)
const passwordFormRef = ref(null)

const profileForm = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  idCard: '',
  address: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  idCard: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const loadProfile = async () => {
  try {
    const res = await getCurrentGuest()
    const userInfo = res.data
    if (userInfo) {
      profileForm.value = {
        username: userInfo.username || '',
        realName: userInfo.realName || '',
        phone: userInfo.phone || '',
        email: userInfo.email || '',
        idCard: userInfo.idCard || '',
        address: userInfo.address || ''
      }
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
    // 错误消息已在 request.js 拦截器中显示，这里不再重复显示
    // 如果是 401 错误，拦截器会自动跳转到登录页
  }
}

const handleSave = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      await updateGuest(profileForm.value)
      await userStore.fetchUserInfo()
      ElMessage.success('保存成功')
    } catch (error) {
      console.error('保存失败:', error)
    } finally {
      saving.value = false
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changingPassword.value = true
    try {
      // 注意：这里需要后端提供修改密码的接口
      // 暂时提示用户联系管理员
      ElMessage.warning('修改密码功能暂未开放，请联系管理员')
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    } catch (error) {
      console.error('修改密码失败:', error)
    } finally {
      changingPassword.value = false
    }
  })
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadProfile()
})
</script>


<style scoped lang="scss">
.profile-page {
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

.profile-content {
  flex: 1;
  padding: 40px 0;
  background: #f5f7fa;

  .page-title {
    font-size: 28px;
    margin-bottom: 30px;
    color: #333;
  }

  .profile-card {
    .profile-info,
    .password-form {
      padding: 20px 0;
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

