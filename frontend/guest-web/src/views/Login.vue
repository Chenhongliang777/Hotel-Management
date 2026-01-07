<template>
  <div class="login-page">
    <div class="login-container">
      <el-card class="login-card">
        <template #header>
          <div class="card-header">
            <h2>登录</h2>
            <p>欢迎回到悦居民宿</p>
          </div>
        </template>
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          label-width="80px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
            />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item label-width="0">
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              style="width: 100%"
            >
              登录
            </el-button>
          </el-form-item>
          <el-form-item label-width="0">
            <div class="footer-links">
              <span>还没有账号？</span>
              <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
              <span class="separator">|</span>
              <el-link type="primary" @click="$router.push('/')">返回首页</el-link>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const loginForm = ref({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      await userStore.doLogin(loginForm.value.username, loginForm.value.password)
      ElMessage.success('登录成功')
      
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 450px;
}

.login-card {
  :deep(.el-form-item[label-width="0"] .el-form-item__content) {
    margin-left: 0 !important;
    display: flex;
    justify-content: center;
  }
  
  .card-header {
    text-align: center;
    
    h2 {
      margin: 0 0 10px 0;
      color: #333;
      font-size: 28px;
    }
    
    p {
      margin: 0;
      color: #999;
      font-size: 14px;
    }
  }
  
  .footer-links {
    text-align: center;
    width: 100%;
    color: #666;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    
    .separator {
      margin: 0 4px;
      color: #ccc;
    }
  }
}
</style>

