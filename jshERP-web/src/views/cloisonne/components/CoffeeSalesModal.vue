<template>
  <a-modal
    title="咖啡销售记录"
    :visible="visible"
    :width="700"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
  >
    <a-form-model
      ref="form"
      :model="form"
      :rules="rules"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
    >
      <a-form-model-item label="商品名称" prop="productName">
        <a-select v-model="form.productName" placeholder="请选择咖啡商品">
          <a-select-option value="americano">美式咖啡</a-select-option>
          <a-select-option value="latte">拿铁咖啡</a-select-option>
          <a-select-option value="cappuccino">卡布奇诺</a-select-option>
          <a-select-option value="mocha">摩卡咖啡</a-select-option>
          <a-select-option value="espresso">意式浓缩</a-select-option>
        </a-select>
      </a-form-model-item>
      
      <a-form-model-item label="销售数量" prop="quantity">
        <a-input-number
          v-model="form.quantity"
          :min="1"
          :max="100"
          placeholder="请输入销售数量"
          style="width: 100%"
        />
      </a-form-model-item>
      
      <a-form-model-item label="单价" prop="unitPrice">
        <a-input-number
          v-model="form.unitPrice"
          :min="0"
          :precision="2"
          placeholder="请输入单价"
          style="width: 100%"
        />
      </a-form-model-item>
      
      <a-form-model-item label="销售员" prop="salesperson">
        <a-select v-model="form.salesperson" placeholder="请选择销售员">
          <a-select-option value="emp001">张三</a-select-option>
          <a-select-option value="emp002">李四</a-select-option>
          <a-select-option value="emp003">王五</a-select-option>
        </a-select>
      </a-form-model-item>
      
      <a-form-model-item label="销售时间" prop="saleTime">
        <a-date-picker
          v-model="form.saleTime"
          show-time
          format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择销售时间"
          style="width: 100%"
        />
      </a-form-model-item>
      
      <a-form-model-item label="支付方式" prop="paymentMethod">
        <a-select v-model="form.paymentMethod" placeholder="请选择支付方式">
          <a-select-option value="cash">现金</a-select-option>
          <a-select-option value="wechat">微信支付</a-select-option>
          <a-select-option value="alipay">支付宝</a-select-option>
          <a-select-option value="card">银行卡</a-select-option>
        </a-select>
      </a-form-model-item>
      
      <a-form-model-item label="客户类型" prop="customerType">
        <a-radio-group v-model="form.customerType">
          <a-radio value="regular">普通客户</a-radio>
          <a-radio value="member">会员客户</a-radio>
          <a-radio value="vip">VIP客户</a-radio>
        </a-radio-group>
      </a-form-model-item>
      
      <a-form-model-item label="折扣" prop="discount">
        <a-input-number
          v-model="form.discount"
          :min="0"
          :max="1"
          :precision="2"
          placeholder="请输入折扣(0-1)"
          style="width: 100%"
        />
      </a-form-model-item>
      
      <a-form-model-item label="备注" prop="remark">
        <a-textarea
          v-model="form.remark"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </a-form-model-item>
      
      <!-- 计算结果显示 -->
      <a-form-model-item label="小计金额">
        <span class="amount-display">¥{{ subtotalAmount.toFixed(2) }}</span>
      </a-form-model-item>
      
      <a-form-model-item label="折扣金额">
        <span class="amount-display discount-amount">-¥{{ discountAmount.toFixed(2) }}</span>
      </a-form-model-item>
      
      <a-form-model-item label="实付金额">
        <span class="amount-display total-amount">¥{{ totalAmount.toFixed(2) }}</span>
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'CoffeeSalesModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    record: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      confirmLoading: false,
      form: {
        id: null,
        productName: '',
        quantity: 1,
        unitPrice: 0,
        salesperson: '',
        saleTime: null,
        paymentMethod: '',
        customerType: 'regular',
        discount: 1,
        remark: ''
      },
      rules: {
        productName: [
          { required: true, message: '请选择咖啡商品', trigger: 'change' }
        ],
        quantity: [
          { required: true, message: '请输入销售数量', trigger: 'blur' }
        ],
        unitPrice: [
          { required: true, message: '请输入单价', trigger: 'blur' }
        ],
        salesperson: [
          { required: true, message: '请选择销售员', trigger: 'change' }
        ],
        saleTime: [
          { required: true, message: '请选择销售时间', trigger: 'change' }
        ],
        paymentMethod: [
          { required: true, message: '请选择支付方式', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    subtotalAmount() {
      return (this.form.quantity || 0) * (this.form.unitPrice || 0)
    },
    discountAmount() {
      return this.subtotalAmount * (1 - (this.form.discount || 1))
    },
    totalAmount() {
      return this.subtotalAmount - this.discountAmount
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.initForm()
      }
    }
  },
  methods: {
    initForm() {
      if (this.record && this.record.id) {
        // 编辑模式
        this.form = { ...this.record }
        if (this.form.saleTime) {
          this.form.saleTime = dayjs(this.form.saleTime)
        }
      } else {
        // 新增模式
        this.form = {
          id: null,
          productName: '',
          quantity: 1,
          unitPrice: 0,
          salesperson: '',
          saleTime: dayjs(),
          paymentMethod: '',
          customerType: 'regular',
          discount: 1,
          remark: ''
        }
      }
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true
          
          const formData = {
            ...this.form,
            saleTime: this.form.saleTime ? this.form.saleTime.format('YYYY-MM-DD HH:mm:ss') : null,
            subtotalAmount: this.subtotalAmount,
            discountAmount: this.discountAmount,
            totalAmount: this.totalAmount
          }
          
          // 这里应该调用API保存数据
          setTimeout(() => {
            this.confirmLoading = false
            this.$emit('ok', formData)
            this.$message.success('销售记录保存成功')
          }, 1000)
        }
      })
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.amount-display {
  font-size: 16px;
  font-weight: bold;
  color: #1890ff;
}

.discount-amount {
  color: #f5222d;
}

.total-amount {
  color: #52c41a;
  font-size: 18px;
}

.ant-form-item {
  margin-bottom: 16px;
}
</style>
