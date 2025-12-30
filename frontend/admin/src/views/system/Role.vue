<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="角色名称/编码/描述" clearable />
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
          <span>角色列表</span>
          <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>添加角色</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="roleLevel" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.roleLevel)">{{ row.roleLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="warning" link @click="openPermissionDialog(row)">权限设置</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :total="pagination.total" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '添加角色'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="isEdit" placeholder="如: owner" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="如: 民宿经营者" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="角色描述" />
        </el-form-item>
        <el-form-item label="角色级别" prop="roleLevel">
          <el-input-number v-model="form.roleLevel" :min="0" :max="100" placeholder="数字越大权限越高" />
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

    <el-dialog v-model="permissionDialogVisible" title="权限设置" width="600px">
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="{ children: 'children', label: 'permissionName' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissions"
        :default-expand-all="true"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPermissions" :loading="permissionSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getRolePage, addRole, updateRole, deleteRole, getRolePermissions, assignPermissions } from '@/api/role'
import { getPermissionTree } from '@/api/permission'

const loading = ref(false)
const submitting = ref(false)
const permissionSubmitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const permissionTreeRef = ref(null)
const currentRoleId = ref(null)

const searchForm = reactive({ keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = ref({ id: null, roleCode: '', roleName: '', description: '', roleLevel: 0, status: 1 })
const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleLevel: [{ required: true, message: '请输入角色级别', trigger: 'blur' }]
}

const permissionTree = ref([])
const checkedPermissions = ref([])

onMounted(() => {
  loadData()
  loadPermissionTree()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getRolePage({ page: pagination.page, size: pagination.size, ...searchForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadPermissionTree() {
  try {
    const res = await getPermissionTree()
    permissionTree.value = res.data
  } catch (error) {
    ElMessage.error('加载权限树失败')
  }
}

function resetSearch() {
  searchForm.keyword = ''
  pagination.page = 1
  loadData()
}

function getLevelType(level) {
  if (level >= 80) return 'danger'
  if (level >= 50) return 'primary'
  return 'success'
}

function openDialog(row = null) {
  isEdit.value = !!row
  form.value = row ? { ...row } : { id: null, roleCode: '', roleName: '', description: '', roleLevel: 0, status: 1 }
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateRole(form.value)
      ElMessage.success('更新成功')
    } else {
      await addRole(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

async function openPermissionDialog(row) {
  currentRoleId.value = row.id
  try {
    const res = await getRolePermissions(row.id)
    checkedPermissions.value = res.data || []
    permissionDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载权限失败')
  }
}

async function submitPermissions() {
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
  const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
  
  permissionSubmitting.value = true
  try {
    await assignPermissions(currentRoleId.value, allCheckedKeys)
    ElMessage.success('权限设置成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '权限设置失败')
  } finally {
    permissionSubmitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除角色"${row.roleName}"吗？`, '提示', { type: 'warning' })
  try {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}
</script>

