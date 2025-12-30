<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/姓名/电话" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-card class="table-card">
      <template #header><span>宾客列表</span></template>
      
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :total="pagination.total" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    
    <el-dialog v-model="detailVisible" title="宾客详情" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ currentGuest.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentGuest.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentGuest.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentGuest.gender === 1 ? '男' : currentGuest.gender === 2 ? '女' : '未知' }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ currentGuest.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentGuest.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证" :span="2">{{ currentGuest.idCard || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ currentGuest.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getGuestPage } from '@/api/guest'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentGuest = ref({})

const searchForm = reactive({ keyword: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getGuestPage({ page: pagination.page, size: pagination.size, ...searchForm })
    tableData.value = res.data.records; pagination.total = res.data.total
  } finally { loading.value = false }
}

function resetSearch() { searchForm.keyword = ''; pagination.page = 1; loadData() }
function viewDetail(row) { currentGuest.value = row; detailVisible.value = true }
</script>
