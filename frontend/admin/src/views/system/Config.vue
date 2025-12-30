<template>
  <div class="page-container">
    <el-card>
      <template #header><span>系统配置</span></template>
      <el-form :model="configForm" label-width="140px" v-loading="loading">
        <el-divider content-position="left">民宿信息</el-divider>
        <el-form-item label="民宿名称">
          <el-input v-model="configForm.homestay_name" style="width: 400px" />
        </el-form-item>
        <el-form-item label="民宿地址">
          <el-input v-model="configForm.homestay_address" style="width: 400px" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="configForm.homestay_phone" style="width: 400px" />
        </el-form-item>
        <el-form-item label="民宿简介">
          <el-input v-model="configForm.homestay_description" type="textarea" :rows="3" style="width: 400px" />
        </el-form-item>
        
        <el-divider content-position="left">预订规则</el-divider>
        <el-form-item label="保证金比例">
          <el-input-number v-model.number="configForm.deposit_rate" :min="0" :max="1" :step="0.1" :precision="2" />
          <span class="form-tip">预订时需支付的保证金比例，如0.3表示30%</span>
        </el-form-item>
        <el-form-item label="免费取消时限(小时)">
          <el-input-number v-model.number="configForm.cancel_free_hours" :min="0" />
          <span class="form-tip">入住前多少小时可免费取消</span>
        </el-form-item>
        <el-form-item label="取消扣款比例">
          <el-input-number v-model.number="configForm.cancel_penalty_rate" :min="0" :max="1" :step="0.1" :precision="2" />
          <span class="form-tip">超过免费取消时限后取消的扣款比例</span>
        </el-form-item>
        
        <el-divider content-position="left">入住退房</el-divider>
        <el-form-item label="入住时间">
          <el-time-picker v-model="checkInTime" format="HH:mm" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="退房时间">
          <el-time-picker v-model="checkOutTime" format="HH:mm" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="延迟退房费(元/小时)">
          <el-input-number v-model.number="configForm.late_checkout_fee" :min="0" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveConfig" :loading="saving">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfigMap, batchUpdateConfig } from '@/api/config'

const loading = ref(false)
const saving = ref(false)
const configForm = ref({})
const checkInTime = ref('14:00')
const checkOutTime = ref('12:00')

onMounted(() => loadConfig())

async function loadConfig() {
  loading.value = true
  try {
    const res = await getConfigMap()
    configForm.value = res.data
    configForm.value.deposit_rate = parseFloat(res.data.deposit_rate || 0.3)
    configForm.value.cancel_free_hours = parseInt(res.data.cancel_free_hours || 24)
    configForm.value.cancel_penalty_rate = parseFloat(res.data.cancel_penalty_rate || 0.5)
    configForm.value.late_checkout_fee = parseFloat(res.data.late_checkout_fee || 50)
    checkInTime.value = res.data.check_in_time || '14:00'
    checkOutTime.value = res.data.check_out_time || '12:00'
  } finally { loading.value = false }
}

async function saveConfig() {
  saving.value = true
  try {
    const data = {
      homestay_name: configForm.value.homestay_name,
      homestay_address: configForm.value.homestay_address,
      homestay_phone: configForm.value.homestay_phone,
      homestay_description: configForm.value.homestay_description,
      deposit_rate: String(configForm.value.deposit_rate),
      cancel_free_hours: String(configForm.value.cancel_free_hours),
      cancel_penalty_rate: String(configForm.value.cancel_penalty_rate),
      check_in_time: checkInTime.value,
      check_out_time: checkOutTime.value,
      late_checkout_fee: String(configForm.value.late_checkout_fee)
    }
    await batchUpdateConfig(data)
    ElMessage.success('保存成功')
  } finally { saving.value = false }
}
</script>

<style lang="scss" scoped>
.form-tip { margin-left: 10px; color: #909399; font-size: 12px; }
</style>
