<template>
  <div class="schedule-list-view">
    <a-card :bordered="false" :loading="loading">
      <!-- 列表工具栏 -->
      <div class="list-toolbar">
        <div class="toolbar-left">
          <a-checkbox 
            :indeterminate="indeterminate"
            :checked="checkAll"
            @change="handleSelectAll">
            全选
          </a-checkbox>
          
          <a-button-group v-if="selectedRowKeys.length > 0">
            <a-button @click="handleBatchStatus('leave')">
              <a-icon type="calendar" />
              批量请假
            </a-button>
            <a-button @click="handleBatchStatus('swap')">
              <a-icon type="swap" />
              批量调班
            </a-button>
            <a-button @click="handleBatchDelete" type="danger">
              <a-icon type="delete" />
              批量删除
            </a-button>
          </a-button-group>
        </div>
        
        <div class="toolbar-right">
          <a-button-group>
            <a-button @click="exportData">
              <a-icon type="download" />
              导出
            </a-button>
            <a-button @click="refreshData">
              <a-icon type="reload" />
              刷新
            </a-button>
          </a-button-group>
        </div>
      </div>

      <!-- 数据表格 -->
      <a-table
        :columns="columns"
        :data-source="scheduleData"
        :row-key="record => record.id"
        :row-selection="rowSelection"
        :pagination="pagination"
        :scroll="{ x: 1200 }"
        size="middle"
        @change="handleTableChange">
        
        <!-- 员工信息列 -->
        <template #employeeInfo="text, record">
          <div class="employee-info">
            <a-avatar 
              :size="32" 
              :src="record.employeeAvatar"
              :style="{ backgroundColor: getEmployeeColor(record.employeeName) }">
              {{ record.employeeName && record.employeeName.charAt(0) }}
            </a-avatar>
            <div class="employee-details">
              <div class="name">{{ record.employeeName }}</div>
              <div class="role">{{ record.employeeRole }}</div>
            </div>
          </div>
        </template>
        
        <!-- 排班日期列 -->
        <template #scheduleDate="text, record">
          <div class="schedule-date">
            <div class="date">{{ formatDate(record.scheduleDate) }}</div>
            <div class="weekday" :class="{ weekend: record.isWeekend }">
              {{ record.dayOfWeekName }}
            </div>
            <a-tag v-if="record.isToday" color="blue" size="small">今天</a-tag>
          </div>
        </template>
        
        <!-- 班次信息列 -->
        <template #shiftInfo="text, record">
          <div class="shift-info">
            <a-tag :color="getShiftColor(record.shiftType)">
              {{ record.shiftType }}
            </a-tag>
            <div class="shift-time" v-if="record.startTime && record.endTime">
              {{ formatTime(record.startTime) }} - {{ formatTime(record.endTime) }}
            </div>
            <div class="work-hours" v-if="record.workHours">
              {{ record.workHours }}小时
            </div>
          </div>
        </template>
        
        <!-- 工作区域列 -->
        <template #workArea="text, record">
          <a-tag v-if="record.workArea" color="geekblue">
            {{ record.workArea }}
          </a-tag>
          <span v-else class="text-muted">未指定</span>
        </template>
        
        <!-- 状态列 -->
        <template #status="text, record">
          <a-tag :color="getStatusColor(record.status)">
            <a-icon :type="getStatusIcon(record.status)" />
            {{ record.statusName }}
          </a-tag>
        </template>
        
        <!-- 备注列 -->
        <template #notes="text, record">
          <div class="notes-cell">
            <span v-if="record.notes" class="notes-text">{{ record.notes }}</span>
            <span v-else class="text-muted">无备注</span>
          </div>
        </template>
        
        <!-- 操作列 -->
        <template #action="text, record">
          <div class="action-buttons">
            <a-button 
              v-if="hasEditPermission"
              size="small" 
              type="link" 
              @click="editSchedule(record)">
              <a-icon type="edit" />
              编辑
            </a-button>
            
            <a-dropdown>
              <a-button size="small" type="link">
                更多
                <a-icon type="down" />
              </a-button>
              <a-menu slot="overlay">
                <a-menu-item @click="copySchedule(record)">
                  <a-icon type="copy" />
                  复制排班
                </a-menu-item>
                <a-menu-item @click="swapSchedule(record)">
                  <a-icon type="swap" />
                  申请调班
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item 
                  v-if="hasDeletePermission"
                  @click="deleteSchedule(record)"
                  class="danger-item">
                  <a-icon type="delete" />
                  删除
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </div>
        </template>
      </a-table>
    </a-card>

    <!-- 批量操作确认弹窗 -->
    <a-modal
      title="批量操作确认"
      :visible="batchModalVisible"
      @ok="confirmBatchOperation"
      @cancel="batchModalVisible = false"
      :confirmLoading="batchLoading">
      <p>确定要对选中的 {{ selectedRowKeys.length }} 条排班记录执行 <strong>{{ batchOperationType }}</strong> 操作吗？</p>
      
      <a-form v-if="batchOperationType !== '删除'" layout="vertical">
        <a-form-item label="备注说明">
          <a-textarea 
            v-model="batchOperationRemark"
            placeholder="请输入操作备注"
            :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'ScheduleListView',
  
  props: {
    scheduleData: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    hasEditPermission: {
      type: Boolean,
      default: false
    },
    hasDeletePermission: {
      type: Boolean,
      default: false
    }
  },
  
  data() {
    return {
      selectedRowKeys: [],
      
      // 批量操作
      batchModalVisible: false,
      batchLoading: false,
      batchOperationType: '',
      batchOperationRemark: '',
      
      // 分页配置
      pagination: {
        current: 1,
        pageSize: 20,
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
      },
      
      // 表格列配置
      columns: [
        {
          title: '员工信息',
          dataIndex: 'employeeName',
          key: 'employeeInfo',
          width: 180,
          fixed: 'left',
          scopedSlots: { customRender: 'employeeInfo' }
        },
        {
          title: '排班日期',
          dataIndex: 'scheduleDate',
          key: 'scheduleDate',
          width: 120,
          scopedSlots: { customRender: 'scheduleDate' },
          sorter: true
        },
        {
          title: '班次信息',
          dataIndex: 'shiftType',
          key: 'shiftInfo',
          width: 150,
          scopedSlots: { customRender: 'shiftInfo' }
        },
        {
          title: '工作区域',
          dataIndex: 'workArea',
          key: 'workArea',
          width: 100,
          scopedSlots: { customRender: 'workArea' }
        },
        {
          title: '状态',
          dataIndex: 'status',
          key: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '备注',
          dataIndex: 'notes',
          key: 'notes',
          width: 200,
          scopedSlots: { customRender: 'notes' }
        },
        {
          title: '创建时间',
          dataIndex: 'createTime',
          key: 'createTime',
          width: 150,
          customRender: (text) => text ? dayjs(text).format('MM-DD HH:mm') : '-'
        },
        {
          title: '操作',
          key: 'action',
          width: 120,
          fixed: 'right',
          scopedSlots: { customRender: 'action' }
        }
      ]
    }
  },
  
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.handleSelectionChange
      }
    },
    
    checkAll() {
      return this.scheduleData.length > 0 && this.selectedRowKeys.length === this.scheduleData.length
    },
    
    indeterminate() {
      return this.selectedRowKeys.length > 0 && this.selectedRowKeys.length < this.scheduleData.length
    }
  },
  
  watch: {
    scheduleData: {
      handler(newData) {
        this.pagination.total = newData.length
      },
      immediate: true
    }
  },
  
  methods: {
    // 格式化日期
    formatDate(date) {
      return dayjs(date).format('MM-DD')
    },
    
    // 格式化时间
    formatTime(time) {
      return dayjs(time, 'HH:mm:ss').format('HH:mm')
    },
    
    // 获取员工颜色
    getEmployeeColor(name) {
      const colors = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#87d068']
      const index = name ? name.charCodeAt(0) % colors.length : 0
      return colors[index]
    },
    
    // 获取班次颜色
    getShiftColor(shift) {
      const colors = {
        '早班': 'green',
        '中班': 'blue',
        '晚班': 'orange',
        '夜班': 'purple',
        '全天': 'red'
      }
      return colors[shift] || 'default'
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colors = {
        'normal': 'green',
        'leave': 'orange',
        'swap': 'blue',
        'absent': 'red'
      }
      return colors[status] || 'default'
    },
    
    // 获取状态图标
    getStatusIcon(status) {
      const icons = {
        'normal': 'check-circle',
        'leave': 'calendar',
        'swap': 'swap',
        'absent': 'close-circle'
      }
      return icons[status] || 'question-circle'
    },
    
    // 选择变化处理
    handleSelectionChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    
    // 全选处理
    handleSelectAll(e) {
      if (e.target.checked) {
        this.selectedRowKeys = this.scheduleData.map(item => item.id)
      } else {
        this.selectedRowKeys = []
      }
    },
    
    // 表格变化处理
    handleTableChange(pagination, filters, sorter) {
      this.pagination = { ...this.pagination, ...pagination }
      this.$emit('table-change', { pagination, filters, sorter })
    },
    
    // 编辑排班
    editSchedule(record) {
      this.$emit('edit-schedule', record)
    },
    
    // 删除排班
    deleteSchedule(record) {
      this.$emit('delete-schedule', record)
    },
    
    // 复制排班
    copySchedule(record) {
      this.$emit('copy-schedule', record)
    },
    
    // 申请调班
    swapSchedule(record) {
      this.$emit('swap-schedule', record)
    },
    
    // 批量状态更新
    handleBatchStatus(status) {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请先选择要操作的排班记录')
        return
      }
      
      const statusNames = {
        'leave': '请假',
        'swap': '调班',
        'normal': '恢复正常'
      }
      
      this.batchOperationType = statusNames[status]
      this.batchOperationRemark = ''
      this.batchModalVisible = true
    },
    
    // 批量删除
    handleBatchDelete() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请先选择要删除的排班记录')
        return
      }
      
      this.batchOperationType = '删除'
      this.batchOperationRemark = ''
      this.batchModalVisible = true
    },
    
    // 确认批量操作
    confirmBatchOperation() {
      this.batchLoading = true
      
      const operationData = {
        ids: this.selectedRowKeys,
        type: this.batchOperationType,
        remark: this.batchOperationRemark
      }
      
      this.$emit('batch-update-status', operationData)
      
      // 模拟操作延迟
      setTimeout(() => {
        this.batchLoading = false
        this.batchModalVisible = false
        this.selectedRowKeys = []
      }, 1000)
    },
    
    // 导出数据
    exportData() {
      this.$emit('export-data', {
        selectedIds: this.selectedRowKeys,
        allData: this.scheduleData
      })
    },
    
    // 刷新数据
    refreshData() {
      this.$emit('refresh-data')
    }
  }
}
</script>

<style lang="less" scoped>
.schedule-list-view {
  .list-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;
    
    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    
    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }
  
  // 员工信息样式
  .employee-info {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .employee-details {
      .name {
        font-weight: 500;
        margin-bottom: 2px;
      }
      
      .role {
        font-size: 12px;
        color: #666;
      }
    }
  }
  
  // 排班日期样式
  .schedule-date {
    .date {
      font-weight: 500;
      margin-bottom: 2px;
    }
    
    .weekday {
      font-size: 12px;
      color: #666;
      
      &.weekend {
        color: #fa8c16;
      }
    }
  }
  
  // 班次信息样式
  .shift-info {
    .shift-time {
      font-size: 12px;
      color: #666;
      margin-top: 4px;
    }
    
    .work-hours {
      font-size: 12px;
      color: #1890ff;
      margin-top: 2px;
    }
  }
  
  // 备注样式
  .notes-cell {
    .notes-text {
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 1.4;
    }
  }
  
  // 操作按钮样式
  .action-buttons {
    display: flex;
    align-items: center;
    gap: 4px;
  }
  
  // 通用样式
  .text-muted {
    color: #999;
    font-style: italic;
  }
  
  .danger-item {
    color: #ff4d4f !important;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .schedule-list-view {
    .list-toolbar {
      flex-direction: column;
      gap: 12px;
      
      .toolbar-left,
      .toolbar-right {
        width: 100%;
        justify-content: space-between;
      }
    }
  }
}
</style>
