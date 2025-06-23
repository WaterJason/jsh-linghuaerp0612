<template>
  <a-modal
    title="批量操作"
    :visible="visible"
    :width="800"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
  >
    <div class="batch-operation-container">
      <!-- 操作类型选择 -->
      <a-card title="操作类型" size="small" class="operation-type-card">
        <a-radio-group v-model="operationType" @change="handleOperationTypeChange">
          <a-radio value="delete">批量删除</a-radio>
          <a-radio value="update">批量更新</a-radio>
          <a-radio value="copy">批量复制</a-radio>
          <a-radio value="export">批量导出</a-radio>
        </a-radio-group>
      </a-card>

      <!-- 选中项目信息 -->
      <a-card title="选中项目" size="small" class="selected-items-card">
        <div class="selected-info">
          <a-tag color="blue">已选择 {{ selectedItems.length }} 项</a-tag>
          <a-button type="link" size="small" @click="showSelectedDetails = !showSelectedDetails">
            {{ showSelectedDetails ? '收起' : '查看详情' }}
          </a-button>
        </div>
        
        <div v-if="showSelectedDetails" class="selected-details">
          <a-list
            :data-source="selectedItems"
            size="small"
            :pagination="false"
          >
            <a-list-item slot="renderItem" slot-scope="item">
              <a-list-item-meta>
                <span slot="title">{{ item.title || item.name || `项目 ${item.id}` }}</span>
                <span slot="description">{{ item.description || item.date || '无描述' }}</span>
              </a-list-item-meta>
              <a-button type="link" size="small" @click="removeSelectedItem(item)">
                移除
              </a-button>
            </a-list-item>
          </a-list>
        </div>
      </a-card>

      <!-- 操作参数配置 -->
      <a-card title="操作配置" size="small" class="operation-config-card">
        <!-- 删除操作配置 -->
        <div v-if="operationType === 'delete'">
          <a-alert
            message="警告"
            description="删除操作不可恢复，请确认是否继续？"
            type="warning"
            show-icon
            style="margin-bottom: 16px"
          />
          <a-checkbox v-model="deleteConfig.confirmDelete">
            我确认要删除选中的 {{ selectedItems.length }} 项
          </a-checkbox>
        </div>

        <!-- 更新操作配置 -->
        <div v-if="operationType === 'update'">
          <a-form-model
            :model="updateConfig"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-model-item label="更新字段">
              <a-select v-model="updateConfig.field" placeholder="请选择要更新的字段">
                <a-select-option value="status">状态</a-select-option>
                <a-select-option value="priority">优先级</a-select-option>
                <a-select-option value="assignee">负责人</a-select-option>
                <a-select-option value="category">分类</a-select-option>
              </a-select>
            </a-form-model-item>
            
            <a-form-model-item label="新值">
              <a-input v-model="updateConfig.value" placeholder="请输入新值" />
            </a-form-model-item>
          </a-form-model>
        </div>

        <!-- 复制操作配置 -->
        <div v-if="operationType === 'copy'">
          <a-form-model
            :model="copyConfig"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-model-item label="复制数量">
              <a-input-number
                v-model="copyConfig.count"
                :min="1"
                :max="10"
                placeholder="每项复制数量"
                style="width: 100%"
              />
            </a-form-model-item>
            
            <a-form-model-item label="命名规则">
              <a-input v-model="copyConfig.namePattern" placeholder="如：{原名称}_副本_{序号}" />
            </a-form-model-item>
          </a-form-model>
        </div>

        <!-- 导出操作配置 -->
        <div v-if="operationType === 'export'">
          <a-form-model
            :model="exportConfig"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-model-item label="导出格式">
              <a-select v-model="exportConfig.format" placeholder="请选择导出格式">
                <a-select-option value="excel">Excel (.xlsx)</a-select-option>
                <a-select-option value="csv">CSV (.csv)</a-select-option>
                <a-select-option value="pdf">PDF (.pdf)</a-select-option>
                <a-select-option value="json">JSON (.json)</a-select-option>
              </a-select>
            </a-form-model-item>
            
            <a-form-model-item label="包含字段">
              <a-checkbox-group v-model="exportConfig.fields">
                <a-checkbox value="id">ID</a-checkbox>
                <a-checkbox value="name">名称</a-checkbox>
                <a-checkbox value="date">日期</a-checkbox>
                <a-checkbox value="status">状态</a-checkbox>
                <a-checkbox value="description">描述</a-checkbox>
              </a-checkbox-group>
            </a-form-model-item>
          </a-form-model>
        </div>
      </a-card>

      <!-- 操作进度 -->
      <a-card v-if="showProgress" title="操作进度" size="small" class="progress-card">
        <a-progress
          :percent="progressPercent"
          :status="progressStatus"
          :show-info="true"
        />
        <div class="progress-info">
          <span>{{ progressText }}</span>
        </div>
      </a-card>
    </div>
  </a-modal>
</template>

<script>
export default {
  name: 'BatchOperationModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    selectedItems: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      confirmLoading: false,
      operationType: 'delete',
      showSelectedDetails: false,
      showProgress: false,
      progressPercent: 0,
      progressStatus: 'active',
      progressText: '',
      deleteConfig: {
        confirmDelete: false
      },
      updateConfig: {
        field: '',
        value: ''
      },
      copyConfig: {
        count: 1,
        namePattern: '{原名称}_副本_{序号}'
      },
      exportConfig: {
        format: 'excel',
        fields: ['id', 'name', 'date', 'status']
      }
    }
  },
  methods: {
    handleOperationTypeChange() {
      // 重置配置
      this.deleteConfig.confirmDelete = false
      this.updateConfig = { field: '', value: '' }
      this.copyConfig = { count: 1, namePattern: '{原名称}_副本_{序号}' }
      this.exportConfig = { format: 'excel', fields: ['id', 'name', 'date', 'status'] }
    },
    removeSelectedItem(item) {
      this.$emit('remove-item', item)
    },
    async handleOk() {
      if (!this.validateOperation()) {
        return
      }

      this.confirmLoading = true
      this.showProgress = true
      this.progressPercent = 0
      this.progressStatus = 'active'

      try {
        await this.executeOperation()
        this.progressStatus = 'success'
        this.progressText = '操作完成'
        this.$message.success('批量操作执行成功')
        this.$emit('ok', {
          type: this.operationType,
          items: this.selectedItems,
          config: this.getOperationConfig()
        })
      } catch (error) {
        this.progressStatus = 'exception'
        this.progressText = '操作失败'
        this.$message.error('批量操作执行失败: ' + error.message)
      } finally {
        this.confirmLoading = false
        setTimeout(() => {
          this.showProgress = false
        }, 2000)
      }
    },
    validateOperation() {
      if (this.selectedItems.length === 0) {
        this.$message.warning('请先选择要操作的项目')
        return false
      }

      if (this.operationType === 'delete' && !this.deleteConfig.confirmDelete) {
        this.$message.warning('请确认删除操作')
        return false
      }

      if (this.operationType === 'update' && (!this.updateConfig.field || !this.updateConfig.value)) {
        this.$message.warning('请完整填写更新配置')
        return false
      }

      return true
    },
    async executeOperation() {
      const total = this.selectedItems.length
      
      for (let i = 0; i < total; i++) {
        // 模拟操作进度
        this.progressPercent = Math.round(((i + 1) / total) * 100)
        this.progressText = `正在处理第 ${i + 1} 项，共 ${total} 项`
        
        // 模拟异步操作
        await new Promise(resolve => setTimeout(resolve, 200))
      }
    },
    getOperationConfig() {
      switch (this.operationType) {
        case 'delete':
          return this.deleteConfig
        case 'update':
          return this.updateConfig
        case 'copy':
          return this.copyConfig
        case 'export':
          return this.exportConfig
        default:
          return {}
      }
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.batch-operation-container {
  max-height: 70vh;
  overflow-y: auto;
}

.operation-type-card,
.selected-items-card,
.operation-config-card,
.progress-card {
  margin-bottom: 16px;
}

.selected-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.selected-details {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 8px;
}

.progress-info {
  margin-top: 8px;
  text-align: center;
  color: #666;
}
</style>
