<template>
  <a-modal
    title="生产订单管理"
    :visible="visible"
    :width="900"
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
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="订单编号" prop="orderNo">
            <a-input v-model="form.orderNo" placeholder="请输入订单编号" />
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="产品名称" prop="productName">
            <a-input v-model="form.productName" placeholder="请输入产品名称" />
          </a-form-model-item>
        </a-col>
      </a-row>
      
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="产品类型" prop="productType">
            <a-select v-model="form.productType" placeholder="请选择产品类型">
              <a-select-option value="cloisonne">掐丝珐琅</a-select-option>
              <a-select-option value="ceramic">陶瓷</a-select-option>
              <a-select-option value="metal">金属工艺</a-select-option>
              <a-select-option value="other">其他</a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="生产数量" prop="quantity">
            <a-input-number
              v-model="form.quantity"
              :min="1"
              placeholder="请输入生产数量"
              style="width: 100%"
            />
          </a-form-model-item>
        </a-col>
      </a-row>
      
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="计划开始日期" prop="plannedStartDate">
            <a-date-picker
              v-model="form.plannedStartDate"
              format="YYYY-MM-DD"
              placeholder="请选择开始日期"
              style="width: 100%"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="计划完成日期" prop="plannedEndDate">
            <a-date-picker
              v-model="form.plannedEndDate"
              format="YYYY-MM-DD"
              placeholder="请选择完成日期"
              style="width: 100%"
            />
          </a-form-model-item>
        </a-col>
      </a-row>
      
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="负责人" prop="manager">
            <a-select v-model="form.manager" placeholder="请选择负责人">
              <a-select-option value="emp001">张三</a-select-option>
              <a-select-option value="emp002">李四</a-select-option>
              <a-select-option value="emp003">王五</a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="优先级" prop="priority">
            <a-select v-model="form.priority" placeholder="请选择优先级">
              <a-select-option value="high">高</a-select-option>
              <a-select-option value="medium">中</a-select-option>
              <a-select-option value="low">低</a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
      </a-row>
      
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="状态" prop="status">
            <a-select v-model="form.status" placeholder="请选择状态">
              <a-select-option value="pending">待开始</a-select-option>
              <a-select-option value="in_progress">进行中</a-select-option>
              <a-select-option value="completed">已完成</a-select-option>
              <a-select-option value="cancelled">已取消</a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="预算成本" prop="budgetCost">
            <a-input-number
              v-model="form.budgetCost"
              :min="0"
              :precision="2"
              placeholder="请输入预算成本"
              style="width: 100%"
            />
          </a-form-model-item>
        </a-col>
      </a-row>
      
      <a-form-model-item label="产品规格" prop="specifications">
        <a-textarea
          v-model="form.specifications"
          :rows="3"
          placeholder="请输入产品规格说明"
        />
      </a-form-model-item>
      
      <a-form-model-item label="生产要求" prop="requirements">
        <a-textarea
          v-model="form.requirements"
          :rows="3"
          placeholder="请输入生产要求"
        />
      </a-form-model-item>
      
      <a-form-model-item label="备注" prop="remark">
        <a-textarea
          v-model="form.remark"
          :rows="2"
          placeholder="请输入备注信息"
        />
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
export default {
  name: 'ProductionOrderModal',
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
        orderNo: '',
        productName: '',
        productType: '',
        quantity: 1,
        plannedStartDate: null,
        plannedEndDate: null,
        manager: '',
        priority: 'medium',
        status: 'pending',
        budgetCost: 0,
        specifications: '',
        requirements: '',
        remark: ''
      },
      rules: {
        orderNo: [
          { required: true, message: '请输入订单编号', trigger: 'blur' }
        ],
        productName: [
          { required: true, message: '请输入产品名称', trigger: 'blur' }
        ],
        productType: [
          { required: true, message: '请选择产品类型', trigger: 'change' }
        ],
        quantity: [
          { required: true, message: '请输入生产数量', trigger: 'blur' }
        ],
        plannedStartDate: [
          { required: true, message: '请选择开始日期', trigger: 'change' }
        ],
        plannedEndDate: [
          { required: true, message: '请选择完成日期', trigger: 'change' }
        ],
        manager: [
          { required: true, message: '请选择负责人', trigger: 'change' }
        ]
      }
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
        this.form = { ...this.record }
      } else {
        this.form = {
          id: null,
          orderNo: this.generateOrderNo(),
          productName: '',
          productType: '',
          quantity: 1,
          plannedStartDate: null,
          plannedEndDate: null,
          manager: '',
          priority: 'medium',
          status: 'pending',
          budgetCost: 0,
          specifications: '',
          requirements: '',
          remark: ''
        }
      }
    },
    generateOrderNo() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
      return `PO${year}${month}${day}${random}`
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true
          setTimeout(() => {
            this.confirmLoading = false
            this.$emit('ok', this.form)
            this.$message.success('生产订单保存成功')
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
.ant-form-item {
  margin-bottom: 16px;
}
</style>
