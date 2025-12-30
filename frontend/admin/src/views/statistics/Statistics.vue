<template>
  <div class="page-container">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-title">今日营收</div>
          <div class="stat-value">¥{{ stats.todayRevenue || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-title">本月营收</div>
          <div class="stat-value">¥{{ stats.monthlyRevenue || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card orange">
          <div class="stat-title">今日订单</div>
          <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card cyan">
          <div class="stat-title">当前入住</div>
          <div class="stat-value">{{ stats.currentGuests || 0 }}</div>
        </div>
      </el-col>
    </el-row>
    
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>营收趋势</span>
          <el-radio-group v-model="dateRange" size="small" @change="loadRevenueChart">
            <el-radio-button label="7">近7天</el-radio-button>
            <el-radio-button label="30">近30天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="revenueChartRef" style="height: 400px;"></div>
    </el-card>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header><span>房态分布</span></template>
          <div ref="roomChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>低库存预警</span></template>
          <el-table :data="lowStockItems" stripe max-height="300">
            <el-table-column prop="name" label="物品" />
            <el-table-column prop="quantity" label="当前库存" width="100">
              <template #default="{ row }"><span class="low-stock">{{ row.quantity }}</span></template>
            </el-table-column>
            <el-table-column prop="minStock" label="预警值" width="80" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats, getRevenueChart } from '@/api/statistics'
import { getLowStockItems } from '@/api/inventory'
import dayjs from 'dayjs'

const stats = ref({})
const lowStockItems = ref([])
const dateRange = ref('7')
const revenueChartRef = ref(null)
const roomChartRef = ref(null)
let revenueChart = null
let roomChart = null

onMounted(async () => {
  await loadStats()
  loadRevenueChart()
  loadLowStock()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  revenueChart?.dispose()
  roomChart?.dispose()
})

function handleResize() { revenueChart?.resize(); roomChart?.resize() }

async function loadStats() {
  const res = await getDashboardStats()
  stats.value = res.data
  if (res.data.roomStats) initRoomChart(res.data.roomStats)
}

async function loadLowStock() { const res = await getLowStockItems(); lowStockItems.value = res.data }

async function loadRevenueChart() {
  const days = parseInt(dateRange.value)
  const endDate = dayjs().format('YYYY-MM-DD')
  const startDate = dayjs().subtract(days - 1, 'day').format('YYYY-MM-DD')
  const res = await getRevenueChart({ startDate, endDate })
  
  const dates = [], revenues = [], counts = []
  for (let i = days - 1; i >= 0; i--) {
    const date = dayjs().subtract(i, 'day').format('YYYY-MM-DD')
    dates.push(dayjs(date).format('MM-DD'))
    const item = res.data.find(d => d.date === date)
    revenues.push(item ? Number(item.revenue) : 0)
    counts.push(item ? Number(item.count) : 0)
  }
  
  if (!revenueChart) revenueChart = echarts.init(revenueChartRef.value)
  revenueChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: ['营收金额', '订单数量'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates },
    yAxis: [{ type: 'value', name: '金额(元)' }, { type: 'value', name: '订单数', position: 'right' }],
    series: [
      { name: '营收金额', type: 'bar', data: revenues, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#667eea' }, { offset: 1, color: '#764ba2' }]) } },
      { name: '订单数量', type: 'line', yAxisIndex: 1, data: counts, smooth: true, itemStyle: { color: '#67c23a' } }
    ]
  })
}

function initRoomChart(roomStats) {
  if (!roomChart) roomChart = echarts.init(roomChartRef.value)
  const colors = { available: '#67c23a', occupied: '#409eff', reserved: '#e6a23c', maintenance: '#f56c6c' }
  roomChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'horizontal', bottom: 0 },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{c}间' },
      data: roomStats.map(item => ({ name: item.label, value: item.count, itemStyle: { color: colors[item.status] || '#909399' } }))
    }]
  })
}
</script>

<style lang="scss" scoped>
.stat-cards { margin-bottom: 20px; }
.stat-card {
  padding: 24px; border-radius: 12px; color: #fff;
  &.blue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
  &.green { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
  &.orange { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
  &.cyan { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
  .stat-title { font-size: 14px; opacity: 0.9; margin-bottom: 10px; }
  .stat-value { font-size: 28px; font-weight: bold; }
}
.low-stock { color: #f56c6c; font-weight: bold; }
</style>
