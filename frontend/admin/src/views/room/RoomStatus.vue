<template>
  <div class="page-container">
    <el-card class="room-status-card">
      <template #header>
        <div class="card-header">
          <span>房态管理</span>
          <div class="legend">
            <span class="legend-item"><i class="dot available"></i>可用</span>
            <span class="legend-item"><i class="dot occupied"></i>已入住</span>
            <span class="legend-item"><i class="dot reserved"></i>已预订</span>
            <span class="legend-item"><i class="dot maintenance"></i>维修中</span>
          </div>
        </div>
      </template>
      
      <div class="room-grid" v-loading="loading">
        <div 
          v-for="room in rooms" 
          :key="room.id" 
          class="room-card"
          :class="[room.status, room.cleanStatus === 'dirty' ? 'dirty' : '']"
          @click="openRoomDetail(room)"
        >
          <div class="room-number">{{ room.roomNumber }}</div>
          <div class="room-type">{{ room.roomType?.name }}</div>
          <div class="room-price">¥{{ room.roomType?.basePrice }}</div>
          <div class="clean-badge" v-if="room.cleanStatus === 'dirty'">
            <el-icon><Warning /></el-icon>待清洁
          </div>
        </div>
      </div>
    </el-card>
    
    <el-dialog v-model="detailVisible" :title="`房间 ${currentRoom.roomNumber}`" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="房间号">{{ currentRoom.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="房型">{{ currentRoom.roomType?.name }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ currentRoom.floor }}F</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentRoom.roomType?.basePrice }}</el-descriptions-item>
        <el-descriptions-item label="房间状态">
          <el-tag :type="getStatusType(currentRoom.status)">
            {{ getStatusLabel(currentRoom.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="清洁状态">
          <el-tag :type="getCleanStatusType(currentRoom.cleanStatus)">
            {{ getCleanStatusLabel(currentRoom.cleanStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRoom.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="room-actions">
        <el-divider>快捷操作</el-divider>
        <el-space wrap>
          <el-button 
            v-if="currentRoom.status !== 'available'" 
            type="success" 
            @click="changeStatus('available')"
          >
            设为可用
          </el-button>
          <el-button 
            v-if="currentRoom.status !== 'maintenance'" 
            type="warning" 
            @click="changeStatus('maintenance')"
          >
            设为维修
          </el-button>
          <el-button 
            v-if="currentRoom.cleanStatus !== 'clean'" 
            type="primary" 
            @click="changeCleanStatus('clean')"
          >
            标记已清洁
          </el-button>
          <el-button 
            v-if="currentRoom.cleanStatus !== 'dirty'" 
            type="danger" 
            @click="changeCleanStatus('dirty')"
          >
            标记待清洁
          </el-button>
        </el-space>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllRooms, updateRoomStatus, updateCleanStatus } from '@/api/room'

const loading = ref(false)
const rooms = ref([])
const detailVisible = ref(false)
const currentRoom = ref({})

onMounted(() => {
  loadRooms()
})

async function loadRooms() {
  loading.value = true
  try {
    const res = await getAllRooms()
    rooms.value = res.data
  } finally {
    loading.value = false
  }
}

function openRoomDetail(room) {
  currentRoom.value = { ...room }
  detailVisible.value = true
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

async function changeStatus(status) {
  await updateRoomStatus(currentRoom.value.id, status)
  ElMessage.success('状态更新成功')
  currentRoom.value.status = status
  loadRooms()
}

async function changeCleanStatus(cleanStatus) {
  await updateCleanStatus(currentRoom.value.id, cleanStatus)
  ElMessage.success('清洁状态更新成功')
  currentRoom.value.cleanStatus = cleanStatus
  loadRooms()
}
</script>

<style lang="scss" scoped>
.room-status-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .legend {
    display: flex;
    gap: 20px;
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      color: #666;
      
      .dot {
        width: 12px;
        height: 12px;
        border-radius: 2px;
        
        &.available { background: #67c23a; }
        &.occupied { background: #409eff; }
        &.reserved { background: #e6a23c; }
        &.maintenance { background: #f56c6c; }
      }
    }
  }
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
}

.room-card {
  position: relative;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  
  &.available {
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    color: #fff;
  }
  &.occupied {
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    color: #fff;
  }
  &.reserved {
    background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
    color: #fff;
  }
  &.maintenance {
    background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
    color: #fff;
  }
  
  &.dirty {
    border: 2px dashed #f56c6c;
  }
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  }
  
  .room-number {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 8px;
  }
  
  .room-type {
    font-size: 12px;
    opacity: 0.9;
  }
  
  .room-price {
    font-size: 14px;
    margin-top: 4px;
  }
  
  .clean-badge {
    position: absolute;
    top: 4px;
    right: 4px;
    font-size: 10px;
    background: rgba(0,0,0,0.3);
    padding: 2px 6px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    gap: 2px;
  }
}

.room-actions {
  margin-top: 20px;
}
</style>
