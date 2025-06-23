<template>
  <div class="schedule-shift-manage">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <h2>班次管理</h2>
        <p>管理排班系统中的班次配置，包括班次时间、类型等设置</p>
      </div>
      <div class="page-actions">
        <a-button type="primary" @click="showAddModal" icon="plus">
          新增班次
        </a-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <a-form layout="inline" :form="searchForm">
        <a-form-item label="班次名称">
          <a-input
            v-decorator="['shiftName']"
            placeholder="请输入班次名称"
            style="width: 200px;"
            @pressEnter="handleSearch" />
        </a-form-item>
        
        <a-form-item label="班次类型">
          <a-select
            v-decorator="['shiftType']"
            placeholder="请选择班次类型"
            style="width: 150px;"
            allowClear>
            <a-select-option value="FULL_DAY">全天班</a-select-option>
            <a-select-option value="MORNING">上午班</a-select-option>
            <a-select-option value="AFTERNOON">下午班</a-select-option>
            <a-select-option value="EVENING">晚班</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="状态">
          <a-select
            v-decorator="['isActive']"
            placeholder="请选择状态"
            style="width: 120px;"
            allowClear>
            <a-select-option value="1">启用</a-select-option>
            <a-select-option value="0">禁用</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item>
          <a-button type="primary" @click="handleSearch" icon="search">
            搜索
          </a-button>
          <a-button @click="handleReset" style="margin-left: 8px;">
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 批量操作区域 -->
    <div class="batch-actions" v-if="selectedRowKeys.length > 0">
      <span>已选择 {{ selectedRowKeys.length }} 项</span>
      <a-divider type="vertical" />
      <a-button @click="batchEnable" icon="check">批量启用</a-button>
      <a-button @click="batchDisable" icon="stop">批量禁用</a-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-area">
      <a-table
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        :pagination="pagination"
        :rowSelection="rowSelection"
        rowKey="id"
        @change="handleTableChange"
        size="middle">
        
        <!-- 班次类型 -->
        <template slot="shiftType" slot-scope="text">
          <a-tag :color="getShiftTypeColor(text)">
            {{ getShiftTypeName(text) }}
          </a-tag>
        </template>
        
        <!-- 班次时间 -->
        <template slot="shiftTime" slot-scope="text, record">
          <span v-if="record.startTime && record.endTime">
            {{ record.startTime }} - {{ record.endTime }}
          </span>
          <span v-else class="text-muted">未设置</span>
        </template>
        
        <!-- 状态 -->
        <template slot="isActive" slot-scope="text">
          <a-tag :color="text ? 'green' : 'red'">
            {{ text ? '启用' : '禁用' }}
          </a-tag>
        </template>
        
        <!-- 使用次数 -->
        <template slot="assignmentCount" slot-scope="text">
          <a-badge :count="text" :numberStyle="{ backgroundColor: '#52c41a' }" />
        </template>
        
        <!-- 操作 -->
        <template slot="action" slot-scope="text, record">
          <a-button-group size="small">
            <a-button @click="showEditModal(record)" icon="edit">
              编辑
            </a-button>
            <a-button 
              @click="toggleStatus(record)" 
              :icon="record.isActive ? 'stop' : 'play-circle'">
              {{ record.isActive ? '禁用' : '启用' }}
            </a-button>
            <a-button 
              @click="deleteShift(record)" 
              type="danger" 
              icon="delete"
              :disabled="record.assignmentCount > 0">
              删除
            </a-button>
          </a-button-group>
        </template>
      </a-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      :visible="modalVisible"
      :title="modalTitle"
      width="600px"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :confirmLoading="submitLoading">
      
      <a-form :form="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="班次名称">
              <a-input
                v-decorator="['shiftName', { 
                  rules: [
                    { required: true, message: '请输入班次名称' },
                    { max: 50, message: '班次名称不能超过50个字符' }
                  ]
                }]"
                placeholder="请输入班次名称" />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item label="班次类型">
              <a-select
                v-decorator="['shiftType', { 
                  rules: [{ required: true, message: '请选择班次类型' }]
                }]"
                placeholder="请选择班次类型">
                <a-select-option value="FULL_DAY">全天班</a-select-option>
                <a-select-option value="MORNING">上午班</a-select-option>
                <a-select-option value="AFTERNOON">下午班</a-select-option>
                <a-select-option value="EVENING">晚班</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="开始时间">
              <a-time-picker
                v-decorator="['startTime']"
                format="HH:mm"
                placeholder="选择开始时间"
                style="width: 100%;" />
            </a-form-item>
          </a-col>
          
          <a-col :span="8">
            <a-form-item label="结束时间">
              <a-time-picker
                v-decorator="['endTime']"
                format="HH:mm"
                placeholder="选择结束时间"
                style="width: 100%;" />
            </a-form-item>
          </a-col>
          
          <a-col :span="8">
            <a-form-item label="时长(小时)">
              <a-input-number
                v-decorator="['durationHours']"
                :min="0"
                :max="24"
                :step="0.5"
                placeholder="班次时长"
                style="width: 100%;" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="显示颜色">
              <a-input
                v-decorator="['color', { initialValue: '#1890ff' }]"
                placeholder="颜色值"
                addonBefore="颜色">
                <template slot="addonAfter">
                  <div 
                    class="color-preview" 
                    :style="{ backgroundColor: form.getFieldValue('color') || '#1890ff' }">
                  </div>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item label="排序">
              <a-input-number
                v-decorator="['sortOrder', { initialValue: 0 }]"
                :min="0"
                placeholder="排序值"
                style="width: 100%;" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="状态">
          <a-radio-group v-decorator="['isActive', { initialValue: true }]">
            <a-radio :value="true">启用</a-radio>
            <a-radio :value="false">禁用</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="描述">
          <a-textarea
            v-decorator="['description']"
            placeholder="请输入班次描述"
            :rows="3"
            :maxLength="200"
            showCount />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import moment from 'moment'
import { getAction, postAction } from '@/api/manage'

export default {
  name: 'ScheduleShiftManage',
  
  data() {
    return {
      // 搜索表单
      searchForm: this.$form.createForm(this),
      
      // 表格数据
      dataSource: [],
      loading: false,
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
      },
      
      // 表格选择
      selectedRowKeys: [],
      
      // 弹窗
      modalVisible: false,
      form: this.$form.createForm(this),
      currentRecord: null,
      submitLoading: false,
      
      // 表格列配置
      columns: [
        {
          title: '班次名称',
          dataIndex: 'shiftName',
          key: 'shiftName',
          width: 120
        },
        {
          title: '班次类型',
          dataIndex: 'shiftType',
          key: 'shiftType',
          width: 100,
          scopedSlots: { customRender: 'shiftType' }
        },
        {
          title: '班次时间',
          key: 'shiftTime',
          width: 150,
          scopedSlots: { customRender: 'shiftTime' }
        },
        {
          title: '时长(小时)',
          dataIndex: 'durationHours',
          key: 'durationHours',
          width: 100
        },
        {
          title: '状态',
          dataIndex: 'isActive',
          key: 'isActive',
          width: 80,
          scopedSlots: { customRender: 'isActive' }
        },
        {
          title: '使用次数',
          dataIndex: 'assignmentCount',
          key: 'assignmentCount',
          width: 100,
          scopedSlots: { customRender: 'assignmentCount' }
        },
        {
          title: '排序',
          dataIndex: 'sortOrder',
          key: 'sortOrder',
          width: 80
        },
        {
          title: '创建时间',
          dataIndex: 'createTime',
          key: 'createTime',
          width: 150,
          customRender: (text) => text ? moment(text).format('YYYY-MM-DD HH:mm') : ''
        },
        {
          title: '操作',
          key: 'action',
          width: 200,
          fixed: 'right',
          scopedSlots: { customRender: 'action' }
        }
      ]
    }
  },
  
  computed: {
    modalTitle() {
      return this.currentRecord ? '编辑班次' : '新增班次'
    },
    
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.onSelectChange
      }
    }
  },
  
  mounted() {
    this.loadData()
  },
  
  methods: {
    // ==================== 数据加载 ====================
    
    async loadData() {
      try {
        this.loading = true
        
        const params = {
          ...this.searchForm.getFieldsValue(),
          page: this.pagination.current,
          pageSize: this.pagination.pageSize
        }
        
        const res = await getAction('/api/plugin/calendar-schedule/shift/list', params)
        
        if (res.code === 200) {
          this.dataSource = res.data || []
          // TODO: 实现分页
          this.pagination.total = this.dataSource.length
        }
      } catch (error) {
        console.error('加载班次数据失败:', error)
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    
    // ==================== 搜索操作 ====================
    
    handleSearch() {
      this.pagination.current = 1
      this.loadData()
    },
    
    handleReset() {
      this.searchForm.resetFields()
      this.pagination.current = 1
      this.loadData()
    },
    
    // ==================== 表格操作 ====================
    
    handleTableChange(pagination) {
      this.pagination = { ...this.pagination, ...pagination }
      this.loadData()
    },
    
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    
    // ==================== 弹窗操作 ====================
    
    showAddModal() {
      this.currentRecord = null
      this.modalVisible = true
      this.$nextTick(() => {
        this.form.resetFields()
      })
    },
    
    showEditModal(record) {
      this.currentRecord = record
      this.modalVisible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          shiftName: record.shiftName,
          shiftType: record.shiftType,
          startTime: record.startTime ? moment(record.startTime, 'HH:mm') : null,
          endTime: record.endTime ? moment(record.endTime, 'HH:mm') : null,
          durationHours: record.durationHours,
          color: record.color,
          sortOrder: record.sortOrder,
          isActive: record.isActive,
          description: record.description
        })
      })
    },
    
    handleCancel() {
      this.modalVisible = false
      this.currentRecord = null
      this.form.resetFields()
    },
    
    handleSubmit() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.submitForm(values)
        }
      })
    },
    
    async submitForm(values) {
      try {
        this.submitLoading = true
        
        const submitData = {
          ...values,
          startTime: values.startTime ? values.startTime.format('HH:mm:ss') : null,
          endTime: values.endTime ? values.endTime.format('HH:mm:ss') : null
        }
        
        if (this.currentRecord) {
          submitData.id = this.currentRecord.id
        }
        
        const apiUrl = this.currentRecord 
          ? '/api/plugin/calendar-schedule/shift/update'
          : '/api/plugin/calendar-schedule/shift/add'
        
        const res = await postAction(apiUrl, submitData)
        
        if (res.code === 200) {
          this.$message.success(this.currentRecord ? '更新成功' : '新增成功')
          this.handleCancel()
          this.loadData()
        } else {
          this.$message.error(res.message || '操作失败')
        }
        
      } catch (error) {
        console.error('提交班次数据失败:', error)
        this.$message.error('操作失败')
      } finally {
        this.submitLoading = false
      }
    },
    
    // ==================== 其他操作 ====================
    
    async toggleStatus(record) {
      try {
        const res = await postAction('/api/plugin/calendar-schedule/shift/batch-update-status', {
          ids: [record.id],
          isActive: !record.isActive
        })
        
        if (res.code === 200) {
          this.$message.success('状态更新成功')
          this.loadData()
        } else {
          this.$message.error(res.message || '状态更新失败')
        }
      } catch (error) {
        console.error('更新状态失败:', error)
        this.$message.error('状态更新失败')
      }
    },
    
    deleteShift(record) {
      this.$confirm({
        title: '确认删除',
        content: `确定要删除班次"${record.shiftName}"吗？`,
        onOk: async () => {
          try {
            const res = await postAction(`/api/plugin/calendar-schedule/shift/delete/${record.id}`)
            
            if (res.code === 200) {
              this.$message.success('删除成功')
              this.loadData()
            } else {
              this.$message.error(res.message || '删除失败')
            }
          } catch (error) {
            console.error('删除班次失败:', error)
            this.$message.error('删除失败')
          }
        }
      })
    },
    
    async batchEnable() {
      await this.batchUpdateStatus(true)
    },
    
    async batchDisable() {
      await this.batchUpdateStatus(false)
    },
    
    async batchUpdateStatus(isActive) {
      try {
        const res = await postAction('/api/plugin/calendar-schedule/shift/batch-update-status', {
          ids: this.selectedRowKeys,
          isActive: isActive
        })
        
        if (res.code === 200) {
          this.$message.success('批量操作成功')
          this.selectedRowKeys = []
          this.loadData()
        } else {
          this.$message.error(res.message || '批量操作失败')
        }
      } catch (error) {
        console.error('批量操作失败:', error)
        this.$message.error('批量操作失败')
      }
    },
    
    // ==================== 工具方法 ====================
    
    getShiftTypeName(type) {
      const typeMap = {
        'FULL_DAY': '全天班',
        'MORNING': '上午班',
        'AFTERNOON': '下午班',
        'EVENING': '晚班'
      }
      return typeMap[type] || type
    },
    
    getShiftTypeColor(type) {
      const colorMap = {
        'FULL_DAY': 'blue',
        'MORNING': 'green',
        'AFTERNOON': 'orange',
        'EVENING': 'purple'
      }
      return colorMap[type] || 'default'
    }
  }
}
</script>

<style scoped>
.schedule-shift-manage {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 500;
}

.page-title p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.search-area {
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.batch-actions {
  background: #e6f7ff;
  padding: 8px 16px;
  border-radius: 4px;
  margin-bottom: 16px;
  border: 1px solid #91d5ff;
}

.table-area {
  background: white;
  border-radius: 6px;
}

.color-preview {
  width: 20px;
  height: 20px;
  border-radius: 2px;
  border: 1px solid #d9d9d9;
}

.text-muted {
  color: #999;
}
</style>
