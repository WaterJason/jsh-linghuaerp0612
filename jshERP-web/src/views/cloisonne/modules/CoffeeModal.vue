<template>
  <a-modal
    title="咖啡店管理"
    :visible="visible"
    :width="800"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
  >
    <a-tabs v-model="activeTab" type="card">
      <!-- 商品管理 -->
      <a-tab-pane key="products" tab="商品管理">
        <a-form-model
          ref="productForm"
          :model="productForm"
          :rules="productRules"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 16 }"
        >
          <a-form-model-item label="商品名称" prop="name">
            <a-input v-model="productForm.name" placeholder="请输入商品名称" />
          </a-form-model-item>
          
          <a-form-model-item label="商品分类" prop="category">
            <a-select v-model="productForm.category" placeholder="请选择分类">
              <a-select-option value="coffee">咖啡</a-select-option>
              <a-select-option value="tea">茶饮</a-select-option>
              <a-select-option value="dessert">甜品</a-select-option>
              <a-select-option value="snack">小食</a-select-option>
            </a-select>
          </a-form-model-item>
          
          <a-form-model-item label="价格" prop="price">
            <a-input-number
              v-model="productForm.price"
              :min="0"
              :precision="2"
              placeholder="请输入价格"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="库存" prop="stock">
            <a-input-number
              v-model="productForm.stock"
              :min="0"
              placeholder="请输入库存数量"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="状态" prop="status">
            <a-select v-model="productForm.status" placeholder="请选择状态">
              <a-select-option value="available">可售</a-select-option>
              <a-select-option value="unavailable">停售</a-select-option>
              <a-select-option value="out_of_stock">缺货</a-select-option>
            </a-select>
          </a-form-model-item>
          
          <a-form-model-item label="描述" prop="description">
            <a-textarea
              v-model="productForm.description"
              :rows="3"
              placeholder="请输入商品描述"
            />
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
      
      <!-- 销售记录 -->
      <a-tab-pane key="sales" tab="销售记录">
        <a-form-model
          ref="salesForm"
          :model="salesForm"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 16 }"
        >
          <a-form-model-item label="商品">
            <a-select v-model="salesForm.productId" placeholder="请选择商品">
              <a-select-option value="1">美式咖啡</a-select-option>
              <a-select-option value="2">拿铁咖啡</a-select-option>
              <a-select-option value="3">卡布奇诺</a-select-option>
            </a-select>
          </a-form-model-item>
          
          <a-form-model-item label="数量">
            <a-input-number
              v-model="salesForm.quantity"
              :min="1"
              placeholder="请输入销售数量"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="单价">
            <a-input-number
              v-model="salesForm.unitPrice"
              :min="0"
              :precision="2"
              placeholder="请输入单价"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="销售员">
            <a-select v-model="salesForm.salesperson" placeholder="请选择销售员">
              <a-select-option value="emp001">张三</a-select-option>
              <a-select-option value="emp002">李四</a-select-option>
              <a-select-option value="emp003">王五</a-select-option>
            </a-select>
          </a-form-model-item>
          
          <a-form-model-item label="支付方式">
            <a-select v-model="salesForm.paymentMethod" placeholder="请选择支付方式">
              <a-select-option value="cash">现金</a-select-option>
              <a-select-option value="wechat">微信支付</a-select-option>
              <a-select-option value="alipay">支付宝</a-select-option>
              <a-select-option value="card">银行卡</a-select-option>
            </a-select>
          </a-form-model-item>
          
          <a-form-model-item label="总金额">
            <span class="amount-display">¥{{ totalAmount.toFixed(2) }}</span>
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
      
      <!-- 库存管理 -->
      <a-tab-pane key="inventory" tab="库存管理">
        <a-form-model
          ref="inventoryForm"
          :model="inventoryForm"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 16 }"
        >
          <a-form-model-item label="操作类型">
            <a-radio-group v-model="inventoryForm.type">
              <a-radio value="in">入库</a-radio>
              <a-radio value="out">出库</a-radio>
              <a-radio value="adjust">调整</a-radio>
            </a-radio-group>
          </a-form-model-item>
          
          <a-form-model-item label="商品">
            <a-select v-model="inventoryForm.productId" placeholder="请选择商品">
              <a-select-option value="1">美式咖啡</a-select-option>
              <a-select-option value="2">拿铁咖啡</a-select-option>
              <a-select-option value="3">卡布奇诺</a-select-option>
            </a-select>
          </a-form-model-item>
          
          <a-form-model-item label="数量">
            <a-input-number
              v-model="inventoryForm.quantity"
              placeholder="请输入数量"
              style="width: 100%"
            />
          </a-form-model-item>
          
          <a-form-model-item label="备注">
            <a-textarea
              v-model="inventoryForm.remark"
              :rows="3"
              placeholder="请输入备注信息"
            />
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
    </a-tabs>
  </a-modal>
</template>

<script>
export default {
  name: 'CoffeeModal',
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
      activeTab: 'products',
      productForm: {
        id: null,
        name: '',
        category: '',
        price: 0,
        stock: 0,
        status: 'available',
        description: ''
      },
      salesForm: {
        productId: '',
        quantity: 1,
        unitPrice: 0,
        salesperson: '',
        paymentMethod: ''
      },
      inventoryForm: {
        type: 'in',
        productId: '',
        quantity: 0,
        remark: ''
      },
      productRules: {
        name: [
          { required: true, message: '请输入商品名称', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择分类', trigger: 'change' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    totalAmount() {
      return (this.salesForm.quantity || 0) * (this.salesForm.unitPrice || 0)
    }
  },
  methods: {
    handleOk() {
      this.confirmLoading = true
      setTimeout(() => {
        this.confirmLoading = false
        this.$emit('ok', {
          tab: this.activeTab,
          data: this.getCurrentFormData()
        })
        this.$message.success('操作成功')
      }, 1000)
    },
    getCurrentFormData() {
      switch (this.activeTab) {
        case 'products':
          return this.productForm
        case 'sales':
          return this.salesForm
        case 'inventory':
          return this.inventoryForm
        default:
          return {}
      }
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

.ant-form-item {
  margin-bottom: 16px;
}
</style>
