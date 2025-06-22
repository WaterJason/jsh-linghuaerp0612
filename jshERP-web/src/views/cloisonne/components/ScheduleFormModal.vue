<template>
  <a-modal
    :title="modalTitle"
    :visible="visible"
    :width="600"
    :confirmLoading="submitLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    :maskClosable="false">
    
    <a-form :form="form" layout="vertical">
      <!-- 基础信息 -->
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="排班日期">
            <a-date-picker
              v-decorator="['scheduleDate', { rules: [{ required: true, message: '请选择排班日期' }] }]"
              style="width: 100%"
              format="YYYY-MM-DD"
              :disabled-date="disabledDate"
              placeholder="选择日期" />
          </a-form-item>
        </a-col>
        
        <a-col :span="12">
          <a-form-item label="员工">
            <a-select
              v-decorator="['employeeId', { rules: [{ required: true, message: '请选择员工' }] }]"
              placeholder="选择员工"
              show-search
              :filter-option="filterEmployee"
              @change="handleEmployeeChange">
              <a-select-option 
                v-for="employee in employeeList"
                :key="employee.id"
                :value="employee.id">
                <div class="employee-option">
                  <a-avatar 
                    :size="24" 
                    :src="employee.avatar"
                    :style="{ backgroundColor: getEmployeeColor(employee.name) }">
                    {{ employee.name && employee.name.charAt(0) }}
                  </a-avatar>
                  <span class="employee-name">{{ employee.name }}</span>
                  <span class="employee-role">{{ employee.role }}</span>
                </div>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 班次信息 -->
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="班次类型">
            <a-select
              v-decorator="['shiftType', { rules: [{ required: true, message: '请选择班次类型' }] }]"
              placeholder="选择班次"
              @change="handleShiftTypeChange">
              <a-select-option 
                v-for="shift in shiftTypes"
                :key="shift.value"
                :value="shift.value">
                <div class="shift-option">
                  <span class="shift-name">{{ shift.label }}</span>
                  <span class="shift-time">{{ shift.time }}</span>
                </div>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        
        <a-col :span="12">
          <a-form-item label="工作区域">
            <a-select
              v-decorator="['workArea']"
              placeholder="选择工作区域"
              allow-clear>
              <a-select-option value="咖啡店">咖啡店</a-select-option>
              <a-select-option value="展厅">展厅</a-select-option>
              <a-select-option value="收银台">收银台</a-select-option>
              <a-select-option value="全部">全部区域</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 时间设置 -->
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="开始时间">
            <a-time-picker
              v-decorator="['startTime']"
              style="width: 100%"
              format="HH:mm"
              placeholder="开始时间"
              @change="calculateWorkHours" />
          </a-form-item>
        </a-col>
        
        <a-col :span="8">
          <a-form-item label="结束时间">
            <a-time-picker
              v-decorator="['endTime']"
              style="width: 100%"
              format="HH:mm"
              placeholder="结束时间"
              @change="calculateWorkHours" />
          </a-form-item>
        </a-col>
        
        <a-col :span="8">
          <a-form-item label="工作时长">
            <a-input-number
              v-decorator="['workHours']"
              style="width: 100%"
              :min="0"
              :max="24"
              :step="0.5"
              :precision="1"
              placeholder="小时"
              suffix="小时" />
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 状态和备注 -->
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="状态">
            <a-select
              v-decorator="['status', { initialValue: 'normal' }]"
              placeholder="选择状态">
              <a-select-option value="normal">
                <a-icon type="check-circle" style="color: #52c41a" />
                正常
              </a-select-option>
              <a-select-option value="leave">
                <a-icon type="calendar" style="color: #fa8c16" />
                请假
              </a-select-option>
              <a-select-option value="swap">
                <a-icon type="swap" style="color: #1890ff" />
                调班
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        
        <a-col :span="12">
          <a-form-item label="优先级">
            <a-select
              v-decorator="['priority', { initialValue: 'normal' }]"
              placeholder="选择优先级">
              <a-select-option value="low">低</a-select-option>
              <a-select-option value="normal">普通</a-select-option>
              <a-select-option value="high">高</a-select-option>
              <a-select-option value="urgent">紧急</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 备注信息 -->
      <a-form-item label="备注">
        <a-textarea
          v-decorator="['notes']"
          placeholder="输入备注信息"
          :rows="3"
          :maxLength="500"
          show-count />
      </a-form-item>

      <!-- 重复设置 -->
      <a-form-item v-if="!isEdit">
        <a-checkbox v-model="enableRepeat">
          重复排班
        </a-checkbox>
        
        <div v-if="enableRepeat" class="repeat-settings">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="重复类型">
                <a-select v-model="repeatType" placeholder="选择重复类型">
                  <a-select-option value="daily">每日</a-select-option>
                  <a-select-option value="weekly">每周</a-select-option>
                  <a-select-option value="monthly">每月</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="重复次数">
                <a-input-number
                  v-model="repeatCount"
                  :min="1"
                  :max="30"
                  placeholder="次数" />
              </a-form-item>
            </a-col>
          </a-row>
        </div>
      </a-form-item>
    </a-form>

    <!-- 冲突检查结果 -->
    <a-alert
      v-if="conflictSchedules.length > 0"
      type="warning"
      show-icon
      class="conflict-alert">
      <template #message>
        <div>
          <strong>发现排班冲突：</strong>
          <ul class="conflict-list">
            <li v-for="conflict in conflictSchedules" :key="conflict.id">
              {{ conflict.employeeName }} 在 {{ conflict.scheduleDate }} 
              {{ conflict.startTime }}-{{ conflict.endTime }} 已有排班
            </li>
          </ul>
        </div>
      </template>
    </a-alert>
  </a-modal>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'ScheduleFormModal',
  
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    formData: {
      type: Object,
      default: () => ({})
    },
    employeeList: {
      type: Array,
      default: () => []
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  
  data() {
    return {
      form: this.$form.createForm(this),
      submitLoading: false,
      
      // 重复设置
      enableRepeat: false,
      repeatType: 'weekly',
      repeatCount: 4,
      
      // 冲突检查
      conflictSchedules: [],
      
      // 班次类型配置
      shiftTypes: [
        { value: '早班', label: '早班', time: '08:00-16:00' },
        { value: '中班', label: '中班', time: '12:00-20:00' },
        { value: '晚班', label: '晚班', time: '16:00-24:00' },
        { value: '夜班', label: '夜班', time: '00:00-08:00' },
        { value: '全天', label: '全天', time: '08:00-24:00' }
      ]
    }
  },
  
  computed: {
    modalTitle() {
      return this.isEdit ? '编辑排班' : '新增排班'
    }
  },
  
  watch: {
    visible(val) {
      if (val) {
        this.initForm()
      } else {
        this.resetForm()
      }
    }
  },
  
  methods: {
    // 初始化表单
    initForm() {
      this.$nextTick(() => {
        if (this.isEdit && this.formData.id) {
          // 编辑模式，填充表单数据
          this.form.setFieldsValue({
            scheduleDate: this.formData.scheduleDate ? dayjs(this.formData.scheduleDate) : null,
            employeeId: this.formData.employeeId,
            shiftType: this.formData.shiftType,
            workArea: this.formData.workArea,
            startTime: this.formData.startTime ? dayjs(this.formData.startTime, 'HH:mm') : null,
            endTime: this.formData.endTime ? dayjs(this.formData.endTime, 'HH:mm') : null,
            workHours: this.formData.workHours,
            status: this.formData.status || 'normal',
            notes: this.formData.notes
          })
        } else {
          // 新增模式，设置默认值
          this.form.setFieldsValue({
            scheduleDate: this.formData.scheduleDate ? dayjs(this.formData.scheduleDate) : dayjs(),
            status: 'normal'
          })
        }
      })
    },
    
    // 重置表单
    resetForm() {
      this.form.resetFields()
      this.enableRepeat = false
      this.conflictSchedules = []
    },
    
    // 禁用日期
    disabledDate(current) {
      // 禁用过去的日期
      return current && current < dayjs().startOf('day')
    },
    
    // 员工筛选
    filterEmployee(input, option) {
      const employee = this.employeeList.find(emp => emp.id === option.key)
      return employee && employee.name.toLowerCase().includes(input.toLowerCase())
    },
    
    // 获取员工颜色
    getEmployeeColor(name) {
      const colors = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#87d068']
      const index = name ? name.charCodeAt(0) % colors.length : 0
      return colors[index]
    },
    
    // 员工变化处理
    handleEmployeeChange(employeeId) {
      const employee = this.employeeList.find(emp => emp.id === employeeId)
      if (employee) {
        // 可以根据员工信息设置默认工作区域等
        console.log('Selected employee:', employee)
      }
      
      // 检查冲突
      this.checkConflict()
    },
    
    // 班次类型变化处理
    handleShiftTypeChange(shiftType) {
      const shift = this.shiftTypes.find(s => s.value === shiftType)
      if (shift) {
        const [startTime, endTime] = shift.time.split('-')
        this.form.setFieldsValue({
          startTime: dayjs(startTime, 'HH:mm'),
          endTime: dayjs(endTime, 'HH:mm')
        })
        this.calculateWorkHours()
      }
    },
    
    // 计算工作时长
    calculateWorkHours() {
      const startTime = this.form.getFieldValue('startTime')
      const endTime = this.form.getFieldValue('endTime')
      
      if (startTime && endTime) {
        let hours = endTime.diff(startTime, 'hours', true)
        if (hours < 0) {
          hours += 24 // 跨天处理
        }
        this.form.setFieldsValue({
          workHours: Math.round(hours * 2) / 2 // 保留0.5小时精度
        })
      }
    },
    
    // 检查冲突
    async checkConflict() {
      const employeeId = this.form.getFieldValue('employeeId')
      const scheduleDate = this.form.getFieldValue('scheduleDate')
      const startTime = this.form.getFieldValue('startTime')
      const endTime = this.form.getFieldValue('endTime')
      
      if (employeeId && scheduleDate && startTime && endTime) {
        try {
          // 这里应该调用API检查冲突
          // const conflicts = await checkScheduleConflict({...})
          // this.conflictSchedules = conflicts
          
          // 模拟冲突检查
          this.conflictSchedules = []
        } catch (error) {
          console.error('检查冲突失败:', error)
        }
      }
    },
    
    // 提交表单
    handleSubmit() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.submitLoading = true
          
          // 处理表单数据
          const formData = {
            ...values,
            scheduleDate: values.scheduleDate.format('YYYY-MM-DD'),
            startTime: values.startTime ? values.startTime.format('HH:mm') : null,
            endTime: values.endTime ? values.endTime.format('HH:mm') : null,
            employeeName: this.getEmployeeName(values.employeeId)
          }
          
          // 如果是编辑模式，添加ID
          if (this.isEdit) {
            formData.id = this.formData.id
          }
          
          // 如果启用重复，添加重复设置
          if (this.enableRepeat && !this.isEdit) {
            formData.repeat = {
              type: this.repeatType,
              count: this.repeatCount
            }
          }
          
          this.$emit('submit', formData)
          
          // 模拟提交延迟
          setTimeout(() => {
            this.submitLoading = false
          }, 1000)
        }
      })
    },
    
    // 取消操作
    handleCancel() {
      this.$emit('cancel')
    },
    
    // 获取员工姓名
    getEmployeeName(employeeId) {
      const employee = this.employeeList.find(emp => emp.id === employeeId)
      return employee ? employee.name : ''
    }
  }
}
</script>

<style lang="less" scoped>
.employee-option {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .employee-name {
    font-weight: 500;
  }
  
  .employee-role {
    font-size: 12px;
    color: #666;
    margin-left: auto;
  }
}

.shift-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .shift-name {
    font-weight: 500;
  }
  
  .shift-time {
    font-size: 12px;
    color: #666;
  }
}

.repeat-settings {
  margin-top: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 6px;
}

.conflict-alert {
  margin-top: 16px;
  
  .conflict-list {
    margin: 8px 0 0 0;
    padding-left: 20px;
    
    li {
      margin-bottom: 4px;
      font-size: 12px;
    }
  }
}
</style>
