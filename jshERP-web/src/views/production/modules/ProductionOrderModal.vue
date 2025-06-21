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
      <a-form :form="form" layout="vertical">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="订单号">
              <a-input
                v-decorator="['orderNo', { rules: [{ required: true, message: '请输入订单号!' }] }]"
                placeholder="请输入订单号"
                :disabled="!!model.id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="销售订单ID">
              <a-input-number
                v-decorator="['salesOrderId']"
                placeholder="请输入销售订单ID"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="产品ID">
              <a-input-number
                v-decorator="['materialId', { rules: [{ required: true, message: '请输入产品ID!' }] }]"
                placeholder="请输入产品ID"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="数量">
              <a-input-number
                v-decorator="['quantity', { rules: [{ required: true, message: '请输入数量!' }] }]"
                placeholder="请输入数量"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="状态">
              <a-select
                v-decorator="['status', { initialValue: 'PENDING' }]"
                placeholder="请选择状态"
              >
                <a-select-option value="PENDING">待开始</a-select-option>
                <a-select-option value="IN_PROGRESS">进行中</a-select-option>
                <a-select-option value="COMPLETED">已完成</a-select-option>
                <a-select-option value="CANCELLED">已取消</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="交付期限">
              <a-date-picker
                v-decorator="['deliveryDate']"
                placeholder="请选择交付期限"
                style="width: 100%"
                show-time
                format="YYYY-MM-DD HH:mm:ss"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="总成本">
              <a-input-number
                v-decorator="['totalCost']"
                placeholder="请输入总成本"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="物料成本">
              <a-input-number
                v-decorator="['materialCost']"
                placeholder="请输入物料成本"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="人工成本">
              <a-input-number
                v-decorator="['laborCost']"
                placeholder="请输入人工成本"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="备注">
          <a-textarea
            v-decorator="['remark']"
            placeholder="请输入备注"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { postAction, putAction } from '@/api/manage'

export default {
  name: 'ProductionOrderModal',
  data() {
    return {
      title: '',
      visible: false,
      model: {},
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {}
    }
  },
  
  methods: {
    add() {
      this.edit({})
    },
    
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      
      this.$nextTick(() => {
        this.form.setFieldsValue({
          orderNo: this.model.orderNo,
          salesOrderId: this.model.salesOrderId,
          materialId: this.model.materialId,
          quantity: this.model.quantity,
          status: this.model.status || 'PENDING',
          deliveryDate: this.model.deliveryDate ? this.$moment(this.model.deliveryDate) : null,
          totalCost: this.model.totalCost,
          materialCost: this.model.materialCost,
          laborCost: this.model.laborCost,
          remark: this.model.remark
        })
      })
    },
    
    detail(record) {
      this.edit(record)
      // 详情模式下禁用所有表单项
      this.$nextTick(() => {
        const formItems = this.$el.querySelectorAll('.ant-form-item input, .ant-form-item textarea, .ant-form-item .ant-select, .ant-form-item .ant-input-number')
        formItems.forEach(item => {
          item.disabled = true
        })
      })
    },
    
    handleOk() {
      const that = this
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          
          // 处理日期格式
          if (values.deliveryDate) {
            values.deliveryDate = values.deliveryDate.format('YYYY-MM-DD HH:mm:ss')
          }
          
          let httpUrl = ''
          let method = ''
          
          if (!this.model.id) {
            httpUrl = '/production/add'
            method = postAction
          } else {
            httpUrl = '/production/update'
            method = putAction
            values.id = this.model.id
          }
          
          method(httpUrl, values).then(res => {
            if (res.code === 200) {
              that.$message.success('操作成功!')
              that.$emit('ok')
              that.handleCancel()
            } else {
              that.$message.error('操作失败：' + res.data.message)
            }
          }).catch(err => {
            that.$message.error('操作失败')
            console.error(err)
          }).finally(() => {
            that.confirmLoading = false
          })
        }
      })
    },
    
    handleCancel() {
      this.visible = false
      this.form.resetFields()
      this.model = {}
    }
  }
}
</script>

<style scoped>
</style>
