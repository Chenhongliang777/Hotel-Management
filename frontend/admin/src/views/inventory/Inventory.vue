<template>
  <div class="page-container">
    <el-alert v-if="lowStockItems.length > 0" type="warning" :closable="false" class="low-stock-alert">
      <template #title>
        <span>低库存预警：{{ lowStockItems.map(i => i.name).join('、') }} 库存不足，请及时补充！</span>
      </template>
    </el-alert>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="物品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入物品名称" clearable />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="searchForm.category" placeholder="全部" clearable>
            <el-option label="洗浴用品" value="toiletry" />
            <el-option label="饮料" value="beverage" />
            <el-option label="食品" value="food" />
            <el-option label="日用品" value="supplies" />
            <el-option label="其他" value="other" />
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
          <span>库存列表</span>
          <el-space>
            <el-button type="success" @click="openStockDialog('in')">入库</el-button>
            <el-button type="warning" @click="openStockDialog('out')">出库</el-button>
            <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>添加物品</el-button>
          </el-space>
        </div>
      </template>
      
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="name" label="物品名称" min-width="120" />
        <el-table-column prop="category" label="类别" width="100">
          <template #default="{ row }">{{ getCategoryLabel(row.category) }}</template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="quantity" label="库存" width="90">
          <template #default="{ row }">
            <span :class="{ 'low-stock': row.quantity <= row.minStock }">{{ row.quantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="预警值" width="80" />
        <el-table-column prop="price" label="单价" width="80">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :total="pagination.total" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑物品' : '添加物品'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="洗浴用品" value="toiletry" />
            <el-option label="饮料" value="beverage" />
            <el-option label="食品" value="food" />
            <el-option label="日用品" value="supplies" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" placeholder="如：瓶、个、包" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="预警值"><el-input-number v-model="form.minStock" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="stockDialogVisible" :title="stockType === 'in' ? '物品入库' : '物品出库'" width="400px">
      <el-form :model="stockForm" ref="stockFormRef" label-width="80px">
        <el-form-item label="物品" prop="itemId">
          <el-select v-model="stockForm.itemId" placeholder="选择物品" style="width:100%">
            <el-option v-for="item in allItems" :key="item.id" :label="`${item.name} (库存:${item.quantity})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="stockForm.quantity" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="stockForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStock" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getItemPage, getAllItems, getLowStockItems, addItem, updateItem, deleteItem, stockIn, stockOut } from '@/api/inventory'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const allItems = ref([])
const lowStockItems = ref([])
const dialogVisible = ref(false)
const stockDialogVisible = ref(false)
const isEdit = ref(false)
const stockType = ref('in')
const formRef = ref(null)
const stockFormRef = ref(null)

const searchForm = reactive({ keyword: '', category: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = ref({ id: null, name: '', category: '', unit: '', price: 0, minStock: 10, description: '' })
const stockForm = ref({ itemId: null, quantity: 1, remark: '' })
const rules = { name: [{ required: true, message: '请输入物品名称', trigger: 'blur' }], category: [{ required: true, message: '请选择类别', trigger: 'change' }] }

onMounted(() => { loadData(); loadAllItems(); loadLowStock() })

async function loadData() {
  loading.value = true
  try {
    const res = await getItemPage({ page: pagination.page, size: pagination.size, ...searchForm })
    tableData.value = res.data.records; pagination.total = res.data.total
  } finally { loading.value = false }
}

async function loadAllItems() { const res = await getAllItems(); allItems.value = res.data }
async function loadLowStock() { const res = await getLowStockItems(); lowStockItems.value = res.data }

function resetSearch() { searchForm.keyword = ''; searchForm.category = ''; pagination.page = 1; loadData() }
function getCategoryLabel(c) { return { toiletry: '洗浴用品', beverage: '饮料', food: '食品', supplies: '日用品', other: '其他' }[c] || c }

function openDialog(row = null) {
  isEdit.value = !!row
  form.value = row ? { ...row } : { id: null, name: '', category: '', unit: '', price: 0, minStock: 10, description: '' }
  dialogVisible.value = true
}

function openStockDialog(type) { stockType.value = type; stockForm.value = { itemId: null, quantity: 1, remark: '' }; stockDialogVisible.value = true }

async function submitForm() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) { await updateItem(form.value); ElMessage.success('更新成功') }
    else { await addItem(form.value); ElMessage.success('添加成功') }
    dialogVisible.value = false; loadData(); loadAllItems(); loadLowStock()
  } finally { submitting.value = false }
}

async function submitStock() {
  if (!stockForm.value.itemId) { ElMessage.warning('请选择物品'); return }
  submitting.value = true
  try {
    if (stockType.value === 'in') { await stockIn(stockForm.value) }
    else { await stockOut(stockForm.value) }
    ElMessage.success(stockType.value === 'in' ? '入库成功' : '出库成功')
    stockDialogVisible.value = false; loadData(); loadAllItems(); loadLowStock()
  } finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除"${row.name}"吗？`, '提示', { type: 'warning' })
  await deleteItem(row.id); ElMessage.success('删除成功'); loadData()
}
</script>

<style lang="scss" scoped>
.low-stock-alert { margin-bottom: 20px; }
.low-stock { color: #f56c6c; font-weight: bold; }
</style>
