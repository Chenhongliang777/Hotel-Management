<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header><span>待分配任务</span></template>
          <div class="task-list">
            <div v-for="task in pendingTasks" :key="task.id" class="task-item pending">
              <div class="task-room">{{ task.roomNumber }}</div>
              <div class="task-type">{{ getTaskTypeLabel(task.taskType) }}</div>
              <el-button type="primary" size="small" @click="openAssignDialog(task)">分配</el-button>
            </div>
            <el-empty v-if="pendingTasks.length === 0" description="暂无待分配任务" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>任务列表</span>
              <el-button type="primary" @click="openCreateDialog"><el-icon><Plus /></el-icon>创建任务</el-button>
            </div>
          </template>
          <el-table :data="tasks" stripe v-loading="loading">
            <el-table-column prop="roomNumber" label="房间" width="80" />
            <el-table-column prop="taskType" label="类型" width="100">
              <template #default="{ row }">{{ getTaskTypeLabel(row.taskType) }}</template>
            </el-table-column>
            <el-table-column prop="assigneeName" label="负责人" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button v-if="row.status === 'assigned'" type="primary" link @click="startTask(row)">开始</el-button>
                <el-button v-if="['assigned','in_progress'].includes(row.status)" type="success" link @click="completeTask(row)">完成</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
              :total="pagination.total" layout="total, prev, pager, next" @current-change="loadTasks" />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog v-model="createDialogVisible" title="创建清洁任务" width="400px">
      <el-form :model="createForm" ref="createFormRef" label-width="80px">
        <el-form-item label="房间" prop="roomId">
          <el-select v-model="createForm.roomId" placeholder="选择房间" style="width:100%">
            <el-option v-for="room in rooms" :key="room.id" :label="room.roomNumber" :value="room.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="createForm.taskType" style="width:100%">
            <el-option label="退房清洁" value="checkout" />
            <el-option label="日常清洁" value="daily" />
            <el-option label="深度清洁" value="deep" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="createForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="assignDialogVisible" title="分配任务" width="400px">
      <el-form label-width="80px">
        <el-form-item label="房间">{{ currentTask.roomNumber }}</el-form-item>
        <el-form-item label="分配给">
          <el-select v-model="assignForm.assigneeId" placeholder="选择员工" style="width:100%" @change="onAssigneeChange">
            <el-option v-for="emp in housekeepers" :key="emp.id" :label="emp.realName" :value="emp.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTaskPage, getPendingTasks, createTask, assignTask, startTask as startTaskApi, completeTask as completeTaskApi } from '@/api/cleaning'
import { getAllRooms } from '@/api/room'
import { getEmployeePage } from '@/api/employee'

const loading = ref(false)
const submitting = ref(false)
const tasks = ref([])
const pendingTasks = ref([])
const rooms = ref([])
const housekeepers = ref([])
const createDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const currentTask = ref({})
const createFormRef = ref(null)

const pagination = reactive({ page: 1, size: 10, total: 0 })
const createForm = ref({ roomId: null, taskType: 'checkout', remark: '' })
const assignForm = ref({ assigneeId: null, assigneeName: '' })

onMounted(() => { loadTasks(); loadPendingTasks(); loadRooms(); loadHousekeepers() })

async function loadTasks() {
  loading.value = true
  try {
    const res = await getTaskPage({ page: pagination.page, size: pagination.size })
    tasks.value = res.data.records; pagination.total = res.data.total
  } finally { loading.value = false }
}

async function loadPendingTasks() { const res = await getPendingTasks(); pendingTasks.value = res.data }
async function loadRooms() { const res = await getAllRooms(); rooms.value = res.data }
async function loadHousekeepers() { const res = await getEmployeePage({ page: 1, size: 100, role: 'housekeeper' }); housekeepers.value = res.data.records }

function getTaskTypeLabel(t) { return { checkout: '退房清洁', daily: '日常清洁', deep: '深度清洁' }[t] || t }
function getStatusType(s) { return { pending: 'info', assigned: 'warning', in_progress: 'primary', completed: 'success' }[s] || 'info' }
function getStatusLabel(s) { return { pending: '待分配', assigned: '已分配', in_progress: '进行中', completed: '已完成' }[s] || s }

function openCreateDialog() { createForm.value = { roomId: null, taskType: 'checkout', remark: '' }; createDialogVisible.value = true }
function openAssignDialog(task) { currentTask.value = task; assignForm.value = { assigneeId: null, assigneeName: '' }; assignDialogVisible.value = true }
function onAssigneeChange(id) { const emp = housekeepers.value.find(e => e.id === id); assignForm.value.assigneeName = emp?.realName || '' }

async function submitCreate() {
  if (!createForm.value.roomId) { ElMessage.warning('请选择房间'); return }
  submitting.value = true
  try { await createTask(createForm.value); ElMessage.success('创建成功'); createDialogVisible.value = false; loadTasks(); loadPendingTasks() }
  finally { submitting.value = false }
}

async function submitAssign() {
  if (!assignForm.value.assigneeId) { ElMessage.warning('请选择员工'); return }
  submitting.value = true
  try { await assignTask(currentTask.value.id, assignForm.value); ElMessage.success('分配成功'); assignDialogVisible.value = false; loadTasks(); loadPendingTasks() }
  finally { submitting.value = false }
}

async function startTask(task) { await startTaskApi(task.id); ElMessage.success('任务已开始'); loadTasks() }
async function completeTask(task) { await completeTaskApi(task.id); ElMessage.success('任务已完成'); loadTasks(); loadPendingTasks() }

function formatDateTime(dateTime) {
  if (!dateTime) return '-'
  return dateTime.replace('T', ' ').substring(0, 19)
}
</script>

<style lang="scss" scoped>
.task-list { max-height: 500px; overflow-y: auto; }
.task-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px; border-radius: 6px; margin-bottom: 8px; background: #fdf6ec;
  .task-room { font-size: 18px; font-weight: bold; }
  .task-type { font-size: 12px; color: #909399; }
}
</style>
