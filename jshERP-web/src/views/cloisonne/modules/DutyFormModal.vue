<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" :model="model" :rules="validatorRules" v-bind="layout">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item label="值班日期" prop="dutyDate">
              <a-date-picker
                v-model="model.dutyDate"
                placeholder="请选择值班日期"
                style="width: 100%"
                :disabled-date="disabledDate"
                format="YYYY-MM-DD"
                valueFormat="YYYY-MM-DD"
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="员工" prop="employeeId">
              <a-select
                v-model="model.employeeId"
                placeholder="请选择员工"
                style="width: 100%"
                show-search
                :filter-option="filterOption"
                @change="handleEmployeeChange"
              >
                <a-select-option
                  v-for="employee in employeeList"
                  :key="employee.id"
                  :value="employee.id"
                >
                  {{ employee.name }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        
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
                :rows="4"
                :maxLength="500"
                show-count
              />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
import { addDuty, editDuty, getDutyById, getEmployeeList, checkDutyConflict } from '@/api/cloisonne/duty'
import moment from 'moment'

export default {
  name: 'DutyFormModal',
  data() {
    return {
      title: "操作",
      width: 800,
      visible: false,
      model: {},
      layout: {
        labelCol: { span: 6 },
        wrapperCol: { span: 16 },
      },
      confirmLoading: false,
      validatorRules: {
        dutyDate: [{ required: true, message: '请选择值班日期!' }],
        employeeId: [{ required: true, message: '请选择员工!' }],
        shiftType: [{ required: true, message: '请选择班次类型!' }],
        startTime: [{ required: true, message: '请选择开始时间!' }],
        endTime: [{ required: true, message: '请选择结束时间!' }],
        status: [{ required: true, message: '请选择状态!' }]
      },
      url: {
        add: "/cloisonne/duty/add",
        edit: "/cloisonne/duty/edit",
        queryById: "/cloisonne/duty/queryById"
      },
      employeeList: []
    }
  },
  created() {
    // 备份model原始值
    this.modelDefault = JSON.parse(JSON.stringify(this.model))
    this.loadEmployeeList()
  },
  methods: {
    // 新增
    add(dateStr) {
      this.edit({})
      if (dateStr) {
        this.model.dutyDate = dateStr
      }
    },
    
    // 编辑
    edit(record) {
      this.$refs.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.title = record.id ? "编辑值班记录" : "新增值班记录"
      
      // 设置默认值
      if (!record.id) {
        this.model.status = 'normal'
        this.model.shiftType = '早班'
        this.model.dutyDate = this.model.dutyDate || moment().format('YYYY-MM-DD')
        this.handleShiftTypeChange('早班')
      }
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
    
    // 员工选择变化
    handleEmployeeChange(value) {
      const employee = this.employeeList.find(emp => emp.id === value)
      if (employee) {
        this.model.employeeName = employee.name
      }
    },
    
    // 班次类型变化
    handleShiftTypeChange(value) {
      // 根据班次类型设置默认时间
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
    
    // 禁用日期（可以根据需要禁用过去的日期）
    disabledDate(current) {
      // 可以根据业务需求禁用某些日期
      return false
    },
    
    // 员工筛选
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    
    // 提交表单
    submitForm() {
      const that = this
      // 触发表单验证
      this.$refs.form.validate((valid) => {
        if (valid) {
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          
          if (!this.model.id) {
            httpurl = this.url.add
            method = 'post'
          } else {
            httpurl = this.url.edit
            method = 'put'
          }
          
          let formData = Object.assign({}, this.model)
          
          // 检查冲突
          this.checkConflict(formData).then(() => {
            // 调用API
            const apiCall = method === 'post' ? addDuty(formData) : editDuty(formData)
            
            apiCall.then((res) => {
              if (res.success) {
                that.$message.success(res.message || '操作成功')
                that.$emit('ok')
                that.handleCancel()
              } else {
                that.$message.warning(res.message || '操作失败')
              }
            }).catch(err => {
              that.$message.error('操作失败：' + err.message)
            }).finally(() => {
              that.confirmLoading = false
            })
          }).catch(err => {
            that.confirmLoading = false
            that.$message.warning(err.message || '存在冲突，无法保存')
          })
        }
      })
    },
    
    // 检查冲突
    checkConflict(formData) {
      return new Promise((resolve, reject) => {
        checkDutyConflict({
          employeeId: formData.employeeId,
          dutyDate: formData.dutyDate,
          shiftType: formData.shiftType,
          excludeId: formData.id
        }).then(res => {
          if (res.success && !res.result.hasConflict) {
            resolve()
          } else {
            reject(new Error(res.result.message || '存在排班冲突'))
          }
        }).catch(err => {
          reject(err)
        })
      })
    },
    
    handleOk() {
      this.submitForm()
    },
    
    handleCancel() {
      this.$emit('close')
      this.visible = false
      this.model = {}
    }
  }
}
</script>

<style scoped>
/* 可以添加自定义样式 */
</style>
