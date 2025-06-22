<template>
  <a-modal
    :visible="visible"
    :title="modalTitle"
    width="500px"
    @ok="handleSubmit"
    @cancel="handleCancel"
    :confirmLoading="loading">
    
    <a-form :form="form" layout="vertical">
      <a-form-item label="排班日期">
        <a-date-picker
          v-decorator="['scheduleDate', { 
            rules: [{ required: true, message: '请选择排班日期' }],
            initialValue: selectedDateMoment
          }]"
          style="width: 100%"
          :disabled="isEdit"
          placeholder="请选择排班日期" />
      </a-form-item>

      <a-form-item label="选择班次">
        <a-select
          v-decorator="['shiftId', { 
            rules: [{ required: true, message: '请选择班次' }]
          }]"
          placeholder="请选择班次"
          style="width: 100%">
          <a-select-option 
            v-for="shift in shiftOptions" 
            :key="shift.value" 
            :value="shift.value">
            <div class="shift-option">
              <span class="shift-name">{{ shift.label }}</span>
              <span class="shift-time">{{ shift.displayName }}</span>
            </div>
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="选择人员">
        <a-select
          v-decorator="['userId', { 
            rules: [{ required: true, message: '请选择人员' }]
          }]"
          placeholder="请选择人员"
          style="width: 100%"
          showSearch
          :filterOption="filterUserOption">
          <a-select-option 
            v-for="user in userOptions" 
            :key="user.id" 
            :value="user.id">
            <div class="user-option">
              <a-avatar size="small" :src="user.avatar" style="margin-right: 8px;">
                {{ user.name.charAt(0) }}
              </a-avatar>
              <span>{{ user.name }}</span>
              <span class="user-dept" v-if="user.department">（{{ user.department }}）</span>
            </div>
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="排班状态">
        <a-select
          v-decorator="['status', { 
            initialValue: 'SCHEDULED'
          }]"
          style="width: 100%">
          <a-select-option value="SCHEDULED">
            <a-tag color="blue">已排班</a-tag>
          </a-select-option>
          <a-select-option value="CONFIRMED">
            <a-tag color="green">已确认</a-tag>
          </a-select-option>
          <a-select-option value="CANCELLED">
            <a-tag color="red">已取消</a-tag>
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="备注" :required="false">
        <a-textarea
          v-decorator="['notes']"
          placeholder="请输入备注信息"
          :rows="3"
          :maxLength="200"
          showCount />
      </a-form-item>

      <!-- 编辑模式下显示更多信息 -->
      <template v-if="isEdit">
        <a-form-item label="工作时长（小时）" :required="false">
          <a-input-number
            v-decorator="['workHours']"
            :min="0"
            :max="24"
            :step="0.5"
            style="width: 100%"
            placeholder="请输入实际工作时长" />
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="签到时间" :required="false">
              <a-time-picker
                v-decorator="['checkInTime']"
                style="width: 100%"
                format="HH:mm"
                placeholder="选择签到时间" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="签退时间" :required="false">
              <a-time-picker
                v-decorator="['checkOutTime']"
                style="width: 100%"
                format="HH:mm"
                placeholder="选择签退时间" />
            </a-form-item>
          </a-col>
        </a-row>
      </template>
    </a-form>

    <!-- 冲突提示 -->
    <a-alert
      v-if="conflictWarning"
      :message="conflictWarning"
      type="warning"
      showIcon
      style="margin-top: 16px;" />
  </a-modal>
</template>

<script>
import moment from 'moment'
import { getAction, postAction } from '@/api/manage'

export default {
  name: 'ScheduleAssignmentModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    selectedDate: {
      type: String,
      default: null
    },
    assignmentData: {
      type: Object,
      default: null
    },
    shiftOptions: {
      type: Array,
      default: () => []
    },
    userOptions: {
      type: Array,
      default: () => []
    }
  },
  
  data() {
    return {
      form: this.$form.createForm(this),
      loading: false,
      conflictWarning: null
    }
  },
  
  computed: {
    isEdit() {
      return !!this.assignmentData
    },
    
    modalTitle() {
      return this.isEdit ? '编辑排班' : '新增排班'
    },
    
    selectedDateMoment() {
      return this.selectedDate ? moment(this.selectedDate) : moment()
    }
  },
  
  watch: {
    visible(val) {
      if (val) {
        this.initForm()
      } else {
        this.resetForm()
      }
    },
    
    assignmentData: {
      handler(val) {
        if (val && this.visible) {
          this.initForm()
        }
      },
      deep: true
    }
  },
  
  methods: {
    // ==================== 表单初始化 ====================
    
    initForm() {
      this.$nextTick(() => {
        if (this.isEdit && this.assignmentData) {
          // 编辑模式：填充现有数据
          this.form.setFieldsValue({
            scheduleDate: moment(this.assignmentData.scheduleDate),
            shiftId: this.assignmentData.shiftId,
            userId: this.assignmentData.userId,
            status: this.assignmentData.status,
            notes: this.assignmentData.notes,
            workHours: this.assignmentData.workHours,
            checkInTime: this.assignmentData.checkInTime ? moment(this.assignmentData.checkInTime, 'HH:mm') : null,
            checkOutTime: this.assignmentData.checkOutTime ? moment(this.assignmentData.checkOutTime, 'HH:mm') : null
          })
        } else {
          // 新增模式：设置默认值
          this.form.setFieldsValue({
            scheduleDate: this.selectedDateMoment,
            status: 'SCHEDULED'
          })
        }
      })
    },
    
    resetForm() {
      this.form.resetFields()
      this.conflictWarning = null
    },
    
    // ==================== 表单提交 ====================
    
    handleSubmit() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.submitForm(values)
        }
      })
    },
    
    async submitForm(values) {
      try {
        this.loading = true
        
        // 准备提交数据
        const submitData = {
          scheduleDate: values.scheduleDate.format('YYYY-MM-DD'),
          shiftId: values.shiftId,
          userId: values.userId,
          userName: this.getUserName(values.userId),
          status: values.status,
          notes: values.notes || null
        }
        
        // 编辑模式下添加额外字段
        if (this.isEdit) {
          submitData.id = this.assignmentData.id
          submitData.workHours = values.workHours || null
          submitData.checkInTime = values.checkInTime ? values.checkInTime.format('HH:mm:ss') : null
          submitData.checkOutTime = values.checkOutTime ? values.checkOutTime.format('HH:mm:ss') : null
        }
        
        // 检查冲突
        const hasConflict = await this.checkConflict(submitData)
        if (hasConflict) {
          return
        }
        
        // 提交数据
        const apiUrl = this.isEdit 
          ? '/api/plugin/calendar-schedule/assignment/update'
          : '/api/plugin/calendar-schedule/assignment/add'
        
        const res = await postAction(apiUrl, submitData)
        
        if (res.code === 200) {
          this.$message.success(this.isEdit ? '更新成功' : '新增成功')
          this.$emit('success', res.data)
          this.handleCancel()
        } else {
          this.$message.error(res.message || '操作失败')
        }
        
      } catch (error) {
        console.error('提交排班数据失败:', error)
        this.$message.error('操作失败')
      } finally {
        this.loading = false
      }
    },
    
    // ==================== 冲突检查 ====================
    
    async checkConflict(data) {
      try {
        // 检查同用户同日期同班次是否已存在
        const params = {
          scheduleDate: data.scheduleDate,
          userId: data.userId,
          shiftId: data.shiftId
        }
        
        if (this.isEdit) {
          params.excludeId = data.id
        }
        
        // TODO: 实现冲突检查API
        // const res = await getAction('/api/plugin/calendar-schedule/assignment/check-conflict', params)
        
        // 临时模拟冲突检查
        const hasConflict = false
        
        if (hasConflict) {
          this.conflictWarning = '该用户在此日期此班次已有排班记录，请检查后重新选择'
          return true
        } else {
          this.conflictWarning = null
          return false
        }
        
      } catch (error) {
        console.error('检查冲突失败:', error)
        return false
      }
    },
    
    // ==================== 工具方法 ====================
    
    handleCancel() {
      this.$emit('update:visible', false)
    },
    
    getUserName(userId) {
      const user = this.userOptions.find(u => u.id === userId)
      return user ? user.name : ''
    },
    
    filterUserOption(input, option) {
      const user = this.userOptions.find(u => u.id === option.key)
      if (!user) return false
      
      const searchText = input.toLowerCase()
      return user.name.toLowerCase().includes(searchText) ||
             (user.department && user.department.toLowerCase().includes(searchText))
    },
    
    // ==================== 表单验证 ====================
    
    validateWorkHours(rule, value, callback) {
      if (value && (value < 0 || value > 24)) {
        callback(new Error('工作时长必须在0-24小时之间'))
      } else {
        callback()
      }
    },
    
    validateTimeRange(rule, value, callback) {
      const checkInTime = this.form.getFieldValue('checkInTime')
      const checkOutTime = this.form.getFieldValue('checkOutTime')
      
      if (checkInTime && checkOutTime && checkInTime.isAfter(checkOutTime)) {
        callback(new Error('签退时间必须晚于签到时间'))
      } else {
        callback()
      }
    }
  }
}
</script>

<style scoped>
.shift-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.shift-name {
  font-weight: 500;
}

.shift-time {
  color: #666;
  font-size: 12px;
}

.user-option {
  display: flex;
  align-items: center;
}

.user-dept {
  color: #999;
  font-size: 12px;
  margin-left: 4px;
}

.ant-form-item {
  margin-bottom: 16px;
}

.ant-alert {
  border-radius: 4px;
}
</style>
