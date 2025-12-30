<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="订单号/姓名/电话" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已入住" value="checked_in" />
            <el-option label="已退房" value="checked_out" />
            <el-option label="已取消" value="cancelled" />
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
          <span>订单列表</span>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>创建订单
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="guestName" label="宾客姓名" width="100" />
        <el-table-column prop="guestPhone" label="联系电话" width="120" />
        <el-table-column prop="checkInDate" label="入住日期" width="110" />
        <el-table-column prop="checkOutDate" label="退房日期" width="110" />
        <el-table-column prop="nights" label="晚数" width="70" />
        <el-table-column prop="totalPrice" label="总价" width="100">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'pending'" type="success" link @click="confirmOrder(row)">确认</el-button>
            <el-button v-if="['pending','confirmed'].includes(row.status)" type="danger" link @click="cancelOrder(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusLabel(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="宾客姓名">{{ currentOrder.guestName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.guestPhone }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentOrder.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="退房日期">{{ currentOrder.checkOutDate }}</el-descriptions-item>
        <el-descriptions-item label="入住晚数">{{ currentOrder.nights }}晚</el-descriptions-item>
        <el-descriptions-item label="入住人数">{{ currentOrder.guestCount }}人</el-descriptions-item>
        <el-descriptions-item label="房间单价">¥{{ currentOrder.roomPrice }}</el-descriptions-item>
        <el-descriptions-item label="订单总价">¥{{ currentOrder.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="保证金">¥{{ currentOrder.deposit }}</el-descriptions-item>
        <el-descriptions-item label="已付金额">¥{{ currentOrder.paidAmount }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <el-dialog v-model="createVisible" title="创建订单" width="600px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="房型" prop="roomTypeId">
          <el-select v-model="createForm.roomTypeId" placeholder="请选择房型" style="width:100%">
            <el-option v-for="rt in roomTypes" :key="rt.id" :label="`${rt.name} - ¥${rt.basePrice}/晚`" :value="rt.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入住日期" prop="checkInDate">
          <el-date-picker v-model="createForm.checkInDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="退房日期" prop="checkOutDate">
          <el-date-picker v-model="createForm.checkOutDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="入住人数" prop="guestCount">
          <el-input-number v-model="createForm.guestCount" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="宾客姓名" prop="guestName">
          <el-input v-model="createForm.guestName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="guestPhone">
          <el-input v-model="createForm.guestPhone" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="createForm.guestIdCard" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, createOrder, confirmOrder as confirmOrderApi, cancelOrder as cancelOrderApi } from '@/api/order'
import { getAvailableRoomTypes } from '@/api/room'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const roomTypes = ref([])
const detailVisible = ref(false)
const createVisible = ref(false)
const currentOrder = ref({})
const createFormRef = ref(null)

const searchForm = reactive({ keyword: '', status: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const createForm = ref({ roomTypeId: null, checkInDate: '', checkOutDate: '', guestCount: 1, guestName: '', guestPhone: '', guestIdCard: '', remark: '' })
const createRules = {
  roomTypeId: [{ required: true, message: '请选择房型', trigger: 'change' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  checkOutDate: [{ required: true, message: '请选择退房日期', trigger: 'change' }],
  guestName: [{ required: true, message: '请输入宾客姓名', trigger: 'blur' }],
  guestPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

onMounted(() => { loadData(); loadRoomTypes() })

async function loadRoomTypes() { const res = await getAvailableRoomTypes(); roomTypes.value = res.data }
async function loadData() {
  loading.value = true
  try {
    const res = await getOrderPage({ page: pagination.page, size: pagination.size, ...searchForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

function resetSearch() { searchForm.keyword = ''; searchForm.status = ''; pagination.page = 1; loadData() }
function getStatusType(s) { return { pending: 'warning', confirmed: 'primary', checked_in: 'success', checked_out: 'info', cancelled: 'danger' }[s] || 'info' }
function getStatusLabel(s) { return { pending: '待确认', confirmed: '已确认', checked_in: '已入住', checked_out: '已退房', cancelled: '已取消' }[s] || s }
function viewDetail(row) { currentOrder.value = row; detailVisible.value = true }
function openCreateDialog() { createForm.value = { roomTypeId: null, checkInDate: '', checkOutDate: '', guestCount: 1, guestName: '', guestPhone: '', guestIdCard: '', remark: '' }; createVisible.value = true }

async function submitCreate() {
  await createFormRef.value.validate()
  submitting.value = true
  try { await createOrder(createForm.value); ElMessage.success('创建成功'); createVisible.value = false; loadData() }
  finally { submitting.value = false }
}

async function confirmOrder(row) {
  await ElMessageBox.confirm('确定要确认此订单吗？', '提示')
  await confirmOrderApi(row.id)
  ElMessage.success('确认成功'); loadData()
}

async function cancelOrder(row) {
  await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
  await cancelOrderApi(row.id)
  ElMessage.success('取消成功'); loadData()
}
</script>
