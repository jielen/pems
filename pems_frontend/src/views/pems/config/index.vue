<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="配置键" prop="paramKey">
        <el-input
          v-model="queryParams.paramKey"
          placeholder="请输入配置键"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="配置类型" prop="paramType">
        <el-select v-model="queryParams.paramType" placeholder="请选择" clearable style="width: 180px">
          <el-option label="字符串" value="string" />
          <el-option label="数字" value="number" />
          <el-option label="布尔" value="boolean" />
          <el-option label="JSON" value="json" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['pems:config:edit']">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="configList">
      <el-table-column label="配置ID" align="center" prop="id" width="80" />
      <el-table-column label="配置键" align="left" prop="paramKey" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="配置值" align="left" prop="paramValue" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="配置类型" align="center" prop="paramType" width="100">
        <template #default="scope">
          <el-tag :type="getTypeTag(scope.row.paramType)">{{ getTypeLabel(scope.row.paramType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="left" prop="remark" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-tooltip content="修改" placement="top" v-if="scope.row">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['pems:config:edit']"></el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form :model="form" :rules="rules" ref="configRef" label-width="100px">
        <el-form-item label="配置键" prop="paramKey">
          <el-input v-model="form.paramKey" placeholder="请输入配置键" :disabled="form.id !== undefined" />
        </el-form-item>
        <el-form-item label="配置值" prop="paramValue">
          <el-input v-model="form.paramValue" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="配置类型" prop="paramType">
          <el-select v-model="form.paramType" placeholder="请选择配置类型">
            <el-option label="字符串" value="string" />
            <el-option label="数字" value="number" />
            <el-option label="布尔" value="boolean" />
            <el-option label="JSON" value="json" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 快捷配置对话框 -->
    <el-dialog title="快捷配置" v-model="quickOpen" width="600px" append-to-body>
      <el-tabs v-model="activeTab">
        <!-- 物证类型配置 (CONFIG-01) -->
        <el-tab-pane label="物证类型" name="evidenceType">
          <el-form :model="evidenceTypeForm" label-width="120px">
            <el-form-item label="物证类型">
              <el-checkbox-group v-model="evidenceTypeForm.selected">
                <el-checkbox label="1">普通物证</el-checkbox>
                <el-checkbox label="2">贵重物证</el-checkbox>
                <el-checkbox label="3">涉密物证</el-checkbox>
                <el-checkbox label="4">危险物证</el-checkbox>
                <el-checkbox label="5">易腐物证</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="quickOpen = false">取消</el-button>
            <el-button type="primary" @click="submitEvidenceTypes">保存</el-button>
          </template>
        </el-tab-pane>

        <!-- 物证等级配置 (CONFIG-02) -->
        <el-tab-pane label="物证等级" name="evidenceLevel">
          <el-form :model="evidenceLevelForm" label-width="120px">
            <el-form-item label="物证等级">
              <el-radio-group v-model="evidenceLevelForm.selected">
                <el-radio label="1">一般物证</el-radio>
                <el-radio label="2">重要物证</el-radio>
                <el-radio label="3">核心物证</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="quickOpen = false">取消</el-button>
            <el-button type="primary" @click="submitEvidenceLevels">保存</el-button>
          </template>
        </el-tab-pane>

        <!-- 编号规则配置 (CONFIG-04) -->
        <el-tab-pane label="编号规则" name="numberingRule">
          <el-form :model="numberingForm" label-width="120px">
            <el-form-item label="编号规则">
              <el-input v-model="numberingForm.paramValue" placeholder="例如: YEAR|MONTH|SEQ|CHECK" />
            </el-form-item>
            <el-form-item label="规则说明">
              <el-tag>YEAR=年份, MONTH=月份, SEQ=序号, CHECK=校验位</el-tag>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="quickOpen = false">取消</el-button>
            <el-button type="primary" @click="submitNumberingRule">保存</el-button>
          </template>
        </el-tab-pane>

        <!-- 双人双锁配置 (CONFIG-06) -->
        <el-tab-pane label="双人双锁" name="dualLock">
          <el-form :model="dualLockForm" label-width="120px">
            <el-form-item label="启用双人双锁">
              <el-switch v-model="dualLockForm.enabled" active-value="true" inactive-value="false" />
            </el-form-item>
            <el-form-item label="说明">
              <el-tag type="info">启用后，关键操作需要双人同时授权</el-tag>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="quickOpen = false">取消</el-button>
            <el-button type="primary" @click="submitDualLock">保存</el-button>
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup name="PemsConfig">
import { listConfig, updateConfig, getConfig, getEvidenceTypes, getEvidenceLevels, getNumberingRule, getDualLockEnabled, updateEvidenceTypes, updateEvidenceLevels, updateNumberingRule, updateDualLockEnabled } from '@/api/pems/config'
import { parseTime } from '@/utils/ruoyi'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(true)
const showSearch = ref(true)
const configList = ref([])
const total = ref(0)
const title = ref('')
const open = ref(false)
const quickOpen = ref(false)
const activeTab = ref('evidenceType')
const configRef = ref()

// 列信息
const columns = ref([
  { key: 0, label: '配置ID', visible: true },
  { key: 1, label: '配置键', visible: true },
  { key: 2, label: '配置值', visible: true },
  { key: 3, label: '配置类型', visible: true },
  { key: 4, label: '备注', visible: true },
  { key: 5, label: '更新时间', visible: true }
])

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  paramKey: '',
  paramType: ''
})

// 表单参数
const form = ref({
  id: undefined,
  paramKey: '',
  paramValue: '',
  paramType: 'string',
  remark: ''
})

// 表单校验
const rules = {
  paramKey: [{ required: true, message: '配置键不能为空', trigger: 'blur' }],
  paramValue: [{ required: true, message: '配置值不能为空', trigger: 'blur' }],
  paramType: [{ required: true, message: '配置类型不能为空', trigger: 'change' }]
}

// 快捷配置表单
const evidenceTypeForm = ref({
  selected: []
})

const evidenceLevelForm = ref({
  selected: ''
})

const numberingForm = ref({
  paramValue: ''
})

const dualLockForm = ref({
  enabled: 'false'
})

/** 查询配置列表 */
function getList() {
  loading.value = true
  listConfig(queryParams.value).then((response) => {
    configList.value = response.rows
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
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    paramKey: '',
    paramType: ''
  }
  handleQuery()
}

/** 新增按钮操作 */
function handleAdd() {
  form.value = {
    id: undefined,
    paramKey: '',
    paramValue: '',
    paramType: 'string',
    remark: ''
  }
  open.value = true
  title.value = '添加配置'
}

/** 修改按钮操作 */
function handleUpdate(row) {
  getConfig(row.id).then((response) => {
    form.value = response.data
    open.value = true
    title.value = '修改配置'
  })
}

/** 提交按钮 */
function submitForm() {
  updateConfig(form.value).then((response) => {
    if (response.code === 200) {
      ElMessage.success('修改成功')
      open.value = false
      getList()
    } else {
      ElMessage.error(response.msg)
    }
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  form.value = {}
}

/** 获取类型标签样式 */
function getTypeTag(type) {
  const tagMap = {
    string: '',
    number: 'success',
    boolean: 'warning',
    json: 'info'
  }
  return tagMap[type] || ''
}

/** 获取类型标签文本 */
function getTypeLabel(type) {
  const labelMap = {
    string: '字符串',
    number: '数字',
    boolean: '布尔',
    json: 'JSON'
  }
  return labelMap[type] || type
}

/** 打开快捷配置 */
function openQuickConfig() {
  quickOpen.value = true
  activeTab.value = 'evidenceType'
  loadQuickConfigs()
}

/** 加载快捷配置数据 */
function loadQuickConfigs() {
  // 加载物证类型
  getEvidenceTypes().then((response) => {
    if (response.data && response.data.length > 0) {
      evidenceTypeForm.value.selected = response.data[0].paramValue.split(',')
    }
  })

  // 加载物证等级
  getEvidenceLevels().then((response) => {
    if (response.data && response.data.length > 0) {
      evidenceLevelForm.value.selected = response.data[0].paramValue
    }
  })

  // 加载编号规则
  getNumberingRule().then((response) => {
    if (response.data) {
      numberingForm.value.paramValue = response.data.paramValue || ''
    }
  })

  // 加载双人双锁配置
  getDualLockEnabled().then((response) => {
    dualLockForm.value.enabled = response.data ? 'true' : 'false'
  })
}

/** 提交物证类型配置 */
function submitEvidenceTypes() {
  const data = {
    paramKey: 'evidence_type',
    paramValue: evidenceTypeForm.value.selected.join(','),
    paramType: 'string',
    remark: '物证类型配置：1=普通 2=贵重 3=涉密 4=危险 5=易腐'
  }
  updateEvidenceTypes(data).then((response) => {
    if (response.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.msg)
    }
  })
}

/** 提交物证等级配置 */
function submitEvidenceLevels() {
  const data = {
    paramKey: 'evidence_level',
    paramValue: evidenceLevelForm.value.selected,
    paramType: 'string',
    remark: '物证等级配置：1=一般 2=重要 3=核心'
  }
  updateEvidenceLevels(data).then((response) => {
    if (response.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.msg)
    }
  })
}

/** 提交编号规则配置 */
function submitNumberingRule() {
  const data = {
    paramKey: 'numbering_rule',
    paramValue: numberingForm.value.paramValue,
    paramType: 'string',
    remark: '物证编号规则'
  }
  updateNumberingRule(data).then((response) => {
    if (response.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.msg)
    }
  })
}

/** 提交双人双锁配置 */
function submitDualLock() {
  const data = {
    paramKey: 'dual_lock_enabled',
    paramValue: dualLockForm.value.enabled,
    paramType: 'boolean',
    remark: '是否启用双人双锁模式'
  }
  updateDualLockEnabled(data).then((response) => {
    if (response.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.msg)
    }
  })
}

getList()
</script>
