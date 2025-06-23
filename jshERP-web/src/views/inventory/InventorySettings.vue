<template>
  <div class="inventory-settings">
    <a-card :bordered="false" title="盘点设置">
      <a-tabs default-active-key="1">
        <!-- 基础设置 -->
        <a-tab-pane key="1" tab="基础设置">
          <a-form :form="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
            <a-form-item label="盘点单号前缀">
              <a-input
                v-decorator="['checkNoPrefix', { initialValue: 'PD', rules: [{ required: true, message: '请输入盘点单号前缀' }] }]"
                placeholder="请输入盘点单号前缀"
              />
            </a-form-item>
            
            <a-form-item label="自动生成单号">
              <a-switch
                v-decorator="['autoGenerateNo', { initialValue: true, valuePropName: 'checked' }]"
                checked-children="开"
                un-checked-children="关"
              />
              <span style="margin-left: 8px; color: #999;">开启后系统将自动生成盘点单号</span>
            </a-form-item>
            
            <a-form-item label="盘点差异阈值">
              <a-input-number
                v-decorator="['differenceThreshold', { initialValue: 5, rules: [{ required: true, message: '请输入差异阈值' }] }]"
                :min="0"
                :max="100"
                :step="0.1"
                style="width: 200px"
              />
              <span style="margin-left: 8px;">%（超过此阈值将标记为异常）</span>
            </a-form-item>
            
            <a-form-item label="盘点提醒">
              <a-switch
                v-decorator="['enableReminder', { initialValue: true, valuePropName: 'checked' }]"
                checked-children="开"
                un-checked-children="关"
              />
              <span style="margin-left: 8px; color: #999;">开启后将在盘点到期前提醒</span>
            </a-form-item>
            
            <a-form-item label="提醒提前天数">
              <a-input-number
                v-decorator="['reminderDays', { initialValue: 3, rules: [{ required: true, message: '请输入提醒天数' }] }]"
                :min="1"
                :max="30"
                style="width: 200px"
              />
              <span style="margin-left: 8px;">天</span>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <!-- 盘点规则 -->
        <a-tab-pane key="2" tab="盘点规则">
          <a-form :form="ruleForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
            <a-form-item label="盘点周期">
              <a-select
                v-decorator="['checkCycle', { initialValue: 'MONTHLY', rules: [{ required: true, message: '请选择盘点周期' }] }]"
                placeholder="请选择盘点周期"
              >
                <a-select-option value="WEEKLY">每周</a-select-option>
                <a-select-option value="MONTHLY">每月</a-select-option>
                <a-select-option value="QUARTERLY">每季度</a-select-option>
                <a-select-option value="YEARLY">每年</a-select-option>
              </a-select>
            </a-form-item>
            
            <a-form-item label="盘点方式">
              <a-radio-group
                v-decorator="['checkMethod', { initialValue: 'FULL', rules: [{ required: true, message: '请选择盘点方式' }] }]"
              >
                <a-radio value="FULL">全盘</a-radio>
                <a-radio value="CYCLE">循环盘点</a-radio>
                <a-radio value="SPOT">抽盘</a-radio>
              </a-radio-group>
            </a-form-item>
            
            <a-form-item label="盘点范围">
              <a-checkbox-group
                v-decorator="['checkScope', { initialValue: ['MATERIAL', 'PRODUCT'], rules: [{ required: true, message: '请选择盘点范围' }] }]"
              >
                <a-checkbox value="MATERIAL">原材料</a-checkbox>
                <a-checkbox value="PRODUCT">产成品</a-checkbox>
                <a-checkbox value="SEMI_PRODUCT">半成品</a-checkbox>
                <a-checkbox value="CONSUMABLE">消耗品</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            
            <a-form-item label="零库存商品">
              <a-radio-group
                v-decorator="['zeroStockHandle', { initialValue: 'INCLUDE', rules: [{ required: true, message: '请选择零库存处理方式' }] }]"
              >
                <a-radio value="INCLUDE">包含</a-radio>
                <a-radio value="EXCLUDE">排除</a-radio>
              </a-radio-group>
            </a-form-item>
            
            <a-form-item label="负库存商品">
              <a-radio-group
                v-decorator="['negativeStockHandle', { initialValue: 'INCLUDE', rules: [{ required: true, message: '请选择负库存处理方式' }] }]"
              >
                <a-radio value="INCLUDE">包含</a-radio>
                <a-radio value="EXCLUDE">排除</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <!-- 权限设置 -->
        <a-tab-pane key="3" tab="权限设置">
          <a-form :form="permissionForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
            <a-form-item label="盘点发起人">
              <a-select
                v-decorator="['initiators', { initialValue: [], rules: [{ required: true, message: '请选择盘点发起人' }] }]"
                mode="multiple"
                placeholder="请选择可以发起盘点的用户"
              >
                <a-select-option value="admin">管理员</a-select-option>
                <a-select-option value="warehouse">仓库管理员</a-select-option>
                <a-select-option value="finance">财务人员</a-select-option>
              </a-select>
            </a-form-item>
            
            <a-form-item label="盘点执行人">
              <a-select
                v-decorator="['executors', { initialValue: [], rules: [{ required: true, message: '请选择盘点执行人' }] }]"
                mode="multiple"
                placeholder="请选择可以执行盘点的用户"
              >
                <a-select-option value="warehouse">仓库管理员</a-select-option>
                <a-select-option value="operator">仓库操作员</a-select-option>
              </a-select>
            </a-form-item>
            
            <a-form-item label="盘点审核人">
              <a-select
                v-decorator="['reviewers', { initialValue: [], rules: [{ required: true, message: '请选择盘点审核人' }] }]"
                mode="multiple"
                placeholder="请选择可以审核盘点的用户"
              >
                <a-select-option value="admin">管理员</a-select-option>
                <a-select-option value="finance">财务人员</a-select-option>
              </a-select>
            </a-form-item>
            
            <a-form-item label="需要审核">
              <a-switch
                v-decorator="['requireReview', { initialValue: true, valuePropName: 'checked' }]"
                checked-children="是"
                un-checked-children="否"
              />
              <span style="margin-left: 8px; color: #999;">开启后盘点结果需要审核才能生效</span>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>

      <!-- 操作按钮 -->
      <div style="text-align: center; margin-top: 24px;">
        <a-button type="primary" @click="handleSave" :loading="saving">保存设置</a-button>
        <a-button style="margin-left: 8px;" @click="handleReset">重置</a-button>
      </div>
    </a-card>
  </div>
</template>

<script>
import { getAction, postAction } from '@/api/manage'

export default {
  name: 'InventorySettings',
  data() {
    return {
      form: this.$form.createForm(this),
      ruleForm: this.$form.createForm(this),
      permissionForm: this.$form.createForm(this),
      saving: false
    }
  },
  
  mounted() {
    this.loadSettings()
  },
  
  methods: {
    // 加载设置
    loadSettings() {
      // 模拟加载设置数据
      this.$message.info('盘点设置功能开发中...')
    },
    
    // 保存设置
    handleSave() {
      // 验证所有表单
      Promise.all([
        this.validateForm(this.form),
        this.validateForm(this.ruleForm),
        this.validateForm(this.permissionForm)
      ]).then(values => {
        const [basicSettings, ruleSettings, permissionSettings] = values
        
        this.saving = true
        
        // 模拟保存
        setTimeout(() => {
          this.saving = false
          this.$message.success('设置保存成功')
        }, 1000)
        
      }).catch(error => {
        console.log('表单验证失败:', error)
      })
    },
    
    // 重置设置
    handleReset() {
      this.form.resetFields()
      this.ruleForm.resetFields()
      this.permissionForm.resetFields()
      this.$message.info('设置已重置')
    },
    
    // 验证表单
    validateForm(form) {
      return new Promise((resolve, reject) => {
        form.validateFields((err, values) => {
          if (err) {
            reject(err)
          } else {
            resolve(values)
          }
        })
      })
    }
  }
}
</script>

<style scoped>
.inventory-settings {
  padding: 24px;
}

.ant-form-item {
  margin-bottom: 24px;
}
</style>
