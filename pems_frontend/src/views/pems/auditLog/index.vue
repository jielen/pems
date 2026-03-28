<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="操作时间" style="width: 320px">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
        />
      </el-form-item>
      <el-form-item label="操作人" prop="operatorName">
        <el-input
          v-model="queryParams.operatorName"
          placeholder="请输入操作人姓名"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作类型" prop="operationType">
        <el-select v-model="queryParams.operationType" placeholder="请选择" clearable style="width: 180px">
          <el-option v-for="dict in operationTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="单位" prop="operatorUnit">
        <el-input
          v-model="queryParams.operatorUnit"
          placeholder="请输入单位名称"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['pems:log:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="CircleCheck" @click="handleVerify" v-hasPermi="['pems:log:list']">验证完整性</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="logList">
      <el-table-column label="操作时间" align="center" prop="operateTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.operateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="operatorName" :show-overflow-tooltip="true" />
      <el-table-column label="警号" align="center" prop="operatorBadge" width="120" />
      <el-table-column label="单位" align="center" prop="operatorUnit" :show-overflow-tooltip="true" />
      <el-table-column label="操作类型" align="center" prop="operationType" width="100">
        <template #default="scope">
          <el-tag :type="getOperationTypeTag(scope.row.operationType)">
            {{ getOperationTypeLabel(scope.row.operationType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作详情" align="left" prop="detail" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="IP地址" align="center" prop="operatorIp" width="140" />
      <el-table-column label="哈希值" align="left" prop="hashValue" :show-overflow-tooltip="true" min-width="200" />
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup name="PemsAuditLog">
import { listAuditLog, exportAuditLog, verifyAuditLog } from '@/api/pems/auditLog'
import { parseTime } from '@/utils/ruoyi'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(true)
const showSearch = ref(true)
const logList = ref([])
const total = ref(0)
const dateRange = ref([])

// 操作类型选项
const operationTypeOptions = [
  { value: 'CREATE', label: '创建' },
  { value: 'UPDATE', label: '更新' },
  { value: 'DELETE', label: '删除' },
  { value: 'TRANSFER', label: '移交' },
  { value: 'BORROW', label: '借用' },
  { value: 'RETURN', label: '归还' },
  { value: 'DESTROY', label: '销毁' },
  { value: 'STORE_IN', label: '入库' },
  { value: 'STORE_OUT', label: '出库' }
]

// 列信息
const columns = ref([
  { key: 0, label: '操作时间', visible: true },
  { key: 1, label: '操作人', visible: true },
  { key: 2, label: '警号', visible: true },
  { key: 3, label: '单位', visible: true },
  { key: 4, label: '操作类型', visible: true },
  { key: 5, label: '操作详情', visible: true },
  { key: 6, label: 'IP地址', visible: true },
  { key: 7, label: '哈希值', visible: true }
])

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  operatorName: '',
  operationType: '',
  operatorUnit: ''
})

/** 查询审计日志列表 */
function getList() {
  loading.value = true
  const params = {
    ...queryParams.value,
    beginTime: dateRange.value && dateRange.value.length === 2 ? dateRange.value[0] : undefined,
    endTime: dateRange.value && dateRange.value.length === 2 ? dateRange.value[1] : undefined
  }
  listAuditLog(params).then((response) => {
    logList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = []
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    operatorName: '',
    operationType: '',
    operatorUnit: ''
  }
  handleQuery()
}

/** 导出按钮操作 */
function handleExport() {
  const params = {
    ...queryParams.value,
    beginTime: dateRange.value && dateRange.value.length === 2 ? dateRange.value[0] : undefined,
    endTime: dateRange.value && dateRange.value.length === 2 ? dateRange.value[1] : undefined
  }
  ElMessageBox.confirm('是否确认导出所有审计日志数据项?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      exportAuditLog(params).then((response) => {
        const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '审计日志_' + parseTime(new Date(), '{y}{m}{d}{h}{i}{s}') + '.xlsx'
        link.click()
        window.URL.revokeObjectURL(url)
        ElMessage.success('导出成功')
      })
    })
    .catch(() => {})
}

/** 验证完整性按钮操作 */
function handleVerify() {
  verifyAuditLog().then((response) => {
    if (response.code === 200) {
      ElMessage.success(response.msg || '哈希链完整有效')
    } else {
      ElMessage.error(response.msg || '哈希链验证失败')
    }
  })
}

/** 获取操作类型标签样式 */
function getOperationTypeTag(type) {
  const tagMap = {
    CREATE: 'success',
    UPDATE: 'warning',
    DELETE: 'danger',
    TRANSFER: 'info',
    BORROW: 'primary',
    RETURN: 'primary',
    DESTROY: 'danger',
    STORE_IN: 'success',
    STORE_OUT: 'warning'
  }
  return tagMap[type] || 'info'
}

/** 获取操作类型标签文本 */
function getOperationTypeLabel(type) {
  const item = operationTypeOptions.find((opt) => opt.value === type)
  return item ? item.label : type
}

getList()
</script>
