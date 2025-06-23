<template>
  <a-modal
    :title="title"
    :width="1000"
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
          <a-col :md="24" :sm="24">
            <a-form-item label="制作原因" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
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
        
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="原材料选择" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
              <a-row :gutter="8">
                <a-col :span="18">
                  <a-select 
                    v-decorator="['materialId', {rules: [{required: true, message: '请选择原材料!'}]}]"
                    placeholder="请选择原材料"
                    showSearch
                    optionFilterProp="children"
                    @change="onMaterialChange"
                    :disabled="action === 'view'">
                    <a-select-option v-for="material in materialList" :key="material.id" :value="material.id">
                      <div style="display: flex; align-items: center;">
                        <img v-if="material.imgName" :src="material.imgName" style="width: 20px; height: 20px; margin-right: 8px; object-fit: cover;" />
                        <span>{{ material.name }} - {{ material.standard }}</span>
                      </div>
                    </a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="6">
                  <a-button @click="showMaterialSelector" :disabled="action === 'view'">选择原材料</a-button>
                </a-col>
              </a-row>
            </a-form-item>
          </a-col>
        </a-row>
        
        <!-- 原材料预览 -->
        <a-row :gutter="24" v-if="selectedMaterial">
          <a-col :md="24" :sm="24">
            <a-card size="small" title="原材料信息">
              <div style="display: flex; align-items: center;">
                <img v-if="selectedMaterial.imgName" :src="selectedMaterial.imgName" 
                     style="width: 80px; height: 80px; margin-right: 16px; object-fit: cover; border-radius: 4px;" />
                <div>
                  <p><strong>名称：</strong>{{ selectedMaterial.name }}</p>
                  <p><strong>规格：</strong>{{ selectedMaterial.standard }}</p>
                  <p><strong>当前库存：</strong>{{ selectedMaterial.stock }} {{ selectedMaterial.unit }}</p>
                  <p><strong>采购价：</strong>¥{{ selectedMaterial.purchasePrice }}</p>
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item label="制作数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number 
                v-decorator="['quantity', {rules: [{required: true, message: '请输入制作数量!'}]}]"
                placeholder="请输入制作数量"
                :min="0.01"
                :precision="2"
                style="width: 100%"
                @change="checkMaterialStock"
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
            <a-form-item label="工费产品" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select 
                v-decorator="['laborCostId']"
                placeholder="请选择工费产品"
                @change="onLaborCostChange"
                :disabled="action === 'view'">
                <a-select-option v-for="labor in laborCostList" :key="labor.id" :value="labor.id">
                  {{ labor.name }} - ¥{{ labor.price }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item label="工费金额" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number 
                v-decorator="['laborCostAmount']"
                placeholder="工费金额"
                :min="0"
                :precision="2"
                style="width: 100%"
                :disabled="action === 'view'" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="供应商" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
              <a-select 
                v-decorator="['supplierId']"
                placeholder="请选择供应商"
                showSearch
                optionFilterProp="children"
                :disabled="action === 'view'">
                <a-select-option v-for="supplier in supplierList" :key="supplier.id" :value="supplier.id">
                  {{ supplier.name }} - {{ supplier.contact }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="备注" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
              <a-textarea 
                v-decorator="['remark']"
                placeholder="请输入备注信息"
                :rows="3"
                :disabled="action === 'view'" />
            </a-form-item>
          </a-col>
        </a-row>
        
      </a-form>
    </a-spin>
    
    <!-- 原材料选择器模态框 - 暂时注释掉 -->
    <!-- <material-selector-modal
      ref="materialSelectorModal"
      @ok="onMaterialSelected">
    </material-selector-modal> -->
    
  </a-modal>
</template>

<script>
export default {
  name: "CloisonneProductionModal",
  components: {
  },
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
      materialList: [],
      unitList: [],
      laborCostList: [],
      supplierList: [],
      selectedMaterial: null,
      stockCheckResult: 'unknown', // sufficient, insufficient, unknown
      currentProductionType: '' // 当前选择的制作类型
    }
  },
  computed: {
    // 是否为订单驱动制作
    isOrderDriven() {
      return this.currentProductionType === 'ORDER_DRIVEN';
    },
    // 是否为库存驱动制作
    isStockDriven() {
      return this.currentProductionType === 'STOCK_DRIVEN';
    },
    // 是否为计划驱动制作
    isPlanDriven() {
      return this.currentProductionType === 'PLAN_DRIVEN';
    },
    // 是否为紧急制作
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
        if (this.model.materialId) {
          this.loadMaterialInfo(this.model.materialId);
        }
      });
    },
    close() {
      this.visible = false;
      this.form.resetFields();
      this.selectedMaterial = null;
      this.stockCheckResult = 'unknown';
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
      // 加载模拟数据用于演示
      this.loadOrderList();
      this.loadMaterialList();
      this.loadUnitList();
      this.loadLaborCostList();
      this.loadSupplierList();
    },
    loadOrderList() {
      // 模拟订单列表
      this.orderList = [
        { id: 1, orderNumber: 'SO202506220001', customerName: '北京艺术品公司' },
        { id: 2, orderNumber: 'SO202506220002', customerName: '上海工艺品商行' },
        { id: 3, orderNumber: 'SO202506220003', customerName: '广州文化用品店' }
      ];
    },
    loadMaterialList() {
      // 模拟原材料列表
      this.materialList = [
        {
          id: 1,
          name: '景泰蓝底胎-花瓶',
          standard: '高30cm 直径15cm',
          imgName: 'https://via.placeholder.com/80x80/4A90E2/FFFFFF?text=花瓶',
          stock: 50,
          unit: '个',
          purchasePrice: 120.00
        },
        {
          id: 2,
          name: '景泰蓝底胎-盘子',
          standard: '直径25cm',
          imgName: 'https://via.placeholder.com/80x80/50C878/FFFFFF?text=盘子',
          stock: 80,
          unit: '个',
          purchasePrice: 80.00
        },
        {
          id: 3,
          name: '景泰蓝底胎-茶具',
          standard: '一壶四杯',
          imgName: 'https://via.placeholder.com/80x80/FF6B6B/FFFFFF?text=茶具',
          stock: 20,
          unit: '套',
          purchasePrice: 300.00
        }
      ];
    },
    loadUnitList() {
      // 模拟单位列表
      this.unitList = [
        { id: 1, name: '个' },
        { id: 2, name: '套' },
        { id: 3, name: '件' },
        { id: 4, name: '对' }
      ];
    },
    loadLaborCostList() {
      // 模拟工费产品列表
      this.laborCostList = [
        { id: 1, name: '掐丝工费-花瓶', price: 300.00 },
        { id: 2, name: '掐丝工费-盘子', price: 200.00 },
        { id: 3, name: '掐丝工费-茶具', price: 1000.00 },
        { id: 4, name: '点蓝工费-标准', price: 150.00 }
      ];
    },
    loadSupplierList() {
      // 模拟供应商列表
      this.supplierList = [
        { id: 1, name: '北京景泰蓝工艺厂', contact: '张师傅 13800138001' },
        { id: 2, name: '天津工艺品厂', contact: '李经理 13800138002' },
        { id: 3, name: '河北工艺美术厂', contact: '王总 13800138003' }
      ];
    },
    generateOrderNumber() {
      // 根据制作类型生成不同前缀的制作单号
      const timestamp = new Date().getTime();
      let prefix = 'CLS'; // 默认前缀

      switch(this.currentProductionType) {
        case 'ORDER_DRIVEN':
          prefix = 'CLO'; // Cloisonne Order
          break;
        case 'STOCK_DRIVEN':
          prefix = 'CLS'; // Cloisonne Stock
          break;
        case 'PLAN_DRIVEN':
          prefix = 'CLP'; // Cloisonne Plan
          break;
        case 'URGENT':
          prefix = 'CLU'; // Cloisonne Urgent
          break;
      }

      const orderNumber = prefix + timestamp;
      this.form.setFieldsValue({ orderNumber: orderNumber });
    },
    onProductionTypeChange(productionType) {
      this.currentProductionType = productionType;

      // 清空制作原因，让用户重新选择
      this.form.setFieldsValue({ productionReason: undefined });

      // 如果不是订单驱动，清空关联订单
      if (productionType !== 'ORDER_DRIVEN') {
        this.form.setFieldsValue({ orderId: undefined });
      }

      // 根据制作类型设置默认优先级
      let defaultPriority = 'NORMAL';
      if (productionType === 'URGENT') {
        defaultPriority = 'URGENT';
      } else if (productionType === 'ORDER_DRIVEN') {
        defaultPriority = 'HIGH';
      }
      this.form.setFieldsValue({ priority: defaultPriority });

      // 重新生成制作单号
      if (this.form.getFieldValue('orderNumber')) {
        this.generateOrderNumber();
      }
    },
    onMaterialChange(materialId) {
      if (materialId) {
        this.loadMaterialInfo(materialId);
      } else {
        this.selectedMaterial = null;
        this.stockCheckResult = 'unknown';
      }
    },
    loadMaterialInfo(materialId) {
      // 从材料列表中查找选中的材料
      const material = this.materialList.find(item => item.id === materialId);
      if (material) {
        this.selectedMaterial = { ...material };
        // 检查库存
        this.checkMaterialStock();
      }
    },
    onLaborCostChange(laborCostId) {
      const laborCost = this.laborCostList.find(item => item.id === laborCostId);
      if (laborCost) {
        this.form.setFieldsValue({ laborCostAmount: laborCost.price });
      }
    },
    checkMaterialStock() {
      const quantity = this.form.getFieldValue('quantity');
      if (this.selectedMaterial && quantity) {
        if (quantity <= this.selectedMaterial.stock) {
          this.stockCheckResult = 'sufficient';
        } else {
          this.stockCheckResult = 'insufficient';
        }
      } else {
        this.stockCheckResult = 'unknown';
      }
    },
    showMaterialSelector() {
      // TODO: 实现原材料选择器
      this.$message.info('原材料选择器功能开发中...');
    },
    onMaterialSelected(material) {
      this.form.setFieldsValue({ materialId: material.id });
      this.onMaterialChange(material.id);
    }
  }
}
</script>

<style scoped>
.ant-form-item {
  margin-bottom: 16px;
}
</style>
