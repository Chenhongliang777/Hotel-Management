<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/姓名/电话" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable>
            <el-option v-for="role in roleList" :key="role.roleCode" :label="role.roleName" :value="role.roleCode" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>员工列表</span>
          <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>添加员工</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :total="pagination.total" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '添加员工'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="默认123456" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option v-for="role in roleList" :key="role.roleCode" :label="role.roleName" :value="role.roleCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEmployeePage, addEmployee, updateEmployee, deleteEmployee, resetPassword } from '@/api/employee'
import { getAllRoles } from '@/api/role'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({ keyword: '', role: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = ref({ id: null, username: '', password: '', realName: '', phone: '', email: '', role: '', status: 1 })
const roleList = ref([])
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

onMounted(() => {
  loadRoles()
  loadData()
})

async function loadRoles() {
  try {
    const res = await getAllRoles()
    roleList.value = res.data || []
    if (roleList.value.length > 0 && !form.value.role) {
      form.value.role = roleList.value[0].roleCode
    }
  } catch (error) {
    console.error('加载角色列表失败', error)
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getEmployeePage({ page: pagination.page, size: pagination.size, ...searchForm })
    tableData.value = res.data.records; pagination.total = res.data.total
  } finally { loading.value = false }
}

function resetSearch() { searchForm.keyword = ''; searchForm.role = ''; pagination.page = 1; loadData() }
function getRoleType(r) {
  const role = roleList.value.find(role => role.roleCode === r)
  if (!role) return 'info'
  if (role.roleLevel >= 80) return 'danger'
  if (role.roleLevel >= 50) return 'primary'
  return 'success'
}
function getRoleLabel(r) {
  const role = roleList.value.find(role => role.roleCode === r)
  return role ? role.roleName : r
}

function openDialog(row = null) {
  isEdit.value = !!row
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { id: null, username: '', password: '', realName: '', phone: '', email: '', role: roleList.value.length > 0 ? roleList.value[0].roleCode : '', status: 1 }
  }
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) { await updateEmployee(form.value); ElMessage.success('更新成功') }
    else { await addEmployee(form.value); ElMessage.success('添加成功') }
    dialogVisible.value = false; loadData()
  } finally { submitting.value = false }
}

async function handleResetPassword(row) {
  await ElMessageBox.confirm(`确定重置"${row.realName}"的密码为123456吗？`, '提示')
  await resetPassword(row.id); ElMessage.success('密码已重置为123456')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除员工"${row.realName}"吗？`, '提示', { type: 'warning' })
  await deleteEmployee(row.id); ElMessage.success('删除成功'); loadData()
}
</script>
