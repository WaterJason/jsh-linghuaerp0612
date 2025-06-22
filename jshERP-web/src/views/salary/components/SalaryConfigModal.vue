<template>
  <a-modal
    title="薪资配置设置"
    :visible="visible"
    :width="900"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
  >
    <a-tabs v-model="activeTab" type="card">
      <!-- 基础配置 -->
      <a-tab-pane key="basic" tab="基础配置">
        <a-form-model
          ref="basicForm"
          :model="basicConfig"
          :rules="basicRules"
          :label-col="{ span: 8 }"
          :wrapper-col="{ span: 14 }"
        >
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-model-item label="最低工资标准" prop="minWage">
                <a-input-number
                  v-model="basicConfig.minWage"
                  :min="0"
                  :precision="2"
                  placeholder="请输入最低工资标准"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item label="加班费倍率" prop="overtimeRate">
                <a-input-number
                  v-model="basicConfig.overtimeRate"
                  :min="1"
                  :max="5"
                  :precision="1"
                  placeholder="请输入加班费倍率"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-model-item label="社保缴费比例" prop="socialInsuranceRate">
                <a-input-number
                  v-model="basicConfig.socialInsuranceRate"
                  :min="0"
                  :max="1"
                  :precision="3"
                  placeholder="请输入社保缴费比例"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item label="公积金缴费比例" prop="housingFundRate">
                <a-input-number
                  v-model="basicConfig.housingFundRate"
                  :min="0"
                  :max="1"
                  :precision="3"
                  placeholder="请输入公积金缴费比例"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </a-tab-pane>
      
      <!-- 提成配置 -->
      <a-tab-pane key="commission" tab="提成配置">
        <a-form-model
          ref="commissionForm"
          :model="commissionConfig"
          :label-col="{ span: 8 }"
          :wrapper-col="{ span: 14 }"
        >
          <a-form-model-item label="销售提成比例">
            <a-input-number
              v-model="commissionConfig.salesCommissionRate"
              :min="0"
              :max="1"
              :precision="3"
              placeholder="请输入销售提成比例"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="生产提成比例">
            <a-input-number
              v-model="commissionConfig.productionCommissionRate"
              :min="0"
              :max="1"
              :precision="3"
              placeholder="请输入生产提成比例"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="培训提成比例">
            <a-input-number
              v-model="commissionConfig.trainingCommissionRate"
              :min="0"
              :max="1"
              :precision="3"
              placeholder="请输入培训提成比例"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="业务开发费比例">
            <a-input-number
              v-model="commissionConfig.businessDevelopmentRate"
              :min="0"
              :max="1"
              :precision="3"
              placeholder="请输入业务开发费比例"
              style="width: 100%"
            />
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
      
      <!-- 考勤配置 -->
      <a-tab-pane key="attendance" tab="考勤配置">
        <a-form-model
          ref="attendanceForm"
          :model="attendanceConfig"
          :label-col="{ span: 8 }"
          :wrapper-col="{ span: 14 }"
        >
          <a-form-model-item label="标准工作时长(小时/天)">
            <a-input-number
              v-model="attendanceConfig.standardWorkHours"
              :min="1"
              :max="24"
              placeholder="请输入标准工作时长"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="迟到扣款(元/次)">
            <a-input-number
              v-model="attendanceConfig.lateDeduction"
              :min="0"
              :precision="2"
              placeholder="请输入迟到扣款金额"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="早退扣款(元/次)">
            <a-input-number
              v-model="attendanceConfig.earlyLeaveDeduction"
              :min="0"
              :precision="2"
              placeholder="请输入早退扣款金额"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="旷工扣款(元/天)">
            <a-input-number
              v-model="attendanceConfig.absenceDeduction"
              :min="0"
              :precision="2"
              placeholder="请输入旷工扣款金额"
              style="width: 100%"
            />
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
    </a-tabs>
  </a-modal>
</template>

<script>
export default {
  name: 'SalaryConfigModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      confirmLoading: false,
      activeTab: 'basic',
      basicConfig: {
        minWage: 2320,
        overtimeRate: 1.5,
        socialInsuranceRate: 0.105,
        housingFundRate: 0.12
      },
      commissionConfig: {
        salesCommissionRate: 0.05,
        productionCommissionRate: 0.03,
        trainingCommissionRate: 0.02,
        businessDevelopmentRate: 0.01
      },
      attendanceConfig: {
        standardWorkHours: 8,
        lateDeduction: 10,
        earlyLeaveDeduction: 10,
        absenceDeduction: 100
      },
      basicRules: {
        minWage: [
          { required: true, message: '请输入最低工资标准', trigger: 'blur' }
        ],
        overtimeRate: [
          { required: true, message: '请输入加班费倍率', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleOk() {
      // 验证所有表单
      const forms = ['basicForm', 'commissionForm', 'attendanceForm']
      let allValid = true
      
      forms.forEach(formRef => {
        if (this.$refs[formRef]) {
          this.$refs[formRef].validate(valid => {
            if (!valid) {
              allValid = false
            }
          })
        }
      })
      
      if (allValid) {
        this.confirmLoading = true
        // 这里应该调用API保存配置
        setTimeout(() => {
          this.confirmLoading = false
          this.$emit('ok', {
            basic: this.basicConfig,
            commission: this.commissionConfig,
            attendance: this.attendanceConfig
          })
          this.$message.success('配置保存成功')
        }, 1000)
      }
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.ant-form-item {
  margin-bottom: 16px;
}
</style>
