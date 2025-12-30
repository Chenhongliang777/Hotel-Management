<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="房间号">
          <el-input v-model="searchForm.keyword" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="searchForm.roomTypeId" placeholder="全部" clearable>
            <el-option v-for="item in roomTypes" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="可用" value="available" />
            <el-option label="已入住" value="occupied" />
            <el-option label="已预订" value="reserved" />
            <el-option label="维修中" value="maintenance" />
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
          <span>房间列表</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon>添加房间
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column label="房型" min-width="120">
          <template #default="{ row }">{{ row.roomType?.name || '-' }}</template>
        </el-table-column>
        <el-table-column label="价格" width="100">
          <template #default="{ row }">¥{{ row.roomType?.basePrice || '-' }}</template>
        </el-table-column>
        <el-table-column prop="floor" label="楼层" width="80">
          <template #default="{ row }">{{ row.floor }}F</template>
        </el-table-column>
        <el-table-column prop="status" label="房间状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cleanStatus" label="清洁状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getCleanStatusType(row.cleanStatus)">
              {{ getCleanStatusLabel(row.cleanStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房间' : '添加房间'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="房型" prop="roomTypeId">
          <el-select v-model="form.roomTypeId" placeholder="请选择房型" style="width: 100%;">
            <el-option v-for="item in roomTypes" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="1" :max="50" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%;">
            <el-option label="可用" value="available" />
            <el-option label="已入住" value="occupied" />
            <el-option label="已预订" value="reserved" />
            <el-option label="维修中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { getRoomPage, addRoom, updateRoom, deleteRoom, getAvailableRoomTypes } from '@/api/room'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const roomTypes = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  roomTypeId: null,
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = ref({
  id: null,
  roomNumber: '',
  roomTypeId: null,
  floor: 1,
  status: 'available',
  remark: ''
})

const rules = {
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  roomTypeId: [{ required: true, message: '请选择房型', trigger: 'change' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }]
}

onMounted(() => {
  loadRoomTypes()
  loadData()
})

async function loadRoomTypes() {
  const res = await getAvailableRoomTypes()
  roomTypes.value = res.data
}

async function loadData() {
  loading.value = true
  try {
    const res = await getRoomPage({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.roomTypeId = null
  searchForm.status = ''
  pagination.page = 1
  loadData()
}

function getStatusType(status) {
  const types = { available: 'success', occupied: 'primary', reserved: 'warning', maintenance: 'danger' }
  return types[status] || 'info'
}

function getStatusLabel(status) {
  const labels = { available: '可用', occupied: '已入住', reserved: '已预订', maintenance: '维修中' }
  return labels[status] || status
}

function getCleanStatusType(status) {
  const types = { clean: 'success', dirty: 'danger', cleaning: 'warning' }
  return types[status] || 'info'
}

function getCleanStatusLabel(status) {
  const labels = { clean: '已清洁', dirty: '待清洁', cleaning: '清洁中' }
  return labels[status] || status
}

function openDialog(row = null) {
  isEdit.value = !!row
  if (row) {
    form.value = { ...row }
  } else {
    form.value = {
      id: null,
      roomNumber: '',
      roomTypeId: null,
      floor: 1,
      status: 'available',
      remark: ''
    }
  }
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value.validate()
  submitting.value = true
  
  try {
    if (isEdit.value) {
      await updateRoom(form.value)
      ElMessage.success('更新成功')
    } else {
      await addRoom(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除房间"${row.roomNumber}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteRoom(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}
</script>
