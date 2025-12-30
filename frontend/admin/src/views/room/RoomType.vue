<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="房型名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入房型名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
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
          <span>房型列表</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon>添加房型
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="name" label="房型名称" min-width="120" />
        <el-table-column prop="basePrice" label="基础价格" width="100">
          <template #default="{ row }">¥{{ row.basePrice }}</template>
        </el-table-column>
        <el-table-column prop="weekendPrice" label="周末价格" width="100">
          <template #default="{ row }">¥{{ row.weekendPrice || '-' }}</template>
        </el-table-column>
        <el-table-column prop="maxGuests" label="最大入住" width="90">
          <template #default="{ row }">{{ row.maxGuests }}人</template>
        </el-table-column>
        <el-table-column prop="bedType" label="床型" width="120" />
        <el-table-column prop="area" label="面积" width="80">
          <template #default="{ row }">{{ row.area }}㎡</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房型' : '添加房型'" width="900px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房型名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入房型名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床型" prop="bedType">
              <el-input v-model="form.bedType" placeholder="如：大床1.8m" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="基础价格" prop="basePrice">
              <el-input-number v-model="form.basePrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="周末价格">
              <el-input-number v-model="form.weekendPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="节假日价格">
              <el-input-number v-model="form.holidayPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="最大入住" prop="maxGuests">
              <el-input-number v-model="form.maxGuests" :min="1" :max="10" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="床位数">
              <el-input-number v-model="form.bedCount" :min="1" :max="5" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="面积(㎡)">
              <el-input-number v-model="form.area" :min="0" :precision="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="房型描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入房型描述" />
        </el-form-item>
        <el-form-item label="设施配置">
          <el-input v-model="form.facilities" type="textarea" :rows="2" placeholder="JSON格式，如：[&quot;空调&quot;,&quot;WiFi&quot;,&quot;电视&quot;]" />
        </el-form-item>
        <el-form-item label="房间图片">
          <div class="image-upload-container">
            <div class="image-list">
              <div class="image-item" v-for="(img, index) in imageList" :key="index">
                <el-image
                  :src="img"
                  :preview-src-list="imageList"
                  :initial-index="index"
                  fit="cover"
                  class="room-image"
                  :preview-teleported="true"
                />
                <div class="image-actions">
                  <el-button type="danger" size="small" circle @click="removeImage(index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                  <el-button type="primary" size="small" circle @click="moveImage(index, -1)" :disabled="index === 0">
                    <el-icon><ArrowUp /></el-icon>
                  </el-button>
                  <el-button type="primary" size="small" circle @click="moveImage(index, 1)" :disabled="index === imageList.length - 1">
                    <el-icon><ArrowDown /></el-icon>
                  </el-button>
                </div>
              </div>
              <el-upload
                class="image-uploader"
                :action="uploadAction"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
                :before-upload="beforeUpload"
                accept="image/*"
                multiple
              >
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>本地上传
                </el-button>
              </el-upload>
            </div>
            <div class="url-upload">
              <el-input
                v-model="imageUrl"
                placeholder="输入图片URL链接"
                class="url-input"
              >
                <template #append>
                  <el-button @click="addImageByUrl">添加</el-button>
                </template>
              </el-input>
            </div>
          </div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" 
                active-text="上架" inactive-text="下架" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Upload, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { getRoomTypePage, addRoomType, updateRoomType, deleteRoomType } from '@/api/room'
import { uploadFile, addImageByUrl as addImageByUrlApi } from '@/api/file'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = ref({
  id: null,
  name: '',
  description: '',
  basePrice: 0,
  weekendPrice: null,
  holidayPrice: null,
  maxGuests: 2,
  bedCount: 1,
  bedType: '',
  area: null,
  facilities: '',
  images: '',
  status: 1,
  sortOrder: 0
})

const imageList = ref([])
const imageUrl = ref('')
const uploadAction = ref('/api/file/upload')
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const rules = {
  name: [{ required: true, message: '请输入房型名称', trigger: 'blur' }],
  basePrice: [{ required: true, message: '请输入基础价格', trigger: 'blur' }],
  maxGuests: [{ required: true, message: '请输入最大入住人数', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getRoomTypePage({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      status: searchForm.status
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.page = 1
  loadData()
}

function openDialog(row = null) {
  isEdit.value = !!row
  if (row) {
    form.value = { ...row }
    // 解析图片列表
    if (form.value.images) {
      try {
        imageList.value = typeof form.value.images === 'string' 
          ? JSON.parse(form.value.images) 
          : form.value.images
      } catch (e) {
        imageList.value = []
      }
    } else {
      imageList.value = []
    }
  } else {
    form.value = {
      id: null,
      name: '',
      description: '',
      basePrice: 0,
      weekendPrice: null,
      holidayPrice: null,
      maxGuests: 2,
      bedCount: 1,
      bedType: '',
      area: null,
      facilities: '',
      images: '',
      status: 1,
      sortOrder: 0
    }
    imageList.value = []
  }
  imageUrl.value = ''
  dialogVisible.value = true
}

// 监听图片列表变化，同步到form
watch(imageList, (newVal) => {
  form.value.images = JSON.stringify(newVal)
}, { deep: true })

async function submitForm() {
  await formRef.value.validate()
  
  // 确保图片列表已同步
  form.value.images = JSON.stringify(imageList.value)
  
  submitting.value = true
  
  try {
    if (isEdit.value) {
      await updateRoomType(form.value)
      ElMessage.success('更新成功')
    } else {
      await addRoomType(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

// 图片上传相关方法
function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

async function handleUploadSuccess(response) {
  if (response.code === 200 && response.data) {
    imageList.value.push(response.data.url)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

async function addImageByUrl() {
  if (!imageUrl.value || !imageUrl.value.trim()) {
    ElMessage.warning('请输入图片URL')
    return
  }
  
  const url = imageUrl.value.trim()
  
  // 简单验证URL格式
  if (!url.startsWith('http://') && !url.startsWith('https://')) {
    ElMessage.error('请输入有效的图片URL（以http://或https://开头）')
    return
  }
  
  try {
    const res = await addImageByUrlApi(url)
    if (res.code === 200 && res.data) {
      imageList.value.push(res.data.url)
      imageUrl.value = ''
      ElMessage.success('图片添加成功')
    }
  } catch (error) {
    ElMessage.error('添加图片失败：' + (error.message || '未知错误'))
  }
}

function removeImage(index) {
  imageList.value.splice(index, 1)
  ElMessage.success('图片已删除')
}

function moveImage(index, direction) {
  const newIndex = index + direction
  if (newIndex >= 0 && newIndex < imageList.value.length) {
    const temp = imageList.value[index]
    imageList.value[index] = imageList.value[newIndex]
    imageList.value[newIndex] = temp
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除房型"${row.name}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteRoomType(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}
</script>

<style scoped>
.image-upload-container {
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
}

.image-item {
  position: relative;
  width: 150px;
  height: 150px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
  transition: all 0.3s;
}

.image-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.image-item:hover .image-actions {
  opacity: 1;
}

.room-image {
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.image-actions {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-uploader {
  width: 150px;
  height: 150px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.image-uploader:hover {
  border-color: #409eff;
  color: #409eff;
}

.url-upload {
  margin-top: 10px;
}

.url-input {
  width: 100%;
}
</style>
