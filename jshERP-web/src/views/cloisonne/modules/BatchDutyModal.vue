<template>
  <a-modal
    title="批量排班"
    :width="900"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" :model="model" :rules="validatorRules" v-bind="layout">
        <!-- 员工选择 -->
        <a-form-model-item label="选择员工" prop="employeeIds">
          <div class="employee-selection">
            <div class="selection-header">
              <a-checkbox 
                :indeterminate="indeterminate" 
                :checked="checkAll" 
                @change="onCheckAllChange"
              >
                全选
              </a-checkbox>
              <span class="selected-count">已选择 {{ model.employeeIds.length }} 人</span>
            </div>
            <div class="employee-grid">
              <a-checkbox-group v-model="model.employeeIds" @change="onEmployeeChange">
                <a-row :gutter="[16, 16]">
                  <a-col 
                    v-for="employee in employeeList" 
                    :key="employee.id"
                    :span="8"
                  >
                    <a-checkbox :value="employee.id" class="employee-checkbox">
                      <a-avatar :size="24" :style="{ backgroundColor: getAvatarColor(employee.name) }">
                        {{ employee.name.charAt(0) }}
                      </a-avatar>
                      <span class="employee-name">{{ employee.name }}</span>
                    </a-checkbox>
                  </a-col>
                </a-row>
              </a-checkbox-group>
            </div>
          </div>
        </a-form-model-item>
        
        <!-- 日期范围 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item label="开始日期" prop="startDate">
              <a-date-picker
                v-model="model.startDate"
                placeholder="请选择开始日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                valueFormat="YYYY-MM-DD"
                :disabled-date="disabledStartDate"
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="结束日期" prop="endDate">
              <a-date-picker
                v-model="model.endDate"
                placeholder="请选择结束日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                valueFormat="YYYY-MM-DD"
                :disabled-date="disabledEndDate"
              />
            </a-form-model-item>
          </a-col>
        </a-row>
        
        <!-- 班次和状态 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item label="班次类型" prop="shiftType">
              <a-select
                v-model="model.shiftType"
                placeholder="请选择班次类型"
                style="width: 100%"
                @change="handleShiftTypeChange"
              >
                <a-select-option value="早班">早班</a-select-option>
                <a-select-option value="晚班">晚班</a-select-option>
                <a-select-option value="全天">全天</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="状态" prop="status">
              <a-select
                v-model="model.status"
                placeholder="请选择状态"
                style="width: 100%"
              >
                <a-select-option value="normal">正常</a-select-option>
                <a-select-option value="leave">请假</a-select-option>
                <a-select-option value="swap">调班</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        
        <!-- 时间设置 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item label="开始时间" prop="startTime">
              <a-time-picker
                v-model="model.startTime"
                placeholder="请选择开始时间"
                style="width: 100%"
                format="HH:mm"
                valueFormat="HH:mm"
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="结束时间" prop="endTime">
              <a-time-picker
                v-model="model.endTime"
                placeholder="请选择结束时间"
                style="width: 100%"
                format="HH:mm"
                valueFormat="HH:mm"
                @change="calculateWorkHours"
              />
            </a-form-model-item>
          </a-col>
        </a-row>
        
        <!-- 工作时长和备注 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item label="工作时长" prop="workHours">
              <a-input-number
                v-model="model.workHours"
                placeholder="工作时长(小时)"
                style="width: 100%"
                :min="0"
                :max="24"
                :precision="1"
                :step="0.5"
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <!-- 占位列 -->
          </a-col>
        </a-row>
        
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="备注" prop="remark">
              <a-textarea
                v-model="model.remark"
                placeholder="请输入备注信息"
                :rows="3"
                :maxLength="500"
                show-count
              />
            </a-form-model-item>
          </a-col>
        </a-row>
        
        <!-- 预览信息 -->
        <a-form-model-item label="批量预览">
          <a-alert
            :message="previewMessage"
            type="info"
            show-icon
            style="margin-bottom: 16px"
          />
          <div class="preview-options">
            <a-checkbox v-model="model.skipConflicts">
              跳过冲突记录（如果员工在同一天同一班次已有排班）
            </a-checkbox>
          </div>
        </a-form-model-item>
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
import { batchAddDuty, getEmployeeList } from '@/api/cloisonne/duty'
import moment from 'moment'

export default {
  name: 'BatchDutyModal',
  data() {
    return {
      visible: false,
      confirmLoading: false,
      model: {
        employeeIds: [],
        startDate: '',
        endDate: '',
        shiftType: '早班',
        status: 'normal',
        startTime: '09:00',
        endTime: '13:00',
        workHours: 4,
        remark: '',
        skipConflicts: true
      },
      layout: {
        labelCol: { span: 4 },
        wrapperCol: { span: 18 },
      },
      validatorRules: {
        employeeIds: [
          { required: true, message: '请至少选择一名员工!', trigger: 'change' },
          { type: 'array', min: 1, message: '请至少选择一名员工!', trigger: 'change' }
        ],
        startDate: [{ required: true, message: '请选择开始日期!' }],
        endDate: [{ required: true, message: '请选择结束日期!' }],
        shiftType: [{ required: true, message: '请选择班次类型!' }],
        startTime: [{ required: true, message: '请选择开始时间!' }],
        endTime: [{ required: true, message: '请选择结束时间!' }],
        status: [{ required: true, message: '请选择状态!' }]
      },
      employeeList: [],
      indeterminate: false,
      checkAll: false
    }
  },
  computed: {
    // 预览信息
    previewMessage() {
      if (this.model.employeeIds.length === 0 || !this.model.startDate || !this.model.endDate) {
        return '请完善批量排班信息'
      }
      
      const startDate = moment(this.model.startDate)
      const endDate = moment(this.model.endDate)
      const days = endDate.diff(startDate, 'days') + 1
      const totalRecords = this.model.employeeIds.length * days
      
      return `将为 ${this.model.employeeIds.length} 名员工在 ${days} 天内创建 ${totalRecords} 条${this.model.shiftType}排班记录`
    }
  },
  created() {
    this.loadEmployeeList()
  },
  methods: {
    // 显示弹窗
    show() {
      this.visible = true
      this.resetForm()
    },
    
    // 重置表单
    resetForm() {
      this.model = {
        employeeIds: [],
        startDate: moment().format('YYYY-MM-DD'),
        endDate: moment().format('YYYY-MM-DD'),
        shiftType: '早班',
        status: 'normal',
        startTime: '09:00',
        endTime: '13:00',
        workHours: 4,
        remark: '',
        skipConflicts: true
      }
      this.checkAll = false
      this.indeterminate = false
      this.$nextTick(() => {
        this.$refs.form.clearValidate()
      })
    },
    
    // 加载员工列表
    loadEmployeeList() {
      getEmployeeList().then(res => {
        if (res.success) {
          this.employeeList = res.result || []
        }
      }).catch(err => {
        console.error('加载员工列表失败:', err)
      })
    },
    
    // 全选变化
    onCheckAllChange(e) {
      this.model.employeeIds = e.target.checked ? this.employeeList.map(emp => emp.id) : []
      this.indeterminate = false
    },
    
    // 员工选择变化
    onEmployeeChange(checkedList) {
      this.indeterminate = !!checkedList.length && checkedList.length < this.employeeList.length
      this.checkAll = checkedList.length === this.employeeList.length
    },
    
    // 班次类型变化
    handleShiftTypeChange(value) {
      if (value === '早班') {
        this.model.startTime = '09:00'
        this.model.endTime = '13:00'
        this.model.workHours = 4
      } else if (value === '晚班') {
        this.model.startTime = '14:00'
        this.model.endTime = '18:00'
        this.model.workHours = 4
      } else if (value === '全天') {
        this.model.startTime = '09:00'
        this.model.endTime = '18:00'
        this.model.workHours = 8
      }
    },
    
    // 计算工作时长
    calculateWorkHours() {
      if (this.model.startTime && this.model.endTime) {
        const start = moment(this.model.startTime, 'HH:mm')
        const end = moment(this.model.endTime, 'HH:mm')
        
        if (end.isAfter(start)) {
          const duration = moment.duration(end.diff(start))
          this.model.workHours = duration.asHours()
        }
      }
    },
    
    // 禁用开始日期
    disabledStartDate(current) {
      return current && current < moment().startOf('day')
    },
    
    // 禁用结束日期
    disabledEndDate(current) {
      const startDate = this.model.startDate
      if (!startDate) {
        return current && current < moment().startOf('day')
      }
      return current && current < moment(startDate).startOf('day')
    },
    
    // 获取头像颜色
    getAvatarColor(name) {
      const colors = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#87d068']
      const index = name.charCodeAt(0) % colors.length
      return colors[index]
    },
    
    // 提交表单
    submitForm() {
      const that = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 验证日期范围
          if (moment(this.model.endDate).isBefore(this.model.startDate)) {
            this.$message.warning('结束日期不能早于开始日期')
            return
          }
          
          that.confirmLoading = true
          
          // 构建批量数据
          const batchData = {
            employeeIds: this.model.employeeIds,
            startDate: this.model.startDate,
            endDate: this.model.endDate,
            shiftType: this.model.shiftType,
            status: this.model.status,
            startTime: this.model.startTime,
            endTime: this.model.endTime,
            workHours: this.model.workHours,
            remark: this.model.remark,
            skipConflicts: this.model.skipConflicts
          }
          
          batchAddDuty(batchData).then((res) => {
            if (res.success) {
              that.$message.success(res.message || '批量排班成功')
              that.$emit('ok')
              that.handleCancel()
            } else {
              that.$message.warning(res.message || '批量排班失败')
            }
          }).catch(err => {
            that.$message.error('批量排班失败：' + err.message)
          }).finally(() => {
            that.confirmLoading = false
          })
        }
      })
    },
    
    handleOk() {
      this.submitForm()
    },
    
    handleCancel() {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>

<style scoped>
.employee-selection {
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  padding: 16px;
}

.selection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.selected-count {
  color: #666;
  font-size: 14px;
}

.employee-grid {
  max-height: 200px;
  overflow-y: auto;
}

.employee-checkbox {
  display: flex;
  align-items: center;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.employee-checkbox:hover {
  background-color: #f5f5f5;
}

.employee-name {
  margin-left: 8px;
  font-size: 14px;
}

.preview-options {
  margin-top: 8px;
}
</style>
