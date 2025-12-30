<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-content">
            <div class="stat-title">今日订单</div>
            <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
          </div>
          <el-icon class="stat-icon"><Document /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-content">
            <div class="stat-title">在住宾客</div>
            <div class="stat-value">{{ stats.currentGuests || 0 }}</div>
          </div>
          <el-icon class="stat-icon"><User /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card orange">
          <div class="stat-content">
            <div class="stat-title">今日营收</div>
            <div class="stat-value">¥{{ stats.todayRevenue || 0 }}</div>
          </div>
          <el-icon class="stat-icon"><Money /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card cyan">
          <div class="stat-content">
            <div class="stat-title">本月营收</div>
            <div class="stat-value">¥{{ stats.monthlyRevenue || 0 }}</div>
          </div>
          <el-icon class="stat-icon"><TrendCharts /></el-icon>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="info-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>今日待入住</span>
          </template>
          <div class="info-value">{{ stats.todayCheckIn || 0 }} 单</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>今日待退房</span>
          </template>
          <div class="info-value">{{ stats.todayCheckOut || 0 }} 单</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>待确认订单</span>
          </template>
          <div class="info-value warning">{{ stats.pendingOrders || 0 }} 单</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>低库存预警</span>
          </template>
          <div class="info-value" :class="{ danger: stats.lowStockCount > 0 }">
            {{ stats.lowStockCount || 0 }} 项
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近7日营收趋势</span>
            </div>
          </template>
          <div ref="revenueChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>房态统计</span>
          </template>
          <div ref="roomChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats, getRevenueChart } from '@/api/statistics'
import dayjs from 'dayjs'

const stats = ref({})
const revenueChartRef = ref(null)
const roomChartRef = ref(null)

let revenueChart = null
let roomChart = null

onMounted(async () => {
  await loadStats()
  await loadRevenueChart()
  initRoomChart()
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  revenueChart?.dispose()
  roomChart?.dispose()
})

function handleResize() {
  revenueChart?.resize()
  roomChart?.resize()
}

async function loadStats() {
  const res = await getDashboardStats()
  stats.value = res.data
  
  if (res.data.roomStats) {
    initRoomChart(res.data.roomStats)
  }
}

async function loadRevenueChart() {
  const endDate = dayjs().format('YYYY-MM-DD')
  const startDate = dayjs().subtract(6, 'day').format('YYYY-MM-DD')
  
  const res = await getRevenueChart({ startDate, endDate })
  
  const dates = []
  const revenues = []
  const counts = []
  
  for (let i = 6; i >= 0; i--) {
    const date = dayjs().subtract(i, 'day').format('YYYY-MM-DD')
    dates.push(dayjs(date).format('MM-DD'))
    
    const item = res.data.find(d => d.date === date)
    revenues.push(item ? Number(item.revenue) : 0)
    counts.push(item ? Number(item.count) : 0)
  }
  
  revenueChart = echarts.init(revenueChartRef.value)
  revenueChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['营收金额', '订单数量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '金额(元)',
        position: 'left'
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '营收金额',
        type: 'bar',
        data: revenues,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        }
      },
      {
        name: '订单数量',
        type: 'line',
        yAxisIndex: 1,
        data: counts,
        smooth: true,
        itemStyle: { color: '#67c23a' }
      }
    ]
  })
}

function initRoomChart(roomStats) {
  const data = roomStats || stats.value.roomStats || []
  
  roomChart = echarts.init(roomChartRef.value)
  roomChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 10
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c}间'
        },
        data: data.map(item => ({
          name: item.label,
          value: item.count,
          itemStyle: {
            color: getStatusColor(item.status)
          }
        }))
      }
    ]
  })
}

function getStatusColor(status) {
  const colors = {
    available: '#67c23a',
    occupied: '#409eff',
    reserved: '#e6a23c',
    maintenance: '#f56c6c'
  }
  return colors[status] || '#909399'
}
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-row {
    margin-bottom: 20px;
  }
  
  .stat-card {
    position: relative;
    border-radius: 12px;
    padding: 24px;
    color: #fff;
    overflow: hidden;
    
    &.blue {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    &.green {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    }
    &.orange {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    &.cyan {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
    
    .stat-content {
      position: relative;
      z-index: 1;
    }
    
    .stat-title {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 12px;
    }
    
    .stat-value {
      font-size: 32px;
      font-weight: bold;
    }
    
    .stat-icon {
      position: absolute;
      right: 20px;
      top: 50%;
      transform: translateY(-50%);
      font-size: 64px;
      opacity: 0.2;
    }
  }
  
  .info-row {
    margin-bottom: 20px;
    
    .info-value {
      font-size: 28px;
      font-weight: bold;
      color: #409eff;
      text-align: center;
      
      &.warning {
        color: #e6a23c;
      }
      &.danger {
        color: #f56c6c;
      }
    }
  }
}
</style>
