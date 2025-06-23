<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item label="制作单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input 
                v-decorator="['orderNumber', {rules: [{required: true, message: '请输入制作单号!'}]}]"
                placeholder="请输入制作单号"
                :disabled="action === 'view'">
                <a-button slot="addonAfter" @click="generateOrderNumber" :disabled="action === 'view'">
                  生成
                </a-button>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item label="制作类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select 
                v-decorator="['productionType', {rules: [{required: true, message: '请选择制作类型!'}]}]"
                placeholder="请选择制作类型"
                @change="onProductionTypeChange"
                :disabled="action === 'view'">
                <a-select-option value="ORDER_DRIVEN">订单驱动制作</a-select-option>
                <a-select-option value="STOCK_DRIVEN">库存驱动制作</a-select-option>
                <a-select-option value="PLAN_DRIVEN">自主计划制作</a-select-option>
                <a-select-option value="URGENT">紧急制作</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item label="配饰类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select 
                v-decorator="['accessoryType', {rules: [{required: true, message: '请选择配饰类型!'}]}]"
                placeholder="请选择配饰类型"
                @change="onAccessoryTypeChange"
                :disabled="action === 'view'">
                <a-select-option value="NECKLACE">项链</a-select-option>
                <a-select-option value="BRACELET">手镯</a-select-option>
                <a-select-option value="EARRING">耳环</a-select-option>
                <a-select-option value="RING">戒指</a-select-option>
                <a-select-option value="PENDANT">吊坠</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item label="优先级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select 
                v-decorator="['priority', {initialValue: 'NORMAL'}]"
                placeholder="请选择优先级"
                :disabled="action === 'view'">
                <a-select-option value="LOW">低优先级</a-select-option>
                <a-select-option value="NORMAL">普通优先级</a-select-option>
                <a-select-option value="HIGH">高优先级</a-select-option>
                <a-select-option value="URGENT">紧急优先级</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item label="关联订单" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select 
                v-decorator="['orderId']"
                placeholder="请选择关联订单（可选）"
                allowClear
                :disabled="action === 'view' || !isOrderDriven">
                <a-select-option v-for="order in orderList" :key="order.id" :value="order.id">
                  {{ order.orderNumber }} - {{ order.customerName }}
                </a-select-option>
              </a-select>
              <div v-if="!isOrderDriven" style="font-size: 12px; color: #999; margin-top: 4px;">
                非订单驱动制作，可不关联订单
              </div>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item label="制作原因" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select 
                v-decorator="['productionReason', {rules: [{required: true, message: '请选择制作原因!'}]}]"
                placeholder="请选择制作原因"
                :disabled="action === 'view'">
                <template v-if="isOrderDriven">
                  <a-select-option value="CUSTOMER_ORDER">客户订单需求</a-select-option>
                  <a-select-option value="URGENT_ORDER">紧急订单补货</a-select-option>
                </template>
                <template v-if="isStockDriven">
                  <a-select-option value="STOCK_REPLENISH">库存补充</a-select-option>
                  <a-select-option value="SAFETY_STOCK">安全库存备货</a-select-option>
                  <a-select-option value="SEASONAL_STOCK">季节性备货</a-select-option>
                </template>
                <template v-if="isPlanDriven">
                  <a-select-option value="NEW_PRODUCT">新产品试制</a-select-option>
                  <a-select-option value="PROCESS_IMPROVE">工艺改进试验</a-select-option>
                  <a-select-option value="MARKET_FORECAST">市场预测备货</a-select-option>
                </template>
                <template v-if="isUrgent">
                  <a-select-option value="SAMPLE_MAKING">样品制作</a-select-option>
                  <a-select-option value="EXHIBITION">展示用品制作</a-select-option>
                  <a-select-option value="EMERGENCY">应急补货</a-select-option>
                </template>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <!-- 半成品选择区域 -->
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="半成品选择" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
              <a-row :gutter="8">
                <a-col :span="18">
                  <a-select 
                    v-decorator="['semiProductId', {rules: [{required: true, message: '请选择半成品!'}]}]"
                    placeholder="请选择半成品"
                    showSearch
                    optionFilterProp="children"
                    @change="onSemiProductChange"
                    :disabled="action === 'view'">
                    <a-select-option v-for="product in semiProductList" :key="product.id" :value="product.id">
                      <div style="display: flex; align-items: center;">
                        <img v-if="product.imgName" :src="product.imgName" style="width: 20px; height: 20px; margin-right: 8px; object-fit: cover;" />
                        <span>{{ product.name }} - {{ product.standard }}</span>
                      </div>
                    </a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="6">
                  <a-button @click="showSemiProductSelector" :disabled="action === 'view'">选择半成品</a-button>
                </a-col>
              </a-row>
            </a-form-item>
          </a-col>
        </a-row>
        
        <!-- 半成品预览 -->
        <a-row :gutter="24" v-if="selectedSemiProduct">
          <a-col :md="24" :sm="24">
            <a-card size="small" title="半成品信息">
              <div style="display: flex; align-items: center;">
                <img v-if="selectedSemiProduct.imgName" :src="selectedSemiProduct.imgName" 
                     style="width: 80px; height: 80px; margin-right: 16px; object-fit: cover; border-radius: 4px;" />
                <div>
                  <p><strong>名称：</strong>{{ selectedSemiProduct.name }}</p>
                  <p><strong>规格：</strong>{{ selectedSemiProduct.standard }}</p>
                  <p><strong>当前库存：</strong>{{ selectedSemiProduct.stock }} {{ selectedSemiProduct.unit }}</p>
                  <p><strong>成本价：</strong>¥{{ selectedSemiProduct.costPrice }}</p>
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
        
        <!-- 配饰材料选择 -->
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="配饰材料" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
              <a-select 
                v-decorator="['accessoryMaterials']"
                mode="multiple"
                placeholder="请选择配饰材料（可多选）"
                :disabled="action === 'view'">
                <a-select-option v-for="material in accessoryMaterialList" :key="material.id" :value="material.id">
                  {{ material.name }} - {{ material.specification }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item label="制作数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number 
                v-decorator="['quantity', {rules: [{required: true, message: '请输入制作数量!'}]}]"
                placeholder="请输入制作数量"
                :min="1"
                :precision="0"
                style="width: 100%"
                @change="calculateSalary"
                :disabled="action === 'view'" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select 
                v-decorator="['unitId']"
                placeholder="请选择单位"
                :disabled="action === 'view'">
                <a-select-option v-for="unit in unitList" :key="unit.id" :value="unit.id">
                  {{ unit.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="库存检查" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-tag v-if="stockCheckResult === 'sufficient'" color="green">库存充足</a-tag>
              <a-tag v-if="stockCheckResult === 'insufficient'" color="red">库存不足</a-tag>
              <a-tag v-if="stockCheckResult === 'unknown'" color="orange">待检查</a-tag>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item label="制作工艺师" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select 
                v-decorator="['craftsmanId']"
                placeholder="请选择制作工艺师"
                @change="onCraftsmanChange"
                :disabled="action === 'view'">
                <a-select-option v-for="craftsman in craftsmanList" :key="craftsman.id" :value="craftsman.id">
                  {{ craftsman.name }} - {{ craftsman.specialty }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item label="薪酬金额" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number 
                v-decorator="['salaryAmount']"
                placeholder="薪酬金额"
                :min="0"
                :precision="2"
                style="width: 100%"
                :disabled="action === 'view'" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="制作要求" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
              <a-textarea 
                v-decorator="['requirements']"
                placeholder="请输入制作要求和注意事项"
                :rows="3"
                :disabled="action === 'view'" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="备注" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
              <a-textarea 
                v-decorator="['remark']"
                placeholder="请输入备注信息"
                :rows="2"
                :disabled="action === 'view'" />
            </a-form-item>
          </a-col>
        </a-row>
        
      </a-form>
    </a-spin>
    
  </a-modal>
</template>

<script>
export default {
  name: "AccessoryProductionModal",
  data() {
    return {
      title: "",
      visible: false,
      action: "",
      confirmLoading: false,
      form: this.$form.createForm(this),
      labelCol: {
        span: 6
      },
      wrapperCol: {
        span: 16
      },
      model: {},
      orderList: [],
      semiProductList: [],
      accessoryMaterialList: [],
      unitList: [],
      craftsmanList: [],
      selectedSemiProduct: null,
      stockCheckResult: 'unknown',
      currentProductionType: '',
      currentAccessoryType: ''
    }
  },
  computed: {
    isOrderDriven() {
      return this.currentProductionType === 'ORDER_DRIVEN';
    },
    isStockDriven() {
      return this.currentProductionType === 'STOCK_DRIVEN';
    },
    isPlanDriven() {
      return this.currentProductionType === 'PLAN_DRIVEN';
    },
    isUrgent() {
      return this.currentProductionType === 'URGENT';
    }
  },
  methods: {
    add() {
      this.visible = true;
      this.action = "add";
      this.loadInitData();
      this.$nextTick(() => {
        this.form.resetFields();
      });
    },
    edit(record) {
      this.visible = true;
      this.model = Object.assign({}, record);
      this.loadInitData();
      this.$nextTick(() => {
        this.form.setFieldsValue(this.model);
        if (this.model.semiProductId) {
          this.loadSemiProductInfo(this.model.semiProductId);
        }
      });
    },
    close() {
      this.visible = false;
      this.form.resetFields();
      this.selectedSemiProduct = null;
      this.stockCheckResult = 'unknown';
      this.currentProductionType = '';
      this.currentAccessoryType = '';
    },
    handleOk() {
      if (this.action === 'view') {
        this.close();
        return;
      }
      
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true;
          let formData = Object.assign({}, values);
          
          // TODO: 调用保存API
          console.log('保存数据:', formData);
          
          setTimeout(() => {
            this.confirmLoading = false;
            this.$message.success('保存成功！');
            this.$emit('ok');
            this.close();
          }, 1000);
        }
      });
    },
    handleCancel() {
      this.close();
    },
    loadInitData() {
      this.loadOrderList();
      this.loadSemiProductList();
      this.loadAccessoryMaterialList();
      this.loadUnitList();
      this.loadCraftsmanList();
    },
    loadOrderList() {
      // 模拟订单列表
      this.orderList = [
        { id: 1, orderNumber: 'SO202506220001', customerName: '北京艺术品公司' },
        { id: 2, orderNumber: 'SO202506220002', customerName: '上海工艺品商行' },
        { id: 3, orderNumber: 'SO202506220003', customerName: '广州文化用品店' }
      ];
    },
    loadSemiProductList() {
      // 模拟半成品列表
      this.semiProductList = [
        { 
          id: 1, 
          name: '掐丝点蓝花瓶-半成品', 
          standard: '高30cm 已完成掐丝',
          imgName: 'https://via.placeholder.com/80x80/4A90E2/FFFFFF?text=花瓶',
          stock: 20,
          unit: '个',
          costPrice: 200.00
        },
        { 
          id: 2, 
          name: '掐丝点蓝盘子-半成品', 
          standard: '直径25cm 已完成掐丝',
          imgName: 'https://via.placeholder.com/80x80/50C878/FFFFFF?text=盘子',
          stock: 30,
          unit: '个',
          costPrice: 150.00
        },
        { 
          id: 3, 
          name: '掐丝点蓝茶具-半成品', 
          standard: '一壶四杯 已完成掐丝',
          imgName: 'https://via.placeholder.com/80x80/FF6B6B/FFFFFF?text=茶具',
          stock: 10,
          unit: '套',
          costPrice: 500.00
        }
      ];
    },
    loadAccessoryMaterialList() {
      // 模拟配饰材料列表
      this.accessoryMaterialList = [
        { id: 1, name: '银链条', specification: '925银 2mm粗' },
        { id: 2, name: '金链条', specification: '18K金 1.5mm粗' },
        { id: 3, name: '皮绳', specification: '真皮 黑色 3mm粗' },
        { id: 4, name: '水晶珠', specification: '透明水晶 6mm直径' },
        { id: 5, name: '银扣', specification: '925银 龙虾扣' }
      ];
    },
    loadUnitList() {
      // 模拟单位列表
      this.unitList = [
        { id: 1, name: '个' },
        { id: 2, name: '条' },
        { id: 3, name: '对' },
        { id: 4, name: '套' }
      ];
    },
    loadCraftsmanList() {
      // 模拟工艺师列表
      this.craftsmanList = [
        { id: 1, name: '李师傅', specialty: '项链制作专家', hourlyRate: 80.00 },
        { id: 2, name: '王师傅', specialty: '手镯制作专家', hourlyRate: 75.00 },
        { id: 3, name: '张师傅', specialty: '耳环制作专家', hourlyRate: 70.00 },
        { id: 4, name: '赵师傅', specialty: '戒指制作专家', hourlyRate: 85.00 }
      ];
    },
    generateOrderNumber() {
      const timestamp = new Date().getTime();
      let prefix = 'AC'; // Accessory
      
      switch(this.currentProductionType) {
        case 'ORDER_DRIVEN':
          prefix = 'ACO'; // Accessory Order
          break;
        case 'STOCK_DRIVEN':
          prefix = 'ACS'; // Accessory Stock
          break;
        case 'PLAN_DRIVEN':
          prefix = 'ACP'; // Accessory Plan
          break;
        case 'URGENT':
          prefix = 'ACU'; // Accessory Urgent
          break;
      }
      
      const orderNumber = prefix + timestamp;
      this.form.setFieldsValue({ orderNumber: orderNumber });
    },
    onProductionTypeChange(productionType) {
      this.currentProductionType = productionType;
      this.form.setFieldsValue({ productionReason: undefined });
      
      if (productionType !== 'ORDER_DRIVEN') {
        this.form.setFieldsValue({ orderId: undefined });
      }
      
      let defaultPriority = 'NORMAL';
      if (productionType === 'URGENT') {
        defaultPriority = 'URGENT';
      } else if (productionType === 'ORDER_DRIVEN') {
        defaultPriority = 'HIGH';
      }
      this.form.setFieldsValue({ priority: defaultPriority });
      
      if (this.form.getFieldValue('orderNumber')) {
        this.generateOrderNumber();
      }
    },
    onAccessoryTypeChange(accessoryType) {
      this.currentAccessoryType = accessoryType;
    },
    onSemiProductChange(semiProductId) {
      if (semiProductId) {
        this.loadSemiProductInfo(semiProductId);
      } else {
        this.selectedSemiProduct = null;
        this.stockCheckResult = 'unknown';
      }
    },
    loadSemiProductInfo(semiProductId) {
      const semiProduct = this.semiProductList.find(item => item.id === semiProductId);
      if (semiProduct) {
        this.selectedSemiProduct = { ...semiProduct };
        this.checkStock();
      }
    },
    onCraftsmanChange(craftsmanId) {
      const craftsman = this.craftsmanList.find(item => item.id === craftsmanId);
      if (craftsman) {
        this.calculateSalary();
      }
    },
    calculateSalary() {
      const quantity = this.form.getFieldValue('quantity');
      const craftsmanId = this.form.getFieldValue('craftsmanId');
      
      if (quantity && craftsmanId) {
        const craftsman = this.craftsmanList.find(item => item.id === craftsmanId);
        if (craftsman) {
          // 简单计算：数量 * 工时系数 * 时薪
          const workHours = this.getWorkHoursByAccessoryType();
          const salaryAmount = quantity * workHours * craftsman.hourlyRate;
          this.form.setFieldsValue({ salaryAmount: salaryAmount });
        }
      }
    },
    getWorkHoursByAccessoryType() {
      // 根据配饰类型返回预估工时
      const accessoryType = this.form.getFieldValue('accessoryType');
      const workHoursMap = {
        'NECKLACE': 4,    // 项链 4小时
        'BRACELET': 3,    // 手镯 3小时
        'EARRING': 2,     // 耳环 2小时
        'RING': 2.5,      // 戒指 2.5小时
        'PENDANT': 3.5    // 吊坠 3.5小时
      };
      return workHoursMap[accessoryType] || 3;
    },
    checkStock() {
      const quantity = this.form.getFieldValue('quantity');
      if (this.selectedSemiProduct && quantity) {
        if (quantity <= this.selectedSemiProduct.stock) {
          this.stockCheckResult = 'sufficient';
        } else {
          this.stockCheckResult = 'insufficient';
        }
      } else {
        this.stockCheckResult = 'unknown';
      }
    },
    showSemiProductSelector() {
      this.$message.info('半成品选择器功能开发中...');
    }
  }
}
</script>

<style scoped>
.ant-form-item {
  margin-bottom: 16px;
}
</style>
