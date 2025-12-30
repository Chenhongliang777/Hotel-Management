<template>
  <div class="page-container">
    <el-card>
      <template #header><span>今日待退房订单</span></template>
      <el-table :data="orders" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="roomId" label="房间" width="80">
          <template #default="{ row }">{{ getRoomNumber(row.roomId) }}</template>
        </el-table-column>
        <el-table-column prop="guestName" label="宾客" width="100" />
        <el-table-column prop="checkInDate" label="入住" width="110" />
        <el-table-column prop="checkOutDate" label="退房" width="110" />
        <el-table-column prop="totalPrice" label="房费" width="90">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
        <el-table-column prop="extraCharges" label="额外消费" width="90">
          <template #default="{ row }">¥{{ row.extraCharges || 0 }}</template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已付" width="90">
          <template #default="{ row }">¥{{ row.paidAmount }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openCheckOut(row)">办理退房</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" title="办理退房" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ getRoomNumber(currentOrder.roomId) }}</el-descriptions-item>
        <el-descriptions-item label="宾客姓名">{{ currentOrder.guestName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.guestPhone }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentOrder.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="退房日期">{{ currentOrder.checkOutDate }}</el-descriptions-item>
        <el-descriptions-item label="房费">¥{{ currentOrder.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="额外消费">¥{{ currentOrder.extraCharges || 0 }}</el-descriptions-item>
        <el-descriptions-item label="应付总计">
          <span class="total-amount">¥{{ (Number(currentOrder.totalPrice) + Number(currentOrder.extraCharges || 0)).toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="已付金额">¥{{ currentOrder.paidAmount }}</el-descriptions-item>
        <el-descriptions-item label="待结金额" :span="2">
          <span :class="balanceAmount > 0 ? 'need-pay' : 'need-refund'">
            {{ balanceAmount > 0 ? `需补交: ¥${balanceAmount.toFixed(2)}` : balanceAmount < 0 ? `需退还: ¥${Math.abs(balanceAmount).toFixed(2)}` : '已结清' }}
          </span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doCheckOut" :loading="submitting">确认退房</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTodayCheckOutOrders, checkOut } from '@/api/order'
import { getAllRooms } from '@/api/room'

const loading = ref(false)
const submitting = ref(false)
const orders = ref([])
const rooms = ref([])
const dialogVisible = ref(false)
const currentOrder = ref({})

const balanceAmount = computed(() => {
  const total = Number(currentOrder.value.totalPrice || 0) + Number(currentOrder.value.extraCharges || 0)
  return total - Number(currentOrder.value.paidAmount || 0)
})

onMounted(() => { loadOrders(); loadRooms() })

async function loadOrders() {
  loading.value = true
  try { const res = await getTodayCheckOutOrders(); orders.value = res.data }
  finally { loading.value = false }
}

async function loadRooms() { const res = await getAllRooms(); rooms.value = res.data }
function getRoomNumber(roomId) { return rooms.value.find(r => r.id === roomId)?.roomNumber || '-' }
function openCheckOut(order) { currentOrder.value = order; dialogVisible.value = true }

async function doCheckOut() {
  await ElMessageBox.confirm('确认办理退房？房间将自动标记为待清洁状态。', '确认退房')
  submitting.value = true
  try {
    await checkOut(currentOrder.value.id)
    ElMessage.success('退房办理成功')
    dialogVisible.value = false
    loadOrders()
  } finally { submitting.value = false }
}
</script>

<style lang="scss" scoped>
.total-amount { font-size: 18px; font-weight: bold; color: #409eff; }
.need-pay { color: #f56c6c; font-weight: bold; }
.need-refund { color: #67c23a; font-weight: bold; }
</style>
