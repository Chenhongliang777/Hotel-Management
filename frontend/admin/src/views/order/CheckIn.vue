<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header><span>今日待入住订单</span></template>
          <el-table :data="orders" stripe v-loading="loading">
            <el-table-column prop="orderNo" label="订单号" width="160" />
            <el-table-column prop="guestName" label="宾客" width="100" />
            <el-table-column prop="guestPhone" label="电话" width="120" />
            <el-table-column prop="checkInDate" label="入住" width="110" />
            <el-table-column prop="checkOutDate" label="退房" width="110" />
            <el-table-column prop="totalPrice" label="总价" width="90">
              <template #default="{ row }">¥{{ row.totalPrice }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === 'confirmed' ? 'primary' : 'warning'">
                  {{ row.status === 'confirmed' ? '已确认' : '待确认' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="openCheckIn(row)">办理入住</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>可用房间</span></template>
          <div class="room-list">
            <div v-for="room in availableRooms" :key="room.id" class="room-item" 
                 :class="{ selected: selectedRoom?.id === room.id }" @click="selectRoom(room)">
              <div class="room-number">{{ room.roomNumber }}</div>
              <div class="room-type">{{ room.roomType?.name }}</div>
              <div class="room-price">¥{{ room.roomType?.basePrice }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog v-model="dialogVisible" title="办理入住" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="宾客姓名">{{ currentOrder.guestName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.guestPhone }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentOrder.checkInDate }} - {{ currentOrder.checkOutDate }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="已付金额">¥{{ currentOrder.paidAmount }}</el-descriptions-item>
        <el-descriptions-item label="分配房间">
          <el-select v-model="selectedRoomId" placeholder="请选择房间" style="width: 100%">
            <el-option v-for="room in availableRooms" :key="room.id" 
                       :label="`${room.roomNumber} - ${room.roomType?.name}`" :value="room.id" />
          </el-select>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doCheckIn" :loading="submitting">确认入住</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodayCheckInOrders, checkIn } from '@/api/order'
import { getAllRooms } from '@/api/room'

const loading = ref(false)
const submitting = ref(false)
const orders = ref([])
const availableRooms = ref([])
const dialogVisible = ref(false)
const currentOrder = ref({})
const selectedRoom = ref(null)
const selectedRoomId = ref(null)

onMounted(() => { loadOrders(); loadRooms() })

async function loadOrders() {
  loading.value = true
  try { const res = await getTodayCheckInOrders(); orders.value = res.data }
  finally { loading.value = false }
}

async function loadRooms() {
  const res = await getAllRooms()
  availableRooms.value = res.data.filter(r => r.status === 'available' && r.cleanStatus === 'clean')
}

function selectRoom(room) { selectedRoom.value = room; selectedRoomId.value = room.id }
function openCheckIn(order) { currentOrder.value = order; selectedRoomId.value = null; dialogVisible.value = true }

async function doCheckIn() {
  if (!selectedRoomId.value) { ElMessage.warning('请选择房间'); return }
  submitting.value = true
  try {
    await checkIn(currentOrder.value.id, selectedRoomId.value)
    ElMessage.success('入住办理成功')
    dialogVisible.value = false
    loadOrders(); loadRooms()
  } finally { submitting.value = false }
}
</script>

<style lang="scss" scoped>
.room-list { display: flex; flex-wrap: wrap; gap: 10px; }
.room-item {
  width: calc(50% - 5px); padding: 12px; border: 2px solid #e4e7ed; border-radius: 8px;
  cursor: pointer; text-align: center; transition: all 0.3s;
  &:hover { border-color: #409eff; }
  &.selected { border-color: #409eff; background: #ecf5ff; }
  .room-number { font-size: 18px; font-weight: bold; color: #303133; }
  .room-type { font-size: 12px; color: #909399; margin: 4px 0; }
  .room-price { font-size: 14px; color: #409eff; }
}
</style>
