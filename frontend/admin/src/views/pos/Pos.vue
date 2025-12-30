<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>消费记录</span>
              <el-button type="primary" @click="openDialog"><el-icon><Plus /></el-icon>录入消费</el-button>
            </div>
          </template>
          <el-table :data="records" stripe v-loading="loading">
            <el-table-column prop="orderNo" label="订单号" width="160" />
            <el-table-column prop="itemName" label="商品" width="120" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="unitPrice" label="单价" width="80">
              <template #default="{ row }">¥{{ row.unitPrice }}</template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="总价" width="90">
              <template #default="{ row }">¥{{ row.totalPrice }}</template>
            </el-table-column>
            <el-table-column prop="category" label="类别" width="90" />
            <el-table-column prop="createTime" label="时间" width="160" />
          </el-table>
          <div class="pagination-container">
            <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
              :total="pagination.total" layout="total, prev, pager, next" @current-change="loadData" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>在住订单</span></template>
          <div class="order-list">
            <div v-for="order in checkedInOrders" :key="order.id" class="order-item"
                 :class="{ selected: selectedOrder?.id === order.id }" @click="selectOrder(order)">
              <div class="order-info">
                <span class="room">{{ order.roomId ? `房间${order.roomId}` : '-' }}</span>
                <span class="guest">{{ order.guestName }}</span>
              </div>
              <div class="order-no">{{ order.orderNo }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog v-model="dialogVisible" title="录入消费" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="订单" prop="orderId">
          <el-select v-model="form.orderId" placeholder="选择订单" style="width:100%">
            <el-option v-for="o in checkedInOrders" :key="o.id" :label="`${o.orderNo} - ${o.guestName}`" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品" prop="itemId">
          <el-select v-model="form.itemId" placeholder="选择商品" style="width:100%" @change="onItemChange">
            <el-option v-for="item in items" :key="item.id" :label="`${item.name} - ¥${item.price}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="itemName">
          <el-input v-model="form.itemName" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="饮料" value="beverage" />
            <el-option label="食品" value="food" />
            <el-option label="其他" value="other" />
          </el-select>
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
import { ElMessage } from 'element-plus'
import { getPosPage, createPosRecord } from '@/api/pos'
import { getOrderPage } from '@/api/order'
import { getAllItems } from '@/api/inventory'

const loading = ref(false)
const submitting = ref(false)
const records = ref([])
const checkedInOrders = ref([])
const items = ref([])
const selectedOrder = ref(null)
const dialogVisible = ref(false)
const formRef = ref(null)

const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = ref({ orderId: null, itemId: null, itemName: '', unitPrice: 0, quantity: 1, category: 'other' })
const rules = {
  orderId: [{ required: true, message: '请选择订单', trigger: 'change' }],
  itemName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

onMounted(() => { loadData(); loadOrders(); loadItems() })

async function loadData() {
  loading.value = true
  try {
    const res = await getPosPage({ page: pagination.page, size: pagination.size })
    records.value = res.data.records; pagination.total = res.data.total
  } finally { loading.value = false }
}

async function loadOrders() {
  const res = await getOrderPage({ page: 1, size: 100, status: 'checked_in' })
  checkedInOrders.value = res.data.records
}

async function loadItems() { const res = await getAllItems(); items.value = res.data }

function selectOrder(order) { selectedOrder.value = order; form.value.orderId = order.id }
function onItemChange(itemId) {
  const item = items.value.find(i => i.id === itemId)
  if (item) { form.value.itemName = item.name; form.value.unitPrice = item.price; form.value.category = item.category }
}

function openDialog() {
  form.value = { orderId: selectedOrder.value?.id || null, itemId: null, itemName: '', unitPrice: 0, quantity: 1, category: 'other' }
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value.validate()
  submitting.value = true
  try {
    await createPosRecord(form.value)
    ElMessage.success('录入成功'); dialogVisible.value = false; loadData()
  } finally { submitting.value = false }
}
</script>

<style lang="scss" scoped>
.order-list { max-height: 400px; overflow-y: auto; }
.order-item {
  padding: 12px; border: 1px solid #e4e7ed; border-radius: 6px; margin-bottom: 8px;
  cursor: pointer; transition: all 0.3s;
  &:hover { border-color: #409eff; }
  &.selected { border-color: #409eff; background: #ecf5ff; }
  .order-info { display: flex; justify-content: space-between; margin-bottom: 4px; }
  .room { font-weight: bold; }
  .order-no { font-size: 12px; color: #909399; }
}
</style>
