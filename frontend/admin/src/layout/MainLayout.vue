<template>
  <el-container class="main-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <el-icon><HomeFilled /></el-icon>
        <span v-show="!isCollapse">悦居民宿</span>
      </div>
      <el-menu
        :default-active="currentRoute"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        
        <el-sub-menu index="room-manage">
          <template #title>
            <el-icon><House /></el-icon>
            <span>房间管理</span>
          </template>
          <el-menu-item index="/room-type">房型管理</el-menu-item>
          <el-menu-item index="/room">房间管理</el-menu-item>
          <el-menu-item index="/room-status">房态管理</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="order-manage">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order">订单列表</el-menu-item>
          <el-menu-item index="/checkin">入住办理</el-menu-item>
          <el-menu-item index="/checkout">退房结算</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/pos">
          <el-icon><ShoppingCart /></el-icon>
          <template #title>POS消费</template>
        </el-menu-item>
        
        <el-menu-item index="/inventory">
          <el-icon><Box /></el-icon>
          <template #title>库存管理</template>
        </el-menu-item>
        
        <el-menu-item index="/cleaning">
          <el-icon><Brush /></el-icon>
          <template #title>清洁任务</template>
        </el-menu-item>
        
        <el-sub-menu index="system" v-if="userStore.hasRole(['owner'])">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/employee">员工管理</el-menu-item>
          <el-menu-item index="/role">角色管理</el-menu-item>
          <el-menu-item index="/guest">宾客管理</el-menu-item>
          <el-menu-item index="/statistics">经营分析</el-menu-item>
          <el-menu-item index="/config">系统设置</el-menu-item>
          <el-menu-item index="/log">操作日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ userStore.realName || userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
  
  <el-dialog v-model="profileDialogVisible" title="个人信息" width="500px">
    <el-form :model="profileInfo" :rules="profileRules" ref="profileFormRef" label-width="100px" label-position="left">
      <el-form-item label="头像">
        <el-avatar :size="80" icon="UserFilled">
          <img v-if="profileInfo.avatar" :src="profileInfo.avatar" />
        </el-avatar>
      </el-form-item>
      <el-form-item label="用户名">
        <span>{{ profileInfo.username || '-' }}</span>
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="profileInfo.realName" placeholder="请输入真实姓名" />
      </el-form-item>
      <el-form-item label="角色">
        <el-tag :type="getRoleType(profileInfo.role)">{{ getRoleLabel(profileInfo.role) }}</el-tag>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="profileInfo.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="profileInfo.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="状态">
        <el-tag :type="profileInfo.status === 1 ? 'success' : 'info'">
          {{ profileInfo.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="profileDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitProfile" :loading="profileSubmitting">保存</el-button>
    </template>
  </el-dialog>
  
  <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="passwordDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitPassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updatePassword, updateEmployee } from '@/api/employee'
import { getCurrentEmployee } from '@/api/auth'

const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)
const currentRoute = computed(() => route.path)

const profileDialogVisible = ref(false)
const profileFormRef = ref(null)
const profileSubmitting = ref(false)
const profileInfo = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  role: '',
  status: 1,
  avatar: ''
})

const profileRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { 
      validator: (rule, value, callback) => {
        if (value && !/^1[3-9]\d{9}$/.test(value)) {
          callback(new Error('请输入正确的手机号'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { 
      validator: (rule, value, callback) => {
        if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
          callback(new Error('请输入正确的邮箱地址'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function getRoleType(role) {
  const types = { owner: 'danger', receptionist: 'primary', housekeeper: 'success' }
  return types[role] || 'info'
}

function getRoleLabel(role) {
  const labels = { owner: '经营者', receptionist: '前台', housekeeper: '房务' }
  return labels[role] || role
}

async function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    }).then(() => {
      userStore.logout()
    })
  } else if (command === 'profile') {
    try {
      const res = await getCurrentEmployee()
      profileInfo.value = { ...res.data }
      profileDialogVisible.value = true
    } catch (error) {
      ElMessage.error('获取个人信息失败')
    }
  } else if (command === 'password') {
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    passwordDialogVisible.value = true
  }
}

async function submitProfile() {
  await profileFormRef.value.validate()
  profileSubmitting.value = true
  try {
    await updateEmployee(profileInfo.value)
    ElMessage.success('个人信息更新成功')
    // 更新用户store中的信息
    await userStore.fetchUserInfo()
    profileDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  } finally {
    profileSubmitting.value = false
  }
}

async function submitPassword() {
  await passwordFormRef.value.validate()
  await updatePassword({
    oldPassword: passwordForm.value.oldPassword,
    newPassword: passwordForm.value.newPassword
  })
  ElMessage.success('密码修改成功')
  passwordDialogVisible.value = false
}
</script>

<style lang="scss" scoped>
.main-layout {
  height: 100vh;
}

.sidebar {
  background: #304156;
  transition: width 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100vh;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    background: #263445;
    flex-shrink: 0;
    
    .el-icon {
      font-size: 24px;
    }
  }
  
  .el-menu {
    border-right: none;
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
  }
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 15px;
    
    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      
      &:hover {
        color: #409eff;
      }
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      
      .username {
        color: #333;
      }
    }
  }
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
